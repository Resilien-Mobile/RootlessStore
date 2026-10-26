package com.baidaidai.rootless_store.ui.components.pluginScreen

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.baidaidai.rootless_store.domain.install.model.LocalPackageType
import com.baidaidai.rootless_store.domain.install.model.PackageSignatureInfo
import com.baidaidai.rootless_store.ui.R
import com.baidaidai.rootless_store.ui.theme.ColorFactory
import com.baidaidai.rootless_store.ui.theme.SuccessColorSet
import com.baidaidai.rootless_store.ui.theme.WarningColorSet

private enum class VerifyState {
    Signed, Unsigned, Untrusted
}

@Composable
fun PluginBottomSheetContent(
    modifier: Modifier = Modifier,
    packageSignatureInfo: PackageSignatureInfo? = null,
    onDismiss: ()-> Unit = {},
    onConfirm: ()-> Unit = {}
){

    val context = LocalContext.current

    val packageDisplayName = packageSignatureInfo?.displayName ?: "Demo Plugin"
    val packageAuthor = packageSignatureInfo?.author ?: "Unknown"
    val hasValidSigner = packageSignatureInfo?.validSigner == true

    val signatureStatus = when(packageSignatureInfo?.isSigned){
        true -> "Signed"
        else -> "Unsigned"
    }
    val authorBadgeContainerColor = if (hasValidSigner) {
        ColorFactory(SuccessColorSet).color
    } else {
        ColorFactory(WarningColorSet).color
    }
    val authorBadgeContentColor = if (hasValidSigner) {
        ColorFactory(SuccessColorSet).onColor
    } else {
        ColorFactory(WarningColorSet).onColor
    }

    val verifyState = if (packageSignatureInfo?.isSigned == true) {
        if (packageSignatureInfo.validSigner){
            VerifyState.Signed
        }else{
            VerifyState.Untrusted
        }
    } else {
        VerifyState.Unsigned
    }

    val badgeContainerColor = when(verifyState){
        VerifyState.Signed -> ColorFactory(SuccessColorSet).color
        VerifyState.Unsigned -> MaterialTheme.colorScheme.error
        VerifyState.Untrusted -> ColorFactory(WarningColorSet).color
    }

    val badgeColor = when(verifyState){
        VerifyState.Signed -> ColorFactory(SuccessColorSet).onColor
        VerifyState.Unsigned -> MaterialTheme.colorScheme.onError
        VerifyState.Untrusted -> ColorFactory(WarningColorSet).onColor
    }

    val colorfulBadgeContainerColor = when(verifyState){
        VerifyState.Signed, VerifyState.Untrusted -> ColorFactory(SuccessColorSet).color
        else -> MaterialTheme.colorScheme.error
    }

    val colorfulBadgeColor = when(verifyState){
        VerifyState.Signed, VerifyState.Untrusted -> ColorFactory(SuccessColorSet).onColor
        else -> MaterialTheme.colorScheme.onError
    }

    val badge = when(verifyState){
        VerifyState.Signed -> R.drawable.material_symbols_shield_success
        VerifyState.Unsigned -> R.drawable.material_symbols_shield_bad
        VerifyState.Untrusted -> R.drawable.material_symbols_shield_question

    }

    val packageDescription = when(verifyState){
        VerifyState.Signed -> "This package has signature metadata available. Please confirm the package information before continuing installation."
        VerifyState.Untrusted -> "This package is signed by an untrusted developer. Proceeding will add this developer to your trusted list and suppress future warnings."
        VerifyState.Unsigned -> "This package has not been signed and cannot be confirmed to be secure. Please carefully consider before making your choice."
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(
                        color = badgeContainerColor
                    )
            ) {
                Icon(
                    painter = painterResource(badge),
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier
                        .size(40.dp)
                )
            }

            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = packageDisplayName,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W600
            )

            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ColorfulBadge(
                    backgroundColor = authorBadgeContainerColor,
                    contentColor = authorBadgeContentColor,
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.material_symbols_person),
                            contentDescription = null
                        )
                    },
                    content = {
                        Text(
                            text = packageAuthor,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                )
                ColorfulBadge(
                    backgroundColor = colorfulBadgeContainerColor,
                    contentColor = colorfulBadgeColor,
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.material_symbols_edit),
                            contentDescription = null
                        )
                    },
                    content = {
                        Text(
                            text = signatureStatus,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                )
            }

            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = packageDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight(440)
            )

            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.height(20.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Dismiss")
                }
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Confirm")
                }
            }
        }
        IconButton(
            colors = IconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                disabledContentColor = MaterialTheme.colorScheme.onSurface
            ),
            onClick = {
                val url = "https://resilien-mobile.github.io/RootlessStore_WiKi/signature/"
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                context.startActivity(intent)
            },
            modifier = Modifier
                .size(15.dp)
                .align(Alignment.TopEnd)
                .offset(x = (-16).dp, y = 16.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.material_symbols_info_i),
                contentDescription = "Why about this page",
                modifier = Modifier.size(10.dp)
            )
        }
    }

}

@PreviewLightDark
@Composable
private fun _preview_() {
    PluginBottomSheetContent(
        packageSignatureInfo = PackageSignatureInfo(
            packageType = LocalPackageType.Plugin,
            displayName = "Demo Plugin",
            author = "Rootless Store",
            validSigner = false,
            isSigned = true,
        ),
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surface)
    )
}
