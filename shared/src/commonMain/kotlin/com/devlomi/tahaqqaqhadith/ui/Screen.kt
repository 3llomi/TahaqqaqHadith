package com.devlomi.tahaqqaqhadith.ui

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Walkthrough : Screen("walkthrough")
}