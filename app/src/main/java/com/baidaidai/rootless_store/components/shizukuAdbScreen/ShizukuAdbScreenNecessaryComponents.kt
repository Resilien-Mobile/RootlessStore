package com.baidaidai.rootless_store.components.shizukuAdbScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.core.i18n.icuString

object ShizukuAdbScreenNecessaryComponents {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShizukuAdbScreenModelSheet(
        remainderTime: Int,
        onDismissRequest: ()-> Unit,
        onCloseButtonClick: ()-> Unit,
        onReturnButtonClick: ()-> Unit
    ){
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = rememberModalBottomSheetState(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painterResource(R.drawable.material_symbols_check),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.shizuku_adb_screen_bottom_sheet_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = icuString(R.string.shizuku_adb_screen_bottom_sheet_countdown_context,mapOf("second" to remainderTime)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCloseButtonClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.shizuku_adb_screen_bottom_sheet_close_button))
                    }
                    Button(
                        onClick = onReturnButtonClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.shizuku_adb_screen_bottom_sheet_return_button))
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun ShizukuAdbScreenActionCard(
        step: String,
        title: String,
        description: String,
        targetStatus: Boolean,
        onClick: () -> Unit
    ){
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column{
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                    ){
                        Column{
                            Text(
                                text = step,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMediumEmphasized
                            )
                        }
                        Button(
                            onClick = onClick,
                            modifier = Modifier.size(48.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                painter = if (targetStatus){
                                    painterResource(R.drawable.material_symbols_check)
                                }else{
                                    painterResource(R.drawable.material_symbols_play_arrow)
                                },
                                contentDescription = stringResource(R.string.shizuku_adb_screen_action_card_start_content_description),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMediumEmphasized
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun ShizukuAdbScreenOverviewCard(){
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(),
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ){
                    Text(
                        text = stringResource(R.string.shizuku_adb_screen_overview_card_title),
                        style = MaterialTheme.typography.titleLargeEmphasized
                    )
                    Icon(
                        painterResource(R.drawable.material_shizuku_icon),
                        contentDescription = stringResource(R.string.shizuku_adb_screen_overview_card_icon_content_description),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                Text(
                    text = stringResource(R.string.shizuku_adb_screen_overview_card_description),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}