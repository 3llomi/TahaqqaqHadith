package com.devlomi.tahaqqaqhadith

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform