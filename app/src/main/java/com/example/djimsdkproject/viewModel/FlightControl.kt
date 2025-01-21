package com.example.djimsdkproject.viewModel

import dji.sdk.keyvalue.key.FlightControllerKey
import dji.sdk.keyvalue.value.common.EmptyMsg
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.et.action
import dji.v5.et.create

class FlightControl: DJIViewModel() {
    fun startTakeOff(callbacks: CommonCallbacks.CompletionCallbackWithParam<EmptyMsg>) {
        FlightControllerKey.KeyStartTakeoff.create().action({
            callbacks.onSuccess(it)
        }, { e: IDJIError ->
            callbacks.onFailure(e)
        })
    }

    fun startLanding(callbacks: CommonCallbacks.CompletionCallbackWithParam<EmptyMsg>) {
        FlightControllerKey.KeyStartAutoLanding.create().action({
            callbacks.onSuccess(it)
        }, { e: IDJIError ->
            callbacks.onFailure(e)
        })
    }
}