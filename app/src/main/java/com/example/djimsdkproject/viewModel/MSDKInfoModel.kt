package com.example.djimsdkproject.viewModel

import dji.v5.common.register.PackageProductCategory
import dji.v5.utils.inner.SDKConfig

class MSDKInfoModel {
    fun isDebug(): Boolean {
        return SDKConfig.getInstance().isDebug
    }

    fun getPackageProductCategory(): PackageProductCategory {
        return SDKConfig.getInstance().packageProductCategory
    }

    fun getSDKVersion(): String {
        return SDKConfig.getInstance().registrationSDKVersion
    }

    fun getBuilderVersion(): String {
        return SDKConfig.getInstance().buildVersion
    }

    fun getCoreInfo(): SDKConfig.CoreInfo {
        return SDKConfig.getInstance().coreInfo
    }
}