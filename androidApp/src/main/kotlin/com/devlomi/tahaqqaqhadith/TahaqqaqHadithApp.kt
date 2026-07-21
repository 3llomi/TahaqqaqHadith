package com.devlomi.tahaqqaqhadith

import android.app.Application
import com.devlomi.tahaqqaqhadith.di.initKoin
import com.devlomi.tahaqqaqhadith.di.networkModule
import com.devlomi.tahaqqaqhadith.di.sharedModule
import com.devlomi.tahaqqaqhadith.di.useCaseModule
import com.devlomi.tahaqqaqhadith.di.viewModelModule
import org.koin.core.KoinApplication

class TahaqqaqHadithApp: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin{
            modules(
                networkModule(),
                sharedModule,//TODO IS THIS OK to put modules here?
                useCaseModule(),
                viewModelModule()
            )
        }
    }
}