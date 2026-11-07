package com.baidaidai.rootless_store.data.environment.repository

import com.baidaidai.rootless_store.domain.environment.repository.EnvironmentStatusRepository
import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.data.environment.database.EnvironmentStatusEntity
import com.baidaidai.rootless_store.data.environment.mapper.EnvironmentMapper.toEnvironmentStatus
import com.baidaidai.rootless_store.domain.environment.model.EnvironmentStatus
import com.baidaidai.rootless_store.domain.plugin.model.PluginOrigin
import com.baidaidai.rootless_store.domain.plugin.model.PluginState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EnvironmentStatusRepositoryImpl @Inject constructor(
    rootlessStoreDatabase: RootlessStoreDatabase
): EnvironmentStatusRepository {
    private val environmentStatusDao = rootlessStoreDatabase.environmentStatusDao()

    // Add
    override suspend fun registerEnvironmentStatus(environmentId: String, origin: PluginOrigin) {
        environmentStatusDao.insertEnvironmentStatus(
            EnvironmentStatusEntity(
                environmentId = environmentId,
                isEnabled = false,
                state = PluginState.Great,
                origin = origin
            )
        )
    }

    // Read
    override suspend fun findEnvironmentStatus(environmentId: String): EnvironmentStatus? {
        return environmentStatusDao.findEnvironmentStatusById(environmentId)?.toEnvironmentStatus()
    }

    override fun observeEnvironmentStatus(environmentId: String): Flow<EnvironmentStatus?> {
        return environmentStatusDao.observeEnvironmentStatusById(environmentId).map { it?.toEnvironmentStatus() }
    }

    override fun observeEnvironmentStatuses(): Flow<List<EnvironmentStatus>> {
        return environmentStatusDao.observeEnvironmentStatuses().map { entities ->
            entities.map { it.toEnvironmentStatus() }
        }
    }

    override fun observeEnabledEnvironmentStatuses(): Flow<List<EnvironmentStatus>> {
        return environmentStatusDao.observeEnabledEnvironmentStatuses().map { entities ->
            entities.map { it.toEnvironmentStatus() }
        }
    }

    override suspend fun getEnabledEnvironmentCount(): Int {
        return environmentStatusDao.getEnabledEnvironmentCount()
    }

    // Update
    override suspend fun enableEnvironment(environmentId: String) {
        environmentStatusDao.updateEnvironmentEnabled(environmentId = environmentId, isEnabled = true)
    }

    override suspend fun disableEnvironment(environmentId: String) {
        environmentStatusDao.updateEnvironmentEnabled(environmentId = environmentId, isEnabled = false)
    }

    // Delete
    override suspend fun deleteEnvironmentStatus(environmentId: String) {
        environmentStatusDao.deleteEnvironmentStatusById(environmentId)
    }
}
