package com.example.djimsdkproject.viewModel

import com.example.djimsdkproject.util.ToastUtils
import dji.sdk.keyvalue.key.DJIKeyInfo
import dji.sdk.keyvalue.value.gimbal.GimbalAngleRotation
import dji.sdk.keyvalue.value.gimbal.GimbalAngleRotationMode
import dji.sdk.keyvalue.key.GimbalKey
import dji.sdk.keyvalue.value.common.EmptyMsg
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.et.action
import dji.v5.et.create
import dji.v5.et.set

class Gimbal: DJIViewModel() {

    fun controlCameraAngle(pitch: Double, yaw: Double, roll: Double, relative: Boolean = false) {
        val gimbalAngleRotation: GimbalAngleRotation = GimbalAngleRotation()

        gimbalAngleRotation.mode = if(relative) GimbalAngleRotationMode.RELATIVE_ANGLE else GimbalAngleRotationMode.ABSOLUTE_ANGLE
        gimbalAngleRotation.pitch = pitch
        gimbalAngleRotation.yaw = yaw
        gimbalAngleRotation.roll = roll

        rotateGimbal(gimbalAngleRotation, object: CommonCallbacks.CompletionCallbackWithParam<EmptyMsg> {
            override fun onSuccess(t: EmptyMsg?) {
                ToastUtils.showToast("success: Gimbal")
            }

            override fun onFailure(error: IDJIError) {
                ToastUtils.showToast("failed: ${error.errorCode()}")
            }
        })
    }

    fun enableVerticalShot(enable: Boolean) {
        GimbalKey.KeyGimbalVerticalShotEnabled.create().set(enable,
            {},
            {}
        )
    }

    private fun rotateGimbal(gimbalAngleRotation: GimbalAngleRotation, callbacks: CommonCallbacks.CompletionCallbackWithParam<EmptyMsg>) {
        GimbalKey.KeyRotateByAngle.create().action(gimbalAngleRotation, {
            callbacks.onSuccess(it)
        }, { e: IDJIError ->
            callbacks.onFailure(e)
        })
    }
}