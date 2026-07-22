package com.devlomi.tahaqqaqhadith.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.devlomi.tahaqqaqhadith.di.networkModule
import com.devlomi.tahaqqaqhadith.di.useCaseModule
import com.devlomi.tahaqqaqhadith.di.viewModelModule
import com.devlomi.tahaqqaqhadith.ui.home.HomeScreen
import com.devlomi.tahaqqaqhadith.ui.home.HomeViewModel
import com.devlomi.tahaqqaqhadith.ui.theme.HadithTheme
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.dsl.koinApplication
import org.koin.dsl.koinConfiguration

@Composable
@Preview
fun App() {
    HadithTheme {
        Box(modifier = Modifier.safeDrawingPadding()) {
            KoinApplication(
                koinConfiguration {
                    koinApplication {
                        modules(
                            networkModule(), useCaseModule(), viewModelModule()
                        )
                    }
                }, content = {
                    NavHost(
                        navController = rememberNavController(),
                        startDestination = Screen.Home.route
                    ) {
                        composable(Screen.Home.route) {
                            val viewModel = koinViewModel<HomeViewModel>()
                            val state = viewModel.state.collectAsStateWithLifecycle().value
                            HomeScreen(state, onEvent = viewModel::onEvent)
                        }
                    }
                }
            )
        }
    }
}