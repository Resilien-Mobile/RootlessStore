package com.baidaidai.rootless_store.ui.components.processManager

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.baidaidai.rootless_store.domain.process.model.ProcessType
import com.baidaidai.rootless_store.domain.process.model.ProcessInfo
import com.baidaidai.rootless_store.ui.R
import com.baidaidai.rootless_store.ui.theme.getListItemColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ProcessManagerListItem(
    processInfo: ProcessInfo,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val leadingIconResource = when (processInfo.processType) {
        ProcessType.LinuxProcess -> R.drawable.terminal_24px
        ProcessType.AndroidPackage -> R.drawable.material_symbols_applications
        ProcessType.Unknown -> R.drawable.materiral_symbols_help
    }

    val packageName = processInfo.packageName?.takeIf {
        processInfo.processType == ProcessType.AndroidPackage && it.isNotBlank()
    }
    val packageIconBitmap = produceState<Bitmap?>(null, packageName) {
        value = null
        value = packageName?.let {
            withContext(Dispatchers.IO) {
                runCatching {
                    context.packageManager.getApplicationIcon(it).toBitmap()
                }.getOrNull()
            }
        }
    }

    ListItem(
        headlineContent = {

            Text(
                text = when (processInfo.processType) {
                    ProcessType.Unknown -> "Unknown"
                    else -> processInfo.processName
                        ?: processInfo.packageName
                        ?: processInfo.processType.name
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

        },
        leadingContent = {
            val appIconBitmap = packageIconBitmap.value
            if (appIconBitmap != null) {
                Image(
                    bitmap = appIconBitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    painter = painterResource(leadingIconResource),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        trailingContent = {
            Text(processInfo.pid.toString())

        },
        colors = getListItemColors(),
        modifier = modifier
    )

}
