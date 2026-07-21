package com.devlomi.tahaqqaqhadith.ui.home

sealed class HomeEvents {
    data class Search(val query: String): HomeEvents()
}