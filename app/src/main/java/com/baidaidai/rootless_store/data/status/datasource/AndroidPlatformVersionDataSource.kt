package com.baidaidai.rootless_store.data.status.datasource

import javax.inject.Inject

class AndroidPlatformVersionDataSource @Inject constructor() {
    fun getReleaseLabel(): String{
        return "Android ${android.os.Build.VERSION.RELEASE}"
    }
    fun getApiLevelLabel(): String{
        return "API ${android.os.Build.VERSION.SDK_INT}"
    }
}
