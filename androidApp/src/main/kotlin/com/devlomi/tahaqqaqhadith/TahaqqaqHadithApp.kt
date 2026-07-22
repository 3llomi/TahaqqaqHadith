package com.devlomi.tahaqqaqhadith

import android.app.Application
import com.devlomi.tahaqqaqhadith.di.cacheModule
import com.devlomi.tahaqqaqhadith.di.initKoin
import com.devlomi.tahaqqaqhadith.di.networkModule
import com.devlomi.tahaqqaqhadith.di.platformModule
import com.devlomi.tahaqqaqhadith.di.sharedModule
import com.devlomi.tahaqqaqhadith.di.useCaseModule
import com.devlomi.tahaqqaqhadith.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication

class TahaqqaqHadithApp: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin{
            androidContext(this@TahaqqaqHadithApp)
            modules(
                networkModule(),
                sharedModule,//TODO IS THIS OK to put modules here?
                useCaseModule(),
                viewModelModule(),
                cacheModule(),
                platformModule
            )
        }
    }
}