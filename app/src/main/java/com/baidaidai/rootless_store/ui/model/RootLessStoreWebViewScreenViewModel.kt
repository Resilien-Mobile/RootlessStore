package com.baidaidai.rootless_store.ui.model

import android.content.Context
import androidx.lifecycle.ViewModel
import com.baidaidai.rootless_store.application.webui.KernelSuCompatible
import com.baidaidai.rootless_store.data.shizuku.gateway.ShizukuUserServiceGatewayImpl
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RootLessStoreWebViewScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val shizukuUserServiceGatewayImpl: ShizukuUserServiceGatewayImpl
): ViewModel() {

    fun createKernelSuCompatible(): KernelSuCompatible {
        return KernelSuCompatible(
            context = context,
            shizukuUserServiceGatewayImpl = shizukuUserServiceGatewayImpl
        )
    }

}