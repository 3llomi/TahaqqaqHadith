package com.devlomi.tahaqqaqhadith

import app.cash.sqldelight.db.SqlDriver
import com.devlomi.tahaqqaqhadith.datasource.cache.Database

expect class DriverFactory {
  fun createDriver(): SqlDriver
}

