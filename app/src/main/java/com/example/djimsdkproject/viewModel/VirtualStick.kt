package com.example.djimsdkproject.viewModel

import androidx.lifecycle.MutableLiveData
import dji.sdk.keyvalue.value.flightcontroller.FlightControlAuthority
import dji.sdk.keyvalue.value.flightcontroller.FlightControlAuthorityChangeReason
import dji.v5.common.callback.CommonCallbacks
import dji.v5.manager.aircraft.virtualstick.VirtualStickManager
import dji.v5.manager.aircraft.virtualstick.VirtualStickState
import dji.v5.manager.aircraft.virtualstick.VirtualStickStateListener

class VirtualStick: DJIViewModel() {
    val currentSpeedLevel: MutableLiveData<Double> = MutableLiveData(0.0)
    val currentVirtualStickStateInfo: MutableLiveData<VirtualStickStateInfo> = MutableLiveData(VirtualStickStateInfo())
    var stickValue: MutableLiveData<RCStickValue> = MutableLiveData<RCStickValue>(RCStickValue(0, 0, 0, 0))

    init {
        currentSpeedLevel.value = VirtualStickManager.getInstance().speedLevel
        VirtualStickManager.getInstance().setVirtualStickStateListener(object : VirtualStickStateListener {
            override fun onVirtualStickStateUpdate(stickState: VirtualStickState) {
                currentVirtualStickStateInfo.postValue(currentVirtualStickStateInfo.value?.apply {
                    this.state = stickState
                })
            }

            override fun onChangeReasonUpdate(reason: FlightControlAuthorityChangeReason) {
                currentVirtualStickStateInfo.postValue(currentVirtualStickStateInfo.value?.apply {
                    this.reason = reason
                })
            }
        })
    }

    fun enableVirtualStick(callback: CommonCallbacks.CompletionCallback) {
        VirtualStickManager.getInstance().enableVirtualStick(callback)
    }

    fun disableVirtualStick(callback: CommonCallbacks.CompletionCallback) {
        VirtualStickManager.getInstance().disableVirtualStick(callback)
    }

    fun setSpeedLevel(speedLevel: Double) {
        VirtualStickManager.getInstance().speedLevel = speedLevel
        currentSpeedLevel.value = speedLevel
    }

    fun setLeftStickPosition(horizontal: Int, vertical: Int) {
        VirtualStickManager.getInstance().leftStick.horizontalPosition = horizontal
        VirtualStickManager.getInstance().leftStick.verticalPosition = vertical
    }

    fun setRightStickPosition(horizontal: Int, vertical: Int) {
        VirtualStickManager.getInstance().rightStick.horizontalPosition = horizontal
        VirtualStickManager.getInstance().rightStick.verticalPosition = vertical
    }

    data class VirtualStickStateInfo(
        var state: VirtualStickState = VirtualStickState(false, FlightControlAuthority.UNKNOWN, false),
        var reason: FlightControlAuthorityChangeReason = FlightControlAuthorityChangeReason.UNKNOWN
    )

    data class RCStickValue (
        var leftHorizontal: Int,
        var leftVertical: Int,
        var rightHorizontal: Int,
        var rightVertical: Int
    )
}