package com.devlomi.tahaqqaqhadith.di

import com.devlomi.tahaqqaqhadith.ui.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun viewModelModule() = module {
    viewModel<HomeViewModel> {
        HomeViewModel(get(),get(),get(),get())
    }
}