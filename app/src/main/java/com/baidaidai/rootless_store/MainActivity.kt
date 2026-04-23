package com.baidaidai.rootless_store

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.baidaidai.rootless_store.ui.screens.RootlessStoreStartScreenContainer
import com.baidaidai.rootless_store.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlin.jvm.java

val RootLessStoreLocalContext = compositionLocalOf<Context>{
    error("No Context Provide")
}

@HiltAndroidApp
class RootlessStoreApp : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity(){

    private var fileIntentUri: Uri? by mutableStateOf(null)

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        // save Intent if hot-start from an implicit invocation
        if (fileIntentUri == null){
            handleFileIntent(intent)
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current

            RootlessStoreTheme {
                CompositionLocalProvider(
                    RootLessStoreLocalContext provides context,
                ) {
                    RootlessStoreStartScreenContainer(
                        fileIntentUri = fileIntentUri,
                        onHandlerEnded = {
                            fileIntentUri = null
                        }
                    )
                }
            }
        }

    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        // save Intent if cold-start from an implicit invocation
        handleFileIntent(intent)
    }

    private fun handleFileIntent(intent: Intent?) {
        if (intent?.action != Intent.ACTION_SEND) {
            this.fileIntentUri = null
        }
        val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra(Intent.EXTRA_STREAM)
        }

        fileIntentUri = uri
    }
}