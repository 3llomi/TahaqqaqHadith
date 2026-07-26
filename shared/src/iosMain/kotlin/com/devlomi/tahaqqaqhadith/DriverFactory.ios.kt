package com.devlomi.tahaqqaqhadith

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.devlomi.tahaqqaqhadith.datasource.cache.Database

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(Database.Schema, "test.cache")
    }
}