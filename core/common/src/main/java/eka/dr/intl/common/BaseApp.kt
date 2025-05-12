package eka.dr.intl.common

import android.app.Application

open class BaseApp : Application() {
    companion object {
        lateinit var baseAppInstance: BaseApp
    }

    override fun onCreate() {
        super.onCreate()
        baseAppInstance = this
    }
}
