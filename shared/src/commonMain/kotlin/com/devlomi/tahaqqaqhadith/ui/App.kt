package com.devlomi.tahaqqaqhadith.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.devlomi.tahaqqaqhadith.data.CommonPreferences
import com.devlomi.tahaqqaqhadith.ui.home.HomeScreen
import com.devlomi.tahaqqaqhadith.ui.home.HomeViewModel
import com.devlomi.tahaqqaqhadith.ui.theme.HadithTheme
import com.devlomi.tahaqqaqhadith.ui.walkthrough.WalkthroughScreen
import org.koin.compose.getKoin
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    val settingsRepository = getKoin().get<CommonPreferences>()
    val walkthroughCompleted = settingsRepository.getBoolean("walkthrough_completed", false)
    val initialScreen = if (walkthroughCompleted) Screen.Home.route else Screen.Walkthrough.route

    HadithTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Box(
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
                    .safeDrawingPadding()
            ) {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = initialScreen,
                    enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
                    exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
                    popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
                    popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
                ) {
                    composable(Screen.Home.route) {
                        val viewModel = koinViewModel<HomeViewModel>()
                        val state = viewModel.state.collectAsStateWithLifecycle().value
                        HomeScreen(state, onEvent = viewModel::onEvent)
                    }
                    composable(Screen.Walkthrough.route) {
                        WalkthroughScreen(
                            onCompleted = {
                                navController.navigate(Screen.Home.route)
                                {
                                    popUpTo(Screen.Walkthrough.route) {
                                        inclusive = true
                                    }
                                }
                                settingsRepository.putBoolean("walkthrough_completed", true)
                            }
                        )
                    }
                }
            }
        }
    }
}