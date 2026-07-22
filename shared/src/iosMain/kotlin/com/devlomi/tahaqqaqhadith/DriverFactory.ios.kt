package com.devlomi.tahaqqaqhadith

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(Database.Schema, "test.cache")
    }
}