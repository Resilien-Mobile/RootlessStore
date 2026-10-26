package com.baidaidai.rootless_store.ui.components.settingScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.ui.R

private const val ROOTLESS_STORE_GITHUB_URL =
    "https://github.com/Resilien-Mobile/RootlessStore"
private const val ROOTLESS_STORE_QQ_GROUP_URL =
    "https://qun.qq.com/universal-share/share?ac=1" +
        "&authKey=ZubO%2FbgKK8WWLB4933YkaBSKlUGFmx6JeZqWVanHIXJe0pi0ktWmCWWSXiQyhtta" +
        "&busi_data=eyJncm91cENvZGUiOiIxMDkzOTkwMzE4IiwidG9rZW4iOiJVSzdKYURkclpQcGhUdXRNYkVURkc2dEFicHpjQXBOLytldy9qOHZndzhrSURhUEVyRWdpbWNKOFhoL2hpTTRuIiwidWluIjoiMTMwMjA2NDM5NiJ9" +
        "&data=q1ZKNAEimSszcWAAYKUpb9GHdgrbg6SfwQv5PlSSDF4wfuNZmTAAOMME92lp1oWop2kilhL2Ta3-CAUzjPgIhw" +
        "&svctype=4&tempid=h5_group_info"
private const val ROOTLESS_STORE_TELEGRAM_GROUP_URL = "https://t.me/+3yjgOb4EpE5jNjFl"
private const val ROOTLESS_STORE_AUTHOR_X_URL = "https://x.com/creater_bai"

@Composable
fun SettingScreenAboutAlertDialogContent(
    onVersionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .padding(20.dp)
            .fillMaxWidth()
            .wrapContentSize()
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onVersionClick
            )
        ) {
            Text(
                text = stringResource(R.string.setting_screen_about_app_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(
                    R.string.setting_screen_about_version,
                    stringResource(R.string.app_version)
                ),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.setting_screen_about_source_prefix))
            Text(
                text = stringResource(R.string.setting_screen_about_github_label),
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    uriHandler.openUri(ROOTLESS_STORE_GITHUB_URL)
                }
            )
            Text(stringResource(R.string.setting_screen_about_source_suffix))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { uriHandler.openUri(ROOTLESS_STORE_GITHUB_URL) }) {
                Icon(
                    painter = painterResource(R.drawable.ant_design_github),
                    contentDescription = stringResource(R.string.setting_screen_about_github_label)
                )
            }
            IconButton(onClick = { uriHandler.openUri(ROOTLESS_STORE_QQ_GROUP_URL) }) {
                Icon(
                    painter = painterResource(R.drawable.ant_design_qq),
                    contentDescription = stringResource(R.string.setting_screen_about_qq_label)
                )
            }
            IconButton(onClick = { uriHandler.openUri(ROOTLESS_STORE_TELEGRAM_GROUP_URL) }) {
                Icon(
                    painter = painterResource(R.drawable.ant_design_telegram),
                    contentDescription = stringResource(R.string.setting_screen_about_telegram_label)
                )
            }
            IconButton(onClick = { uriHandler.openUri(ROOTLESS_STORE_AUTHOR_X_URL) }) {
                Icon(
                    painter = painterResource(R.drawable.ant_design_twitter),
                    contentDescription = stringResource(R.string.setting_screen_about_twitter_label)
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun _SettingScreenAboutAlertDialogContentPreview_() {
    SettingScreenAboutAlertDialogContent(onVersionClick = {})
}
