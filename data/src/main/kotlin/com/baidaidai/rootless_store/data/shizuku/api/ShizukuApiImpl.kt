package com.baidaidai.rootless_store.data.shizuku.api

import android.content.pm.PackageManager
import com.baidaidai.rootless_store.domain.shizuku.api.ShizukuApi
import javax.inject.Inject
import rikka.shizuku.Shizuku

class ShizukuApiImpl @Inject constructor() : ShizukuApi {

    override fun hasShizukuPermission(): Boolean {

        return Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED

    }

    override fun isShizukuBinderAvailable(): Boolean {

        return Shizuku.pingBinder()

    }

}
