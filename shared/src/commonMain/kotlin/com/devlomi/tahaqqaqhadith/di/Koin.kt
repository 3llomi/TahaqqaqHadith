package com.devlomi.tahaqqaqhadith.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes
import org.koin.dsl.module


val sharedModule = module {
    includes(networkModule(), useCaseModule(), viewModelModule(), cacheModule())
}
expect val platformModule: Module
fun initKoin(config: KoinAppDeclaration? = null): KoinApplication {
    return startKoin {
        includes(config)
        modules(sharedModule,platformModule)
    }
}
