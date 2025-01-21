package com.example.djimsdkproject

import android.app.Application
import android.content.Context
import com.example.djimsdkproject.viewModel.MSDKManager
import com.example.djimsdkproject.viewModel.globalViewModels



class MyApplication : Application() {
    private val msdkManager: MSDKManager by globalViewModels()

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        com.cySdkyc.clx.Helper.install(this)
    }

    override fun onCreate() {
        super.onCreate()

        msdkManager.initMobileSDK(this)
    }
}