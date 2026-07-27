package com.devlomi.tahaqqaqhadith.di

import android.content.SharedPreferences
import app.cash.sqldelight.db.SqlDriver
import com.devlomi.tahaqqaqhadith.DriverFactory
import com.devlomi.tahaqqaqhadith.data.CommonPreferences
import com.devlomi.tahaqqaqhadith.datasource.cache.Database
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule = module {
    single<SqlDriver>{
        DriverFactory(androidContext()).createDriver()
    }
    single<SharedPreferences> {
        androidContext().getSharedPreferences("prefs", android.content.Context.MODE_PRIVATE)
    }
    single<CommonPreferences> {
        CommonPreferences(get())
    }
}