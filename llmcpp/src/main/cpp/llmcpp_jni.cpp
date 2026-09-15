#include <jni.h>

#include "llama.h"

#include <cstdint>
#include <cstring>
#include <memory>
#include <mutex>
#include <string>
#include <unordered_map>
#include <vector>

namespace {

struct LlmCppSession {
    std::string modelPath;
    llama_model * model;
    llama_context * context;
    const llama_vocab * vocab;
    llama_sampler * sampler;
    std::mutex generationMutex;

    LlmCppSession(
            std::string modelPath,
            llama_model * model,
            llama_context * context,
            const llama_vocab * vocab,
            llama_sampler * sampler
    ) :
            modelPath(std::move(modelPath)),
            model(model),
            context(context),
            vocab(vocab),
            sampler(sampler) {}

    ~LlmCppSession() {
        if (sampler != nullptr) {
            llama_sampler_free(sampler);
        }

        if (context != nullptr) {
            llama_free(context);
        }

        if (model != nullptr) {
            llama_model_free(model);
        }
    }
};

std::mutex sessionMutex;
std::unordered_map<std::int64_t, std::shared_ptr<LlmCppSession>> sessionMap;
std::int64_t nextSessionHandle = 1;
std::once_flag backendInitFlag;

const char * DEFAULT_DTO_GRAMMAR = R"gbnf(
root ::= "{" space "\"context\"" space ":" space string space "}"
string ::= "\"" char{1,512} "\""
char ::= [^"\\\x7F\x00-\x1F] | "\\" (["\\/bfnrt] | "u" hex hex hex hex)
hex ::= [0-9a-fA-F]
space ::= [ \t\n\r]*
)gbnf";

void ensureBackendInitialized() {
    std::call_once(backendInitFlag, [] {
        llama_backend_init();
    });
}

std::string copyJString(JNIEnv *env, jstring value) {
    if (value == nullptr) {
        return "";
    }

    const char *rawValue = env->GetStringUTFChars(value, nullptr);
    if (rawValue == nullptr) {
        return "";
    }

    std::string copiedValue(rawValue);
    env->ReleaseStringUTFChars(value, rawValue);
    return copiedValue;
}

llama_sampler * createDefaultDtoSampler(
        const llama_vocab * vocab,
        float temperature
) {
    llama_sampler * sampler = llama_sampler_chain_init(llama_sampler_chain_default_params());
    llama_sampler * grammarSampler = llama_sampler_init_grammar(
            vocab,
            DEFAULT_DTO_GRAMMAR,
            "root"
    );

    if (grammarSampler == nullptr) {
        llama_sampler_free(sampler);
        return nullptr;
    }

    llama_sampler_chain_add(sampler, grammarSampler);
    llama_sampler_chain_add(sampler, llama_sampler_init_temp(temperature));
    llama_sampler_chain_add(sampler, llama_sampler_init_dist(LLAMA_DEFAULT_SEED));
    return sampler;
}

std::vector<llama_token> tokenizePrompt(
        const llama_vocab * vocab,
        const std::string & prompt
) {
    const int tokenCount = -llama_tokenize(
            vocab,
            prompt.c_str(),
            static_cast<int32_t>(prompt.size()),
            nullptr,
            0,
            true,
            true
    );

    if (tokenCount <= 0) {
        return {};
    }

    std::vector<llama_token> promptTokens(static_cast<std::size_t>(tokenCount));
    const int actualTokenCount = llama_tokenize(
            vocab,
            prompt.c_str(),
            static_cast<int32_t>(prompt.size()),
            promptTokens.data(),
            tokenCount,
            true,
            true
    );

    if (actualTokenCount < 0) {
        return {};
    }

    promptTokens.resize(static_cast<std::size_t>(actualTokenCount));
    return promptTokens;
}

std::string convertTokenToPiece(
        const llama_vocab * vocab,
        llama_token token
) {
    char smallBuffer[256];
    const int smallBufferSize = llama_token_to_piece(
            vocab,
            token,
            smallBuffer,
            sizeof(smallBuffer),
            0,
            true
    );

    if (smallBufferSize >= 0) {
        return std::string(smallBuffer, static_cast<std::size_t>(smallBufferSize));
    }

    const int requiredBufferSize = -smallBufferSize;
    std::vector<char> dynamicBuffer(static_cast<std::size_t>(requiredBufferSize));
    const int dynamicBufferSize = llama_token_to_piece(
            vocab,
            token,
            dynamicBuffer.data(),
            requiredBufferSize,
            0,
            true
    );

    if (dynamicBufferSize < 0) {
        return "";
    }

    return std::string(dynamicBuffer.data(), static_cast<std::size_t>(dynamicBufferSize));
}

