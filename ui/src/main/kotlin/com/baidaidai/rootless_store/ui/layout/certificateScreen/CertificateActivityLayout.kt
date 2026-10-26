package com.baidaidai.rootless_store.ui.layout.certificateScreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.baidaidai.rootless_store.core.i18n.icuString
import com.baidaidai.rootless_store.ui.R
import com.baidaidai.rootless_store.ui.components.common.VerticalSpacer
import com.baidaidai.rootless_store.ui.components.pluginScreen.PluginCaCertificateBottomSheetContent
import com.baidaidai.rootless_store.ui.model.CertificateScreenViewModel
import com.baidaidai.rootless_store.ui.theme.getExpressiveListItemShape
import com.baidaidai.rootless_store.ui.theme.getListItemColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun CertificateActivityLayout(
    certificateScreenViewModel: CertificateScreenViewModel = hiltViewModel()
){
    // State
    val coroutineScope = rememberCoroutineScope()
    val certificateScreenUiState by certificateScreenViewModel.certificateScreenUiState.collectAsState()
    val certificateList by certificateScreenViewModel.certificateList.collectAsState()
    val isImeVisible = WindowInsets.isImeVisible

    val bottomSheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(
            SheetValue.Hidden,
            SheetValue.Expanded
        )
    )

    // Functions
    fun hideBottomSheet() {
        coroutineScope.launch {
            bottomSheetState.hide()
            certificateScreenViewModel.dismissCaCertificateBottomSheet()
        }
    }

    // Launched
    LaunchedEffect(isImeVisible, certificateScreenUiState.isCaCertificateBottomSheetVisible) {
        if (!certificateScreenUiState.isCaCertificateBottomSheetVisible) return@LaunchedEffect

        if (isImeVisible) {
            bottomSheetState.expand()
        }
    }

    val certificateDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { certificateFileUri ->
        certificateFileUri?.let(certificateScreenViewModel::onCaCertificateFileSelected)
    }

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text("Certificate Screen")
                },
                subtitle = {
                    Text(
                        text = icuString(
                            R.string.certificate_screen_top_app_bar_subtitle,
                            mapOf("certificateCount" to certificateList.size)
                        )
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = certificateScreenViewModel::showCaCertificateBottomSheet
            ) {
                Icon(
                    painter = painterResource(R.drawable.material_symbols_add),
                    contentDescription = stringResource(
                        R.string.certificate_screen_add_certificate_content_description
                    )
                )
            }
        }
    ) { contentPadding ->

        if (certificateScreenUiState.isCaCertificateBottomSheetVisible) {
            ModalBottomSheet(
                sheetState = bottomSheetState,
                properties = ModalBottomSheetProperties(
                    shouldDismissOnBackPress = !isImeVisible
                ),
                onDismissRequest = {
                    hideBottomSheet()
                },
                modifier = Modifier.imePadding()
            ) {
                PluginCaCertificateBottomSheetContent(
                    certificateName = certificateScreenUiState.caCertificateName,
                    onCertificateNameChange = certificateScreenViewModel::onCaCertificateNameChanged,
                    onDismiss = { hideBottomSheet() },
                    onConfirm = certificateScreenViewModel::addCaCertificate,
                    onSelectCertificateFile = {
                        certificateDocumentLauncher.launch(
                            arrayOf(
                                "application/pkix-cert",
                                "application/x-x509-ca-cert",
                                "application/x-pem-file",
                                "application/pem-certificate-chain",
                                "application/octet-stream",
                                "text/plain"
                            )
                        )
                    }
                )
            }
        }

        if (certificateList.isNotEmpty()){

            LazyColumn(
                modifier = Modifier
                    .padding(
                        top = contentPadding.calculateTopPadding()
                    )
                    .padding(horizontal = 16.dp)
            ) {
                itemsIndexed(
                    items = certificateList,
                    key = { _, certificate -> certificate.unixTimestamp }
                ){ index, certificate ->
                    val swipeToDismissBoxState = rememberSwipeToDismissBoxState()

                    SwipeToDismissBox(
                        state = swipeToDismissBoxState,
                        enableDismissFromStartToEnd = false,
                        enableDismissFromEndToStart = true,
                        onDismiss = { dismissDirection ->
                            if (dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                                certificateScreenViewModel.deleteCaCertificate(
                                    certificate.unixTimestamp
                                )
                            }
                        },
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.errorContainer),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.material_symbols_delete),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(end = 16.dp)
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(getExpressiveListItemShape(index,certificateList))
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(certificate.name)
                            },
                            trailingContent = {
                                Switch(
                                    checked = certificate.enabled,
                                    onCheckedChange = { enabled ->
                                        certificateScreenViewModel.updateCaCertificateEnabled(
                                            unixTimestamp = certificate.unixTimestamp,
                                            enabled = enabled
                                        )
                                    }
                                )
                            },
                            colors = getListItemColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (index != certificateList.lastIndex){
                        VerticalSpacer(2.dp)
                    }
                }

            }

        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = contentPadding.calculateTopPadding())
            ) {
                Icon(
                    painter = painterResource(R.drawable.material_symbols_license),
                    modifier = Modifier.size(64.dp),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Text(
                    text = stringResource(R.string.certificate_screen_empty_state_message),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }


    }

}