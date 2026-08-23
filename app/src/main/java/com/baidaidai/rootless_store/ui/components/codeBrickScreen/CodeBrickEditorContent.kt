package com.baidaidai.rootless_store.ui.components.codeBrickScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.domain.codebrick.model.CodeBrickContextConfig
import com.baidaidai.rootless_store.domain.status.model.ExecutionContext

@Composable
fun CodeBrickEditorContent(

    modifier: Modifier = Modifier,
    titleContent: String,
    tileIndex: Int?,
    codeContent: String,
    onCodeBrickTitleValueChange: (value: String)-> Unit,
    onCodeBrickTileValueChange: (value: Int?) -> Unit,
    onCodeBrickContentValueChange: (value: String)-> Unit,
    onCodeBrickContextValueChange: (executionContext: ExecutionContext) -> Unit

){

    val brickContextConfigList = listOf(
        CodeBrickContextConfig(
            contextType = ExecutionContext.LIMITED,
            contextTextResource = R.string.code_brick_screen_editor_context_app_shell_label,
            contextIcon = R.drawable.material_symbols_applicaitons
        ),
        CodeBrickContextConfig(
            contextType = ExecutionContext.ADB,
            contextTextResource = R.string.code_brick_screen_editor_context_adb_shell_label,
            contextIcon = R.drawable.material_symbols_adb
        ),
        CodeBrickContextConfig(
            contextType = ExecutionContext.ROOTD,
            contextTextResource = R.string.code_brick_screen_editor_context_root_shell_label,
            contextIcon = R.drawable.material_symbols_cyclone
        )
    )

    var currentSelectedContext by remember { mutableStateOf(brickContextConfigList[0]) }

    Column(modifier) {

        OutlinedTextField(
            value = titleContent,
            onValueChange = onCodeBrickTitleValueChange,
            label = {
                Text(stringResource(R.string.code_brick_screen_editor_brick_name_label))
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = codeContent,
            onValueChange = onCodeBrickContentValueChange,
            label = {
                Text(stringResource(R.string.code_brick_screen_editor_shell_script_label))
            },
            modifier = Modifier.heightIn(max = 300.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBrickContextList(
            currentSelectedContext = currentSelectedContext,
            brickContextConfigList = brickContextConfigList,
            onListItemClick = { codeBrickContextConfig ->
                currentSelectedContext = codeBrickContextConfig
                onCodeBrickContextValueChange(codeBrickContextConfig.contextType)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CodeBrickTileList(
            tileBinderIndex = tileIndex
        ) { newTileBinderIndex ->
            onCodeBrickTileValueChange(newTileBinderIndex)
        }

    }

}

@PreviewLightDark
@Composable
private fun _preview_(){
    CodeBrickEditorContent(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .width(280.dp)
            .heightIn(min = 300.dp)
            .padding(16.dp)
        ,
        titleContent = "a",
        tileIndex = null,
        codeContent =  "b",
        onCodeBrickTitleValueChange =  {},
        onCodeBrickContentValueChange = {},
        onCodeBrickContextValueChange = {},
        onCodeBrickTileValueChange = {}
    )
}