std::vector<std::string> generateTokenList(
        LlmCppSession * session,
        const std::string & prompt,
        int maxTokenCount
) {
    std::vector<std::string> tokenPieceList;
    if (session == nullptr || session->context == nullptr || session->vocab == nullptr || session->sampler == nullptr) {
        return tokenPieceList;
    }

    llama_memory_clear(llama_get_memory(session->context), true);
    llama_sampler_reset(session->sampler);

    std::vector<llama_token> promptTokens = tokenizePrompt(session->vocab, prompt);
    if (promptTokens.empty()) {
        return tokenPieceList;
    }

    llama_batch batch = llama_batch_get_one(
            promptTokens.data(),
            static_cast<int32_t>(promptTokens.size())
    );

    const int generationLimit = maxTokenCount > 0 ? maxTokenCount : 1;

    for (int generatedTokenCount = 0; generatedTokenCount < generationLimit; generatedTokenCount++) {
        if (llama_decode(session->context, batch) != 0) {
            break;
        }

        const llama_token token = llama_sampler_sample(session->sampler, session->context, -1);
        if (llama_vocab_is_eog(session->vocab, token)) {
            break;
        }

        const std::string tokenPiece = convertTokenToPiece(session->vocab, token);
        if (!tokenPiece.empty()) {
            tokenPieceList.push_back(tokenPiece);
        }

        batch = llama_batch_get_one(
                const_cast<llama_token *>(&token),
                1
        );
    }

    return tokenPieceList;
}

jobjectArray createJavaStringArray(
        JNIEnv *env,
        const std::vector<std::string> &tokenList
) {
    jclass stringClass = env->FindClass("java/lang/String");
    jobjectArray javaTokenArray = env->NewObjectArray(
            static_cast<jsize>(tokenList.size()),
            stringClass,
            nullptr
    );

    for (jsize tokenIndex = 0; tokenIndex < static_cast<jsize>(tokenList.size()); tokenIndex++) {
        jstring javaToken = env->NewStringUTF(tokenList[tokenIndex].c_str());
        env->SetObjectArrayElement(javaTokenArray, tokenIndex, javaToken);
        env->DeleteLocalRef(javaToken);
    }

    return javaTokenArray;
}

} // namespace

extern "C"
JNIEXPORT jlong JNICALL
Java_com_baidaidai_rootless_1store_llmcpp_LlmCppNativeBridge_loadModel(
        JNIEnv *env,
        jobject,
        jstring modelPath,
        jint contextSize,
        jint threadCount
) {
    ensureBackendInitialized();

    const std::string copiedModelPath = copyJString(env, modelPath);
    if (copiedModelPath.empty()) {
        return 0;
    }

    llama_model_params modelParams = llama_model_default_params();
    modelParams.n_gpu_layers = 0;

    llama_model * model = llama_model_load_from_file(copiedModelPath.c_str(), modelParams);
    if (model == nullptr) {
        return 0;
    }

    llama_context_params contextParams = llama_context_default_params();
    contextParams.n_ctx = contextSize > 0 ? static_cast<uint32_t>(contextSize) : 2048;
    contextParams.n_batch = contextParams.n_ctx;
    contextParams.n_ubatch = contextParams.n_ctx;
    contextParams.n_threads = threadCount > 0 ? threadCount : 4;
    contextParams.n_threads_batch = contextParams.n_threads;
    contextParams.no_perf = true;

    llama_context * context = llama_init_from_model(model, contextParams);
    if (context == nullptr) {
        llama_model_free(model);
        return 0;
    }

    const llama_vocab * vocab = llama_model_get_vocab(model);
    llama_sampler * sampler = createDefaultDtoSampler(vocab, 0.8f);
    if (sampler == nullptr) {
        llama_free(context);
        llama_model_free(model);
        return 0;
    }

    auto session = std::make_shared<LlmCppSession>(
            copiedModelPath,
            model,
            context,
            vocab,
            sampler
    );

    std::lock_guard<std::mutex> lock(sessionMutex);
    const std::int64_t sessionHandle = nextSessionHandle++;
    sessionMap[sessionHandle] = session;

    return static_cast<jlong>(sessionHandle);
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_baidaidai_rootless_1store_llmcpp_LlmCppNativeBridge_generate(
        JNIEnv *env,
        jobject,
        jlong nativeHandle,
        jstring prompt,
        jint maxTokenCount,
        jfloat temperature
) {
    const std::string copiedPrompt = copyJString(env, prompt);

    std::shared_ptr<LlmCppSession> session;
    {
        std::lock_guard<std::mutex> lock(sessionMutex);
        const auto sessionIterator = sessionMap.find(static_cast<std::int64_t>(nativeHandle));
        if (sessionIterator == sessionMap.end()) {
            return createJavaStringArray(env, {});
        }
        session = sessionIterator->second;
    }

    std::lock_guard<std::mutex> generationLock(session->generationMutex);

    if (session->sampler != nullptr) {
        llama_sampler_free(session->sampler);
    }
    session->sampler = createDefaultDtoSampler(session->vocab, temperature);
    if (session->sampler == nullptr) {
        return createJavaStringArray(env, {});
    }

    const std::vector<std::string> tokenList = generateTokenList(
            session.get(),
            copiedPrompt,
            maxTokenCount
    );
    return createJavaStringArray(env, tokenList);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_baidaidai_rootless_1store_llmcpp_LlmCppNativeBridge_unloadModel(
        JNIEnv *,
        jobject,
        jlong nativeHandle
) {
    std::lock_guard<std::mutex> lock(sessionMutex);
    const auto sessionIterator = sessionMap.find(static_cast<std::int64_t>(nativeHandle));
    if (sessionIterator != sessionMap.end()) {
        sessionMap.erase(sessionIterator);
    }
}
