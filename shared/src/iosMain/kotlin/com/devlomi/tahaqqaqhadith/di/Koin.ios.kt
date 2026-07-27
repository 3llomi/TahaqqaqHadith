package com.devlomi.tahaqqaqhadith.di

import app.cash.sqldelight.db.SqlDriver
import com.devlomi.tahaqqaqhadith.DriverFactory
import com.devlomi.tahaqqaqhadith.data.CommonPreferences
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule = module{
    single<SqlDriver> {
        DriverFactory().createDriver()
    }
    single<CommonPreferences> {
        CommonPreferences()
    }
}
