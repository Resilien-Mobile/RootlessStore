package com.baidaidai.rootless_store.data.shizuku.gateway

import com.baidaidai.rootless_store.domain.shizuku.api.ShizukuApi
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HasShizukuPermissionTest {

    private class FakeShizukuApi(
        private val shizukuPermissionGranted: Boolean,
        private val shizukuBinderAvailable: Boolean,
    ) : ShizukuApi {

        override fun hasShizukuPermission(): Boolean {

            return shizukuPermissionGranted

        }

        override fun isShizukuBinderAvailable(): Boolean {

            return shizukuBinderAvailable

        }

    }

    @Test
    fun hasShizukuPermissionNormal() {

        val fakeShizukuApi = FakeShizukuApi(
            shizukuPermissionGranted = true,
            shizukuBinderAvailable = true,
        )
        val shizukuPermissionGateway = ShizukuPermissionGatewayImpl(
            shizukuApi = fakeShizukuApi,
        )

        val actualShizukuPermission = shizukuPermissionGateway.hasShizukuPermission()

        assertTrue(actualShizukuPermission)

    }

    @Test
    fun hasShizukuPermissionFail() {

        val fakeShizukuApi = FakeShizukuApi(
            shizukuPermissionGranted = false,
            shizukuBinderAvailable = true,
        )
        val shizukuPermissionGateway = ShizukuPermissionGatewayImpl(
            shizukuApi = fakeShizukuApi,
        )

        val actualShizukuPermission = shizukuPermissionGateway.hasShizukuPermission()

        assertFalse(actualShizukuPermission)

    }

}
