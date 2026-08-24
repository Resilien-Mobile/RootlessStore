package com.baidaidai.rootless_store.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.domain.source.model.PluginSource
import com.baidaidai.rootless_store.ui.model.RootlessStoreSourceScreenViewModel
import com.baidaidai.rootless_store.ui.components.sourcesScreen.SourceScreenAlertDialog
import com.baidaidai.rootless_store.ui.components.sourcesScreen.SourceScreenAuthenticationModalBottomSheet
import com.baidaidai.rootless_store.ui.components.sourcesScreen.SourceScreenListItem

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SourceScreen(
    contentPadding: PaddingValues,
    sourceScreenViewModel: RootlessStoreSourceScreenViewModel,
    onPluginSourceClick: (pluginSource: PluginSource)-> Unit
){
    val pluginSources by sourceScreenViewModel.pluginSources.collectAsState()
    val isDeleteActionVisible by sourceScreenViewModel.isDeleteActionVisible.collectAsState()

    val isAuthenticationSheetVisible by sourceScreenViewModel.isAuthenticationSheetVisible.collectAsState()
    val isAuthenticationDialogVisible by sourceScreenViewModel.isAuthenticationDialogVisible.collectAsState()



    if(isAuthenticationDialogVisible){
        SourceScreenAlertDialog(
            onDismissRequest = sourceScreenViewModel::cancelSourceAuthentication,
            onStartAuthentication = sourceScreenViewModel::startSourceAuthentication,
            onCancelAuthentication = sourceScreenViewModel::cancelSourceAuthentication
        )
    }

    if (isAuthenticationSheetVisible){
        SourceScreenAuthenticationModalBottomSheet(
            onDismissRequest = sourceScreenViewModel::cancelSourceAuthentication,
            onCancelAuthentication = sourceScreenViewModel::cancelSourceAuthentication,
        ) { username, password ->
            sourceScreenViewModel.addAuthenticatedPluginSource(username,password)
        }
    }

    Box(
        modifier = Modifier
            .padding(contentPadding)
            .padding(horizontal = 16.dp)
    ) {
        Surface(
            modifier = Modifier.clip(MaterialTheme.shapes.large)
        ) {
            LazyColumn{
                itemsIndexed(
                    items = pluginSources
                ){ listIndex, pluginSource ->
                    Column {
                        SourceScreenListItem(
                            isDeleteActionVisible = isDeleteActionVisible,
                            pluginSource = pluginSource,
                            sourceScreenViewModel = sourceScreenViewModel,
                            onPluginSourceClick = onPluginSourceClick
                        )
                        if (listIndex!=pluginSources.size-1){
                            Spacer(modifier = Modifier.height(2.dp))
                        }
                    }
                }
            }
        }
    }
}
