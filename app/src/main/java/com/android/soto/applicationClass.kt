package com.android.soto

import android.app.Application
import com.mazenrashed.printooth.Printooth

class applicationClass : Application() {

    override fun onCreate() {
        super.onCreate()
        Printooth.init(this)
    }
}