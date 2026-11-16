package com.baidaidai.rootless_store.data.assistant.api

import android.content.Context
import android.util.Log
import com.baidaidai.rootless_store.data.R
import com.baidaidai.rootless_store.core.util.formatAsMultilineString
import com.baidaidai.rootless_store.domain.fileSystem.gateway.AndroidFileSystemCreateOperatorGateway
import com.baidaidai.rootless_store.domain.fileSystem.gateway.AndroidFileSystemDefaultOperatorGateway
import com.baidaidai.rootless_store.domain.fileSystem.gateway.AndroidFileSystemSearchOperatorGateway
import com.baidaidai.rootless_store.data.assistant.api.dto.AssistantDto
import com.baidaidai.rootless_store.data.assistant.model.AssistantPrompts
import com.baidaidai.rootless_store.data.assistant.model.AssistantApi
import com.baidaidai.rootless_store.data.assistant.model.EngineManager
import com.baidaidai.rootless_store.domain.assistant.error.AssistantError
import com.baidaidai.rootless_store.domain.assistant.model.AssistantEngineStatus
import com.baidaidai.rootless_store.domain.assistant.model.AssistantJudgeStatus
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.onErr
import com.github.michaelbull.result.onOk
import com.github.michaelbull.result.runCatching
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Channel
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Contents
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import com.google.ai.edge.litertlm.Message
import com.google.ai.edge.litertlm.ResponseFormat
import com.google.ai.edge.litertlm.SamplerConfig
import com.google.ai.edge.litertlm.ThinkingConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import java.io.File
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Singleton
class LocallyLiteRTApi @Inject constructor(
    @ApplicationContext val context: Context,
    private val androidFileSystemDefaultOperatorGateway: AndroidFileSystemDefaultOperatorGateway,
    private val androidFileSystemCreateOperatorGateway: AndroidFileSystemCreateOperatorGateway,
    private val androidFileSystemSearchOperatorGateway: AndroidFileSystemSearchOperatorGateway,
): EngineManager(), AssistantApi {

    private companion object {
        private const val MODEL_FILE_NAME = "Qwen.litertlm"
        private val thinkBlockRegex = Regex("<think>[\\s\\S]*?</think>", RegexOption.DOT_MATCHES_ALL)

        fun stripThinkMarkers(text: String): String =
            text
                .replace(thinkBlockRegex, "")
                .replace("<think>", "")
                .replace("</think>", "")
                .trim()
    }

    @Volatile
    override var engine: Engine? = null

    @Synchronized
    override fun resolveUnmaskedResponse(
        prompt: String
    ): Result<String, AssistantError> {
        if (prompt.isBlank()) {
            return Err(
                AssistantError(
                    errorMessage = context.getString(R.string.assistant_api_prompt_required_title),
                    errorCompanion = context.getString(R.string.assistant_api_prompt_required_description)
                )
            )
        }

        if (engine == null) {
            initializeLlmEngine().getOrElse { assistantError ->
                return Err(assistantError)
            }
        }

        val engine = engine ?: return Err(
            AssistantError(
                errorMessage = context.getString(R.string.assistant_api_engine_unavailable_title),
                errorCompanion = context.getString(R.string.assistant_api_engine_unavailable_description)
            )
        )

        engine.createConversation(
            conversationConfig = ConversationConfig(
                systemInstruction = Contents.of(AssistantPrompts.UNMASKED_DEBUG_INSTRUCTION),
                samplerConfig = SamplerConfig(
                    topK = 1,
                    topP = 1.0,
                    temperature = 0.0,
                ),
                channels = listOf(
                    Channel(
                        channelName = "thought",
                        start = "<think>\n",
                        end = "\n</think>",
                    ),
                ),
                thinkingConfig = ThinkingConfig(
                    enableThinking = false,
                    thinkingTokenBudget = 0
                ),
                maxOutputToken = 128,
                enableResponseFormat = false
            )
        ).use { llmConversation ->

            Log.d(
                "JSON_SCHEMA",
                Json.encodeToString(AssistantDto.getShellCommandSchema())
            )

            val conversationMessage = llmConversation
                .sendMessage(
                    message = Message.user(prompt),
                    maxOutputToken = 128,
                    thinkingConfig = ThinkingConfig(
                        enableThinking = false,
                        thinkingTokenBudget = 0
                    )
                )

            val rawResponseJson = stripThinkMarkers(
                text = conversationMessage.toString()
            )

            return Ok(
                value = rawResponseJson
            )

        }
    }


    @Synchronized
    override fun resolveMaskedResponse(
        prompt: String
    ): Result<AssistantDto, AssistantError> {
        if (prompt.isBlank()) {
            return Err(
                AssistantError(
                    errorMessage = context.getString(R.string.assistant_api_prompt_required_title),
                    errorCompanion = context.getString(R.string.assistant_api_prompt_required_description)
                )
            )
        }

        if (engine == null) {
            initializeLlmEngine().getOrElse { assistantError ->
                return Err(assistantError)
            }
        }

        val engine = engine ?: return Err(
            AssistantError(
                errorMessage = context.getString(R.string.assistant_api_engine_unavailable_title),
                errorCompanion = context.getString(R.string.assistant_api_engine_unavailable_description)
            )
        )

        engine.createConversation(
            conversationConfig = ConversationConfig(
                systemInstruction = Contents.of(AssistantPrompts.SYSTEM_INSTRUCTION),
                samplerConfig = SamplerConfig(
                    topK = 1,
                    topP = 1.0,
                    temperature = 0.0,
                ),
                channels = listOf(
                    Channel(
                        channelName = "thought",
                        start = "<think>\n",
                        end = "\n</think>",
                    ),
                ),
                thinkingConfig = ThinkingConfig(
                    enableThinking = false,
                    thinkingTokenBudget = 0
                ),
                maxOutputToken = 128,
                enableResponseFormat = true,
            )
        ).use { llmConversation ->

            Log.d(
                "JSON_SCHEMA",
                Json.encodeToString(AssistantDto.getShellCommandSchema())
            )

            val conversationMessage = llmConversation
                .sendMessage(
                    message = Message.user(prompt),
                    maxOutputToken = 128,
                    thinkingConfig = ThinkingConfig(
                        enableThinking = false,
                        thinkingTokenBudget = 0
                    ),
                    responseFormat = ResponseFormat.json(
                        Json.encodeToString(
                            value = AssistantDto.getShellCommandSchema()
                        )
                    )
                )
            val rawResponseJson = stripThinkMarkers(
                text = conversationMessage.toString()
            )

            Log.d("LocallyLiteRTApi", "原始模型输出: $rawResponseJson")

            val assistantDto = Json
                .decodeFromString<AssistantDto>(
                    string = rawResponseJson
                )

            when {
                assistantDto.assistantJudgeStatus == AssistantJudgeStatus.illegal -> {
                    return Ok(
                        value = assistantDto.copy(shellCode = null)
                    )
                }
                assistantDto.shellCode.isNullOrBlank() -> {
                    return Ok(
                        value = AssistantDto(
                            assistantJudgeStatus = AssistantJudgeStatus.legal,
                            shellCode = null,
                        )
                    )

                }
                else -> {
                    return Ok(
                        assistantDto.copy(
                            shellCode = assistantDto
                                .shellCode
                                .trim()
                        )
                    )
                }
            }
        }
    }

    @Synchronized
    override fun closeLlmEngine() {
        engine?.close()
        engine = null
    }

    override fun observeEngineStatus(

    ): Flow<AssistantEngineStatus> =
        flow {
            while (true) {
                emit(
                    if (engine == null) {
                        AssistantEngineStatus.Released
                    } else {
                        AssistantEngineStatus.Working
                    }
                )
                delay(1_000L)
            }
        }

    @Synchronized
    override fun initializeLlmEngine(
    ): Result<Unit, AssistantError> {

            if (engine != null) {
                return Ok(Unit)
            }

            // 注意：模型包内嵌的聊天模板必须与运行时版本匹配。
            // - 0.18+ 运行时把消息 content 以「类型化片段」列表传给模板；
            // - 0.16.x 运行时传的是普通字符串。
            // 当前使用 litertlm-android 0.19.1 + 新格式模型包，二者匹配，无需覆盖模板。

            val modelFile = requireExternalModelFile()
                .getOrElse { return Err(it) }
            val cacheDirectory = File(context.cacheDir, "litertlm")
            check(cacheDirectory.isDirectory || cacheDirectory.mkdirs()) {
                "无法创建 LiteRT-LM 缓存目录：${cacheDirectory.absolutePath}"
            }

            initializeGpuEngine(
                modelFile = modelFile,
                cacheDirectory = cacheDirectory
            ).onOk {
                return Ok( it)
            }

            Log.w("LiteRTGateway", "GPU 初始化失败，尝试使用 CPU 后端。")

            return initializeCpuEngine(
                modelFile = modelFile,
                cacheDirectory = cacheDirectory
            ).onOk {
                Ok( it)
            }.onErr {
                Err(it)
            }

    }

    internal fun initializeGpuEngine(
        modelFile: File,
        cacheDirectory: File
    ): Result<Unit, AssistantError> =
        runCatching {
            val _engine = Engine(
                EngineConfig(
                    modelPath = modelFile.absolutePath,
                    backend = Backend.GPU(),
                    maxNumTokens = 1024,
                    cacheDir = cacheDirectory.absolutePath,
                )
            )
            _engine.initialize()
            engine = _engine

        }.mapError { throwable ->
            AssistantError(
                errorMessage = context.getString(
                    R.string.assistant_api_gpu_backend_initialization_failed_title
                ),
                errorCause = throwable.stackTrace.formatAsMultilineString(),
                errorCompanion = context.getString(
                    R.string.assistant_api_gpu_backend_initialization_failed_description
                )
            )
        }
    internal fun initializeCpuEngine(
        modelFile: File,
        cacheDirectory: File
    ): Result<Unit, AssistantError> =
        runCatching {
            val _engine = Engine(
                EngineConfig(
                    modelPath = modelFile.absolutePath,
                    backend = Backend.CPU(),
                    maxNumTokens = 1024,
                    cacheDir = cacheDirectory.absolutePath,
                )
            )
            _engine.initialize()
            engine = _engine

        }.mapError { throwable ->
            AssistantError(
                errorMessage = context.getString(
                    R.string.assistant_api_cpu_backend_initialization_failed_title
                ),
                errorCause = throwable.stackTrace.formatAsMultilineString(),
                errorCompanion = context.getString(
                    R.string.assistant_api_cpu_backend_initialization_failed_description
                )
            )
        }

    /**
     * 查找并返回 LiteRT-LM 要加载的模型文件，不从 APK assets 复制模型。
     *
     * 模型文件必须由用户或外部文件管理器预先放入应用的 external files 目录。默认文件名为
     * `Qwen.litertlm`，对应设备上的路径通常是：
     * `/storage/emulated/0/Android/data/com.baidaidai.rootless_store/files/models/Qwen.litertlm`。
     * 可以使用获得 Shizuku 授权的 MT 管理器，将完整 `.litertlm` 文件复制到该目录。
     *
     * 查找步骤：
     * 1. 通过 File System Gateway 获取当前应用的 external files 根目录；不可用时返回 [Err]。
     * 2. 在 external files 根目录下定位 `models` 子目录。如果目录不存在，则尝试创建；
     *    创建失败时返回 [Err]。
     * 3. 在 `models` 目录中查找 `Qwen.litertlm`；文件不存在或不是普通文件时返回 [Err]。
     * 4. 文件存在时以 [Ok] 返回，由调用方把其绝对路径传给 [EngineConfig.modelPath]。
     *    本方法不读取文件内容、不复制文件，也不校验模型格式或版本。
     *
     * @return 成功时返回模型文件，失败时返回对应的 [AssistantError]。
     */
    private fun requireExternalModelFile(): Result<File, AssistantError> {
        val externalFilesDirectory = androidFileSystemDefaultOperatorGateway
            .findExternalAppFilesDirectoryFile()

        if (externalFilesDirectory == null) {
            return Err(
                AssistantError(
                    errorMessage = context.getString(
                        R.string.assistant_api_model_storage_unavailable_title
                    ),
                    errorCompanion = context.getString(
                        R.string.assistant_api_model_storage_unavailable_description
                    )
                )
            )
        }

        val modelDirectory = androidFileSystemCreateOperatorGateway.resolveChildFile(
            parentDirectory = externalFilesDirectory,
            childName = "models"
        )
        val ensuredModelDirectory = androidFileSystemCreateOperatorGateway
            .ensureDirectory(modelDirectory)

        if (ensuredModelDirectory == null) {
            return Err(
                AssistantError(
                    errorMessage = context.getString(
                        R.string.assistant_api_model_directory_unavailable_title
                    ),
                        errorCause = modelDirectory.absolutePath,
                    errorCompanion = context.getString(
                        R.string.assistant_api_model_directory_unavailable_description
                    )
                )
            )
        }

        val modelFile = androidFileSystemCreateOperatorGateway.resolveChildFile(
            parentDirectory = ensuredModelDirectory,
            childName = MODEL_FILE_NAME
        )
        val availableModelFile = androidFileSystemSearchOperatorGateway
            .findRegularFile(modelFile)

        if (availableModelFile == null) {
            return Err(
                AssistantError(
                    errorMessage = context.getString(
                        R.string.assistant_api_model_file_not_found_title
                    ),
                        errorCause = modelFile.absolutePath,
                    errorCompanion = context.getString(
                        R.string.assistant_api_model_file_not_found_description
                    )
                )
            )
        }

        return Ok(availableModelFile)
    }

}
