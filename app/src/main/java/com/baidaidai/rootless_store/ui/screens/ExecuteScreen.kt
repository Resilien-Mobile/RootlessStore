package com.baidaidai.rootless_store.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.domain.execution.model.ExecutionResultTag
import com.baidaidai.rootless_store.ui.model.RootlessStoreExecuteScreenViewModel

@Composable
fun ExecuteScreen(
    contentPaddingValues: PaddingValues,
    executeScreenViewModel: RootlessStoreExecuteScreenViewModel
){
    val executionLog by executeScreenViewModel.executionLog.collectAsState()

    LazyColumn(
        modifier = Modifier.padding(contentPaddingValues),

        contentPadding = PaddingValues(
            vertical = 15.dp,
            horizontal = 15.dp
        )
    ) {
        itemsIndexed(executionLog){ ListIndex, ListContent ->
            when(ListContent.resultTag){
                ExecutionResultTag.Normal -> {
                    Text(
                        text = ListContent.content,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                ExecutionResultTag.RedLine -> {
                    Text(
                        text = ListContent.content,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red
                    )
                }
            }
        }
    }
}
