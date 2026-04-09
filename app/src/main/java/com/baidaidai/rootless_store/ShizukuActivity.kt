package com.baidaidai.rootless_store

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.baidaidai.rootless_store.components.startScreen.StartScreenErrorDialog
import com.baidaidai.rootless_store.domain.error.RootlessStoreError
import com.baidaidai.rootless_store.ui.model.RootlessStoreShizukuAdbScreenViewModel
import com.baidaidai.rootless_store.ui.screens.ShizukuAdbScreen
import com.baidaidai.rootless_store.ui.theme.RootlessStoreTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShizukuActivity: ComponentActivity() {
    @OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val shizukuAdbScreenViewModel: RootlessStoreShizukuAdbScreenViewModel = hiltViewModel<RootlessStoreShizukuAdbScreenViewModel>()
            var sharedEvent by rememberSaveable { mutableStateOf<RootlessStoreError?>(null) }
            LaunchedEffect(0) {
                shizukuAdbScreenViewModel.shizukuEvent.collect { event ->
                    sharedEvent = event
                }
            }
            RootlessStoreTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text("Shizuku Auth")
                            }
                        )
                    }
                ) { contentPadding ->
                    if (sharedEvent is RootlessStoreError){
                        StartScreenErrorDialog(shizukuAdbScreenViewModel, sharedEvent)
                    }
                    ShizukuAdbScreen(
                        contentPaddingValues = contentPadding,
                        shizukuAdbScreenViewModel
                    )
                }
            }
        }
    }
}