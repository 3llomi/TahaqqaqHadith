package com.devlomi.tahaqqaqhadith.ui.home

sealed class HomeEvents {
    data object Search: HomeEvents()
    data class OnQueryTextChange(val text: String): HomeEvents()
}