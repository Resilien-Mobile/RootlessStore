package com.baidaidai.rootless_store.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import com.baidaidai.rootless_store.ui.layout.certificateScreen.CertificateActivityLayout
import com.baidaidai.rootless_store.ui.theme.RootlessStoreTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CertificateActivity: ComponentActivity() {

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RootlessStoreTheme{
                CertificateActivityLayout()
            }
        }

    }

}