package com.example.djimsdkproject.viewModel

import com.dji.wpmzsdk.common.utils.kml.model.Location3D
import dji.sdk.keyvalue.key.FlightControllerKey
import dji.sdk.keyvalue.value.common.Velocity3D
import dji.v5.et.create
import dji.v5.et.get

class FlyData {
    companion object {
        fun getInstance(): FlyData = FlyData()

        fun getVelocity(): Velocity3D? {
            return FlightControllerKey.KeyAircraftVelocity.create().get()
        }

        fun getIsFlying(): Boolean? {
            return FlightControllerKey.KeyIsFlying.create().get()
        }

        fun getLocation(): Location3D? {
            return null
        }
    }
}