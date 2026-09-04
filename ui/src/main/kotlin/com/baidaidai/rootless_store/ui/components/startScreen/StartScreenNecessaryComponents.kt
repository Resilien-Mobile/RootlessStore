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
import com.baidaidai.rootless_store.ui.R
import com.baidaidai.rootless_store.ui.navigation.`interface`.RootlessNavigationKey
import com.baidaidai.rootless_store.ui.navigation.model.CodeBrickScreenKey
import com.baidaidai.rootless_store.ui.navigation.model.ExecuteScreenKey
import com.baidaidai.rootless_store.ui.navigation.model.HomeScreenKey
import com.baidaidai.rootless_store.ui.navigation.model.PluginScreenKey
import com.baidaidai.rootless_store.ui.navigation.model.SettingScreenKey
import com.baidaidai.rootless_store.ui.navigation.model.ShellScreenKey
import com.baidaidai.rootless_store.ui.navigation.model.NavBarItemSpec
import com.baidaidai.rootless_store.ui.navigation.model.ThirdPartyNotificationScreenKey

object StartScreenNecessaryComponents {

    @Composable
    private fun listNavigationBarItems(): List<NavBarItemSpec>{
        return listOf(
            NavBarItemSpec(
                number = 0,
                icon = painterResource(R.drawable.outline_home_24),
                label = stringResource(R.string.start_screen_navigation_bar_home_label),
                targetDestination = HomeScreenKey,
                compatibleDestinations = listOf(
                    HomeScreenKey::class,
                    ShellScreenKey::class,
                    SettingScreenKey::class,
                    ThirdPartyNotificationScreenKey::class
                )
            ),
            NavBarItemSpec(
                number = 1,
                icon = painterResource(R.drawable.outline_extension_24),
                label = stringResource(R.string.start_screen_navigation_bar_plugin_label),
                targetDestination = PluginScreenKey,
                compatibleDestinations = listOf(
                    PluginScreenKey::class,
                    ExecuteScreenKey("abcde")::class
                )
            ),
            NavBarItemSpec(
                number = 2,
                icon = painterResource(R.drawable.material_symbols_data_object),
                label = stringResource(R.string.start_screen_navigation_bar_brick_label),
                targetDestination = CodeBrickScreenKey,
                compatibleDestinations = listOf(
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
        val navigationItems = listNavigationBarItems()
        NavigationBar {
            navigationItems.forEach { navBarItemSpec ->
                NavigationBarItem(
                    selected = currentDestination::class in navBarItemSpec.compatibleDestinations,
                    onClick = {
                        onNavigate(navBarItemSpec.targetDestination)
                    },
                    icon = { Icon(navBarItemSpec.icon, contentDescription = navBarItemSpec.label) },
                    label = { Text(navBarItemSpec.label) }
                )
            }
        }
    }

    @Composable
    fun StartScreenNavigationRail(
        currentDestination: RootlessNavigationKey,
        onNavigate:(RootlessNavigationKey)-> Unit
    ) {
        val navigationItems = listNavigationBarItems()
        NavigationRail(
        ){
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                navigationItems.forEach { navBarItemSpec ->
                    NavigationRailItem(
                        selected = currentDestination::class in navBarItemSpec.compatibleDestinations,
                        onClick = {
                            onNavigate(navBarItemSpec.targetDestination)
                        },
                        icon = {
                            Icon(
                                painter = navBarItemSpec.icon,
                                contentDescription = navBarItemSpec.label
                            )
                        },
                        label = {
                            Text(navBarItemSpec.label)
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

        val navigationItems = listNavigationBarItems()

        NavigationRail{
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .widthIn(220.dp, 360.dp)
                    .padding(horizontal = 20.dp)
                    .fillMaxHeight()
            ) {
                navigationItems.forEach { navBarItemSpec ->
                    ExpressiveNavigationRailItem(
                        isSelected = currentDestination::class in navBarItemSpec.compatibleDestinations,
                        onClick = {
                            onNavigate(navBarItemSpec.targetDestination)
                        },
                        icon = {
                            Icon(
                                painter = navBarItemSpec.icon,
                                contentDescription = navBarItemSpec.label
                            )
                        },
                        label = {
                            Text(navBarItemSpec.label)
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun ExpressiveNavigationRailItem(
        isSelected: Boolean,
        onClick: () -> Unit,
        icon: @Composable () -> Unit,
        modifier: Modifier = Modifier,
        isEnabled: Boolean = true,
        label: @Composable (() -> Unit)? = null,
    ){
        val selectedButtonColors = ButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
        val unselectedButtonColors = ButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            disabledContentColor = MaterialTheme.colorScheme.onSurface
        )

        Button(
            onClick = { if (isEnabled){ onClick() } },
            contentPadding = PaddingValues(horizontal = 24.dp),
            colors = if (isSelected) selectedButtonColors else unselectedButtonColors,
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
