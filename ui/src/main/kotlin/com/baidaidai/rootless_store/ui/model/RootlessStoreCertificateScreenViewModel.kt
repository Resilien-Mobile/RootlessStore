package com.baidaidai.rootless_store.ui.model

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidaidai.rootless_store.application.signature.AddCaCertificateUseCase
import com.baidaidai.rootless_store.application.signature.DeleteCaCertificateUseCase
import com.baidaidai.rootless_store.application.signature.ObserveCaCertificatesUseCase
import com.baidaidai.rootless_store.application.signature.UpdateCaCertificateEnabledUseCase
import com.baidaidai.rootless_store.domain.signature.model.CaCertificate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CertificateScreenUiState(
    val isCaCertificateBottomSheetVisible: Boolean = false,
    val caCertificateName: String = "",
    val caCertificateFileUri: Uri? = null
)
@HiltViewModel
class CertificateScreenViewModel @Inject constructor(
    private val addCaCertificateUseCase: AddCaCertificateUseCase,
    private val deleteCaCertificateUseCase: DeleteCaCertificateUseCase,
    private val updateCaCertificateEnabledUseCase: UpdateCaCertificateEnabledUseCase,
    observeCaCertificatesUseCase: ObserveCaCertificatesUseCase
) : ViewModel() {

    private val _certificateScreenUiState = MutableStateFlow(CertificateScreenUiState())
    val certificateScreenUiState = _certificateScreenUiState.asStateFlow()
    val certificateList = observeCaCertificatesUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList<CaCertificate>()
    )

    fun addCaCertificate() {
        val certificateScreenUiState = _certificateScreenUiState.value
        val certificateFileUri = certificateScreenUiState.caCertificateFileUri ?: return

        viewModelScope.launch {
            addCaCertificateUseCase(
                name = certificateScreenUiState.caCertificateName,
                certificateUri = certificateFileUri
            )
            dismissCaCertificateBottomSheet()
        }
    }

    fun deleteCaCertificate(unixTimestamp: Long) {
        viewModelScope.launch {
            deleteCaCertificateUseCase(unixTimestamp)
        }
    }

    fun updateCaCertificateEnabled(
        unixTimestamp: Long,
        enabled: Boolean
    ) {
        viewModelScope.launch {
            updateCaCertificateEnabledUseCase(
                unixTimestamp = unixTimestamp,
                enabled = enabled
            )
        }
    }

    fun showCaCertificateBottomSheet() {
        _certificateScreenUiState.update { uiState ->
            uiState.copy(isCaCertificateBottomSheetVisible = true)
        }
    }

    fun dismissCaCertificateBottomSheet() {
        _certificateScreenUiState.update { uiState ->
            uiState.copy(
                isCaCertificateBottomSheetVisible = false,
                caCertificateName = "",
                caCertificateFileUri = null
            )
        }
    }

    fun onCaCertificateNameChanged(certificateName: String) {
        _certificateScreenUiState.update { uiState ->
            uiState.copy(caCertificateName = certificateName)
        }
    }

    fun onCaCertificateFileSelected(certificateFileUri: Uri) {
        _certificateScreenUiState.update { uiState ->
            uiState.copy(caCertificateFileUri = certificateFileUri)
        }
    }
}