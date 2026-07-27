package com.baidaidai.rootless_store

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import com.baidaidai.rootless_store.ui.theme.RootlessStoreTheme
import com.baidaidai.rootless_store.ui.screens.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebViewActivity: ComponentActivity() {

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val webUri = intent.getStringExtra("webUri")

        setContent {
            RootlessStoreTheme {
                RootlessStoreWebViewScreen(webUri = webUri)
            }
        }
    }
}