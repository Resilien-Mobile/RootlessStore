package com.baidaidai.rootless_store.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.components.sourcesScreen.SourceScreenListItem
import com.baidaidai.rootless_store.domain.source.model.PluginSourceLocal
import com.baidaidai.rootless_store.ui.model.RootLessStoreSourceScreenViewModel
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SourceScreen(
    contentPadding: PaddingValues,
    sourceScreenViewModel: RootLessStoreSourceScreenViewModel,
    onListItemClick: (pluginSourceLocal: PluginSourceLocal)-> Unit
){
    val pluginSourceList by sourceScreenViewModel.sourceList.collectAsState()
    val sourceScreenLeadingDeleteButtonStatus by sourceScreenViewModel.deleterShowStatus.collectAsState()

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
                    items = pluginSourceList
                ){ listIndex, pluginSource ->
                    Column {
                        SourceScreenListItem(
                            sourceScreenLeadingDeleteButtonStatus,
                            pluginSourceLocal = pluginSource,
                            sourceScreenViewModel
                        ) {
                            onListItemClick(pluginSource)
                        }
                        if (listIndex!=pluginSourceList.size-1){
                            Spacer(modifier = Modifier.height(2.dp))
                        }
                    }
                }
            }
        }
    }


//    val renderingList = sourcesScreenViewModel.plugins.collectAsLazyPagingItems()
//    LazyColumn(
//        modifier = Modifier
//            .padding(contentPadding)
//            .fillMaxSize()
//            .padding(vertical = 15.dp)
//            .padding(horizontal = 15.dp),
//        verticalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        items(
//            count = renderingList.itemCount
//        ){ plugin ->
//            val items = renderingList[plugin]
//            PluginInfoContainer(pluginManifest = items!!){}
//        }
//    }
}

//@OptIn(ExperimentalMaterial3ExpressiveApi::class)
//@Composable
//@PreviewLightDark
//private fun _SourcesScreenPreview_(){
//    ListItem(
//        headlineContent = { Text("1") },
//        supportingContent = {Text("2")}
//    )
//}
