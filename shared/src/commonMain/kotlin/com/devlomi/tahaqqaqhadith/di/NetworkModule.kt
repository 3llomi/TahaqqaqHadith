package com.devlomi.tahaqqaqhadith.di

import com.devlomi.tahaqqaqhadith.BASE_URL
import com.devlomi.tahaqqaqhadith.data.network.HadithService
import com.devlomi.tahaqqaqhadith.data.network.HadithServiceImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

fun networkModule() = module {

    single {
        HttpClient {
            expectSuccess = true
            install(ContentNegotiation) {
                json(
                    Json {
                        explicitNulls = false
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                    }
                )
            }
            defaultRequest {
                url(BASE_URL)
                contentType(ContentType.Application.Json)
            }
        }
    }
    single<HadithService> { HadithServiceImpl(get(),get(),get()) }


}