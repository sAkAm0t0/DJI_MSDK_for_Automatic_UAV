package com.example.djimsdkproject.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.djimsdkproject.util.ToastUtils
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BasicController: ViewModel() {
    companion object {
        private val _controlQueue: MutableLiveData<ArrayDeque<ControlInfo>> = MutableLiveData<ArrayDeque<ControlInfo>>()
        private val coroutineJob: MutableLiveData<Job> = MutableLiveData<Job>()
        private val virtualStick: VirtualStick = VirtualStick()
        private val done: MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)

        fun getInstance(): BasicController = BasicController()

        fun runControlQueue() {
            coroutineJob.value = getInstance().viewModelScope.launch(Dispatchers.Main) {
                if(done.value == false) {
                    ToastUtils.showToast("Controller is still running.")

                    return@launch
                }

                if(virtualStick.currentVirtualStickStateInfo.value?.state?.isVirtualStickEnable == false) {
                    virtualStick.enableVirtualStick(object: CommonCallbacks.CompletionCallback {
                        override fun onSuccess() {

                        }

                        override fun onFailure(p0: IDJIError) {
                            ToastUtils.showToast("Virtual Stick is disable.")

                            return
                        }
                    })
                }

                done.postValue(false)

                do {
                    val info = getControl()

                    sendControl(info)
                } while (!isEmpty())

                done.postValue(true)
            }
        }

        private suspend fun sendControl(info: ControlInfo) = withContext(Dispatchers.IO) {
            virtualStick.setSpeedLevel(info.speed)

            virtualStick.setLeftStickPosition(info.leftHorizontal, info.leftVertical)
            virtualStick.setRightStickPosition(info.rightHorizontal, info.rightVertical)

            if(info.runTime > 0) Thread.sleep(info.runTime)

            return@withContext
        }

        fun destroy() {
            coroutineJob.value?.cancel()
            _controlQueue.value?.clear()
            done.postValue(true)

            runControlQueue()
        }

        fun addControl(info: ControlInfo) {
            _controlQueue.value?.add(info)
        }

        private fun getControl(): ControlInfo {
            return _controlQueue.value?.removeFirst() ?: ControlInfo()
        }

         private fun isEmpty(): Boolean {
            return _controlQueue.value?.isEmpty() ?: true
        }

        val controlQueue: MutableLiveData<ArrayDeque<ControlInfo>>
            get() = _controlQueue

        data class ControlInfo(
            val leftHorizontal: Int = 0,
            val leftVertical: Int = 0,
            val rightHorizontal: Int = 0,
            val rightVertical: Int = 0,
            val speed: Double = virtualStick.currentSpeedLevel.value ?: 0.0,
            val runTime: Long = 0,
        )
    }
}