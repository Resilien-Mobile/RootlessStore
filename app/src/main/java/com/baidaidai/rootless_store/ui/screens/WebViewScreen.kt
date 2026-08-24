package com.baidaidai.rootless_store.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebView.setWebContentsDebuggingEnabled
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.webkit.WebViewCompat.addWebMessageListener
import com.baidaidai.rootless_store.ui.model.RootlessStoreWebViewScreenViewModel
import kotlinx.coroutines.launch

private val kernelSuCompatibilityScript = """
    (function() {
        window.ksu = window.ksu || {};
        window.ksu.exec = async function(command, options) {
            const rawResult = window.__rootless_ksu.exec(command);
            return JSON.parse(rawResult);
        };
        window.ksu.listPackages = function(type) {
            const rawResult = window.__rootless_ksu.listPackages(type || "all");
            return rawResult
        };
    })();
""".trimIndent()

@SuppressLint("RequiresFeature")
@Composable
fun RootlessStoreWebViewScreen(
    modifier: Modifier = Modifier,
    webUri: String?,
    webViewScreenViewModel: RootlessStoreWebViewScreenViewModel = hiltViewModel()
) {

    if (webUri == null) return

    val coroutineScope = rememberCoroutineScope()

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {

                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                ) // Ensure CSS's style is OK

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                setWebContentsDebuggingEnabled(true)

                addWebMessageListener(this, "AppShell",setOf("*")){ _, message, _, _, proxy ->
                    coroutineScope.launch {
                        webViewScreenViewModel.executeAppShell(message.data).collect { shellResult ->
                            proxy.postMessage(shellResult.output)
                        }
                    }
                } // Newest JavaScript Native Bridge

                addJavascriptInterface(
                    webViewScreenViewModel.createKernelSuJavaScriptBridge(),
                    "__rootless_ksu"
                ) // Oldest Inject JavaScript Native Bridge

                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)

                        view?.evaluateJavascript(kernelSuCompatibilityScript, null)  // Inject JavaScript KernelSU API grammar adaptor support

                    }
                }

                loadUrl(webUri)

            }
        }
    )
}
