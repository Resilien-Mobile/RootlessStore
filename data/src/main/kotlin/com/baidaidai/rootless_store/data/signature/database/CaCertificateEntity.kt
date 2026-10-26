package com.baidaidai.rootless_store.data.signature.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CaCertificateEntity(
    @PrimaryKey
    val unixTimestamp: Long,
    val name: String,
    val derBytes: ByteArray,
    val enabled: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CaCertificateEntity

        if (unixTimestamp != other.unixTimestamp) return false
        if (enabled != other.enabled) return false
        if (name != other.name) return false
        if (!derBytes.contentEquals(other.derBytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = unixTimestamp.hashCode()
        result = 31 * result + enabled.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + derBytes.contentHashCode()
        return result
    }
}