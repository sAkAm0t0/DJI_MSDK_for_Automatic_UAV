package com.example.djimsdkproject.viewModel

import com.example.djimsdkproject.util.ToastUtils
import dji.sdk.keyvalue.key.CameraKey
import dji.sdk.keyvalue.key.DJIKey
import dji.sdk.keyvalue.value.camera.CameraMode
import dji.v5.et.action
import dji.v5.et.create
import dji.v5.et.get
import dji.v5.et.set

class Camera {
    private val cameraMode: DJIKey<CameraMode> = CameraKey.KeyCameraMode.create()

    fun zoomCamera(ratio: Double) {
        CameraKey.KeyCameraZoomRatios.create().set(ratio,
            {

            },
            {

            }
        )
    }

    fun takePhoto() {
        if(CameraKey.KeyIsShootingPhoto.create().get() == true) return

        if(cameraMode.get() != CameraMode.PHOTO_NORMAL) cameraMode.set(CameraMode.PHOTO_NORMAL)

        CameraKey.KeyStartShootPhoto.create().action()
    }

    fun startRecordVideo() {
        if(CameraKey.KeyIsRecording.create().get() == true) return

        if(cameraMode.get() != CameraMode.VIDEO_NORMAL) cameraMode.set(CameraMode.VIDEO_NORMAL)

        CameraKey.KeyStartRecord.create().action()
    }

    fun stopRecordVideo() {
        if(CameraKey.KeyIsRecording.create().get() == false) return

        CameraKey.KeyStopRecord.create().action()
    }

    val isRecording: Boolean?
        get() = CameraKey.KeyIsRecording.create().get()
}