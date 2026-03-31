package com.batspace.app

import android.app.Application

class BatSpaceApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    companion object {
        lateinit var instance: BatSpaceApp
            private set
    }
}
