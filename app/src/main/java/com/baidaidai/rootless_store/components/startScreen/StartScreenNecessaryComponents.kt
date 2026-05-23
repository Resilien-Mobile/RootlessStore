package com.baidaidai.rootless_store.components.startScreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.domain.plugin.model.NavBarItemSpec

object StartScreenNecessaryComponents {
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
        navigatorController: NavController
    ) {
        val NavigationBarRenderingList = listOf(
            NavBarItemSpec(
                number = 0,
                pattern = painterResource(R.drawable.outline_home_24),
                contentDeprecated = stringResource(R.string.start_screen_navigation_bar_home_label),
                compatibleDestinationList = listOf(
                    "HomeScreen",
                    "ShellScreen",
                    "SettingScreen"
                )
            ),
            NavBarItemSpec(
                number = 1,
                pattern = painterResource(R.drawable.outline_extension_24),
                contentDeprecated = stringResource(R.string.start_screen_navigation_bar_plugin_label),
                compatibleDestinationList = listOf(
                    "PluginScreen",
                    "ExecuteScreen"
                )
            ),
            NavBarItemSpec(
                number = 2,
                pattern = painterResource(R.drawable.outline_list_alt_24),
                contentDeprecated = stringResource(R.string.start_screen_navigation_bar_sources_label),
                compatibleDestinationList = listOf(
                    "SourcesScreen",
                    "MarketScreen"
                )
            )
        )
        val navBackStackEntry by navigatorController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination?.route ?: "HomeScreen"
        NavigationBar {
            NavigationBarRenderingList.forEachIndexed { index, spec ->
                NavigationBarItem(
                    selected = currentDestination in spec.compatibleDestinationList,
                    onClick = {
                        navigatorController.navigate(spec.compatibleDestinationList.first())
                    },
                    icon = { Icon(spec.pattern, contentDescription = spec.contentDeprecated) },
                    label = { Text(spec.contentDeprecated) }
                )
            }
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