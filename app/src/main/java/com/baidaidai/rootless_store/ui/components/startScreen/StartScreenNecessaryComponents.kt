package com.baidaidai.rootless_store.ui.components.startScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.domain.navigation.`interface`.RootlessNavigationKey
import com.baidaidai.rootless_store.domain.navigation.model.CodeBrickScreenKey
import com.baidaidai.rootless_store.domain.navigation.model.ExecuteScreenKey
import com.baidaidai.rootless_store.domain.navigation.model.HomeScreenKey
import com.baidaidai.rootless_store.domain.navigation.model.PluginScreenKey
import com.baidaidai.rootless_store.domain.navigation.model.SettingScreenKey
import com.baidaidai.rootless_store.domain.navigation.model.ShellScreenKey
import com.baidaidai.rootless_store.domain.navigation.model.NavBarItemSpec
import com.baidaidai.rootless_store.domain.navigation.model.ThirdPartyNotificationScreenKey

object StartScreenNecessaryComponents {

    @Composable
    private fun getNavBarItemsSpecList(): List<NavBarItemSpec>{
        return listOf(
            NavBarItemSpec(
                number = 0,
                pattern = painterResource(R.drawable.outline_home_24),
                contentDeprecated = stringResource(R.string.start_screen_navigation_bar_home_label),
                targetDestination = HomeScreenKey,
                compatibleDestinationList = listOf(
                    HomeScreenKey::class,
                    ShellScreenKey::class,
                    SettingScreenKey::class,
                    ThirdPartyNotificationScreenKey::class
                )
            ),
            NavBarItemSpec(
                number = 1,
                pattern = painterResource(R.drawable.outline_extension_24),
                contentDeprecated = stringResource(R.string.start_screen_navigation_bar_plugin_label),
                targetDestination = PluginScreenKey,
                compatibleDestinationList = listOf(
                    PluginScreenKey::class,
                    ExecuteScreenKey("abcde")::class
                )
            ),
            NavBarItemSpec(
                number = 2,
                pattern = painterResource(R.drawable.material_symbols_data_object),
                contentDeprecated = stringResource(R.string.start_screen_navigation_bar_brick_label),
                targetDestination = CodeBrickScreenKey,
                compatibleDestinationList = listOf(
                    CodeBrickScreenKey::class,
                )
            )
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun StartScreenTopAppBar(
        scrollBehavior: TopAppBarScrollBehavior,
        onSettingClick:()-> Unit
    ){
        TopAppBar(
            title = {
                Text("Rootless Store")
            },
            actions = {
                IconButton(
                    onClick = onSettingClick
                ) {
                    Icon(
                        painter = painterResource(R.drawable.material_symbols_settings),
                        contentDescription = "Setting"
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
    }

    @Composable
    fun StartScreenNavigationBar(
        currentDestination: RootlessNavigationKey,
        onNavigate:(RootlessNavigationKey)-> Unit
    ) {
        val navigationBarRenderingList = getNavBarItemsSpecList()
        NavigationBar {
            navigationBarRenderingList.forEach { navBarItemSpec ->
                NavigationBarItem(
                    selected = currentDestination::class in navBarItemSpec.compatibleDestinationList,
                    onClick = {
                        onNavigate(navBarItemSpec.targetDestination)
                    },
                    icon = { Icon(navBarItemSpec.pattern, contentDescription = navBarItemSpec.contentDeprecated) },
                    label = { Text(navBarItemSpec.contentDeprecated) }
                )
            }
        }
    }

    @Composable
    fun StartScreenNavigationRail(
        currentDestination: RootlessNavigationKey,
        onNavigate:(RootlessNavigationKey)-> Unit
    ) {
        val navigationBarRenderingList = getNavBarItemsSpecList()
        NavigationRail(
        ){
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                navigationBarRenderingList.forEach { navBarItemSpec ->
                    NavigationRailItem(
                        selected = currentDestination::class in navBarItemSpec.compatibleDestinationList,
                        onClick = {
                            onNavigate(navBarItemSpec.targetDestination)
                        },
                        icon = {
                            Icon(
                                painter = navBarItemSpec.pattern,
                                contentDescription = navBarItemSpec.contentDeprecated
                            )
                        },
                        label = {
                            Text(navBarItemSpec.contentDeprecated)
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun StartScreenExpressiveNavigationRail(
        currentDestination: RootlessNavigationKey,
        onNavigate:(RootlessNavigationKey)-> Unit
    ) {

        val navBarItemsList = getNavBarItemsSpecList()

        NavigationRail{
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .widthIn(220.dp, 360.dp)
                    .padding(horizontal = 20.dp)
                    .fillMaxHeight()
            ) {
                navBarItemsList.forEach { navBarItemSpec ->
                    ExpressiveNavigationRailItem(
                        selected = currentDestination::class in navBarItemSpec.compatibleDestinationList,
                        onClick = {
                            onNavigate(navBarItemSpec.targetDestination)
                        },
                        icon = {
                            Icon(
                                painter = navBarItemSpec.pattern,
                                contentDescription = navBarItemSpec.contentDeprecated
                            )
                        },
                        label = {
                            Text(navBarItemSpec.contentDeprecated)
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun ExpressiveNavigationRailItem(
        selected: Boolean,
        onClick: () -> Unit,
        icon: @Composable () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        label: @Composable (() -> Unit)? = null,
    ){
        val buttonColorsSelected = ButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
        val buttonColorsNoSelected = ButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            disabledContentColor = MaterialTheme.colorScheme.onSurface
        )

        Button(
            onClick = { if (enabled){ onClick() } },
            contentPadding = PaddingValues(horizontal = 24.dp),
            colors = if (selected) buttonColorsSelected else buttonColorsNoSelected,
            modifier = modifier
                .height(56.dp)
        ) {
            icon()
            Spacer(Modifier.width(8.dp))
            label?.invoke()
        }
    }

    @Composable
    fun StartScreenFloatingButton(
        onClick:()-> Unit
    ){
        FloatingActionButton(
            onClick = onClick
        ) {
            Icon(
                painter = painterResource(R.drawable.terminal_24px),
                contentDescription = "Terminal"
            )
        }
    }
}