package com.baidaidai.rootless_store.ui.screens

import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.baidaidai.rootless_store.ui.model.RootLessStoreWebViewScreenViewModel

private val kernelSuCompatibleScript = """
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

@Composable
fun RootlessStoreWebViewScreen(
    modifier: Modifier = Modifier,
    webUri: String?,
    webViewScreenViewModel: RootLessStoreWebViewScreenViewModel = hiltViewModel()
) {

    if (webUri == null) return

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
                WebView.setWebContentsDebuggingEnabled(true)

                addJavascriptInterface(
                    webViewScreenViewModel.createKernelSuCompatible(),
                    "__rootless_ksu"
                ) // Inject JavaScript Native Bridge

                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)

                        view?.evaluateJavascript(kernelSuCompatibleScript, null)  // Inject JavaScript KernelSU API grammar adaptor support

                    }
                }

                loadUrl(webUri)

            }
        }
    )
}