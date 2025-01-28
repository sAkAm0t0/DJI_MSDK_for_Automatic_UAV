package com.example.djimsdkproject.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.djimsdkproject.data.CameraParams
import com.example.djimsdkproject.util.ToastUtils
import com.example.djimsdkproject.data.ControlInfo
import com.example.djimsdkproject.data.FlightControlParams
import com.example.djimsdkproject.data.GimbalParams
import com.example.djimsdkproject.data.VirtualStickParams
import dji.sdk.keyvalue.value.common.EmptyMsg
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class BasicController: ViewModel() {
    companion object {
        private val _controlQueue: MutableLiveData<ArrayDeque<ControlInfo>> = MutableLiveData<ArrayDeque<ControlInfo>>()
        private val coroutineJob: MutableLiveData<Job> = MutableLiveData<Job>()
        private val flightControl: FlightControl = FlightControl()
        private val virtualStick: VirtualStick = VirtualStick()
        private val camera: Camera = Camera()
        private val gimbal: Gimbal = Gimbal()
        private val done: MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)

        init {
            _controlQueue.value = ArrayDeque(listOf())
        }

        fun getInstance(): BasicController = BasicController()

        fun runControlWithQueue() {
            runControl {
                while(!isEmpty()) {
                    val info: ControlInfo = getControl()

                    sendControl(info)
                }
            }
        }

        fun runControlWithInfo(info: ControlInfo) {
            runControl {
                sendControl(info)
            }
        }

        fun destroy() {
            coroutineJob.value?.cancel()
            _controlQueue.value?.clear()
            done.postValue(true)

            val info: ControlInfo = ControlInfo(
                command = "virtualStick",
                params = "{ }",
                runTime = 0,
            )

            runControlWithInfo(info)
        }

        fun addControl(info: ControlInfo) {
            _controlQueue.value?.add(info)
        }

        private fun runControl(func: suspend () -> Unit) {
            if(done.value == false) {
                ToastUtils.showToast("Controller is still running.")

                return
            }

            coroutineJob.value = getInstance().viewModelScope.launch(Dispatchers.Main) {

                done.postValue(false)

                func()

                done.postValue(true)
            }

        }

        private suspend fun sendControl(info: ControlInfo) = withContext(Dispatchers.IO) {
            val control: (String) -> Unit = mapOf<String, (String) -> Unit>(
                "flightControl" to ::controlFlightControl,
                "virtualStick" to ::controlVirtualStick,
                "gimbal" to ::controlGimbal,
                "camera" to ::controlCamera,
            )[info.command] ?: return@withContext

            control(info.params)

            if(info.runTime > 0) Thread.sleep(info.runTime)

            return@withContext
        }

        private fun controlFlightControl(stringParams: String) {
            val flightControlParams: FlightControlParams = Json.decodeFromString<FlightControlParams>(stringParams.trimIndent())
            val command: (CommonCallbacks.CompletionCallbackWithParam<EmptyMsg>) -> Unit = mapOf<String, (CommonCallbacks.CompletionCallbackWithParam<EmptyMsg>)->Unit>(
                "landing" to flightControl::startLanding,
                "takeOff" to flightControl::startTakeOff,
            )[flightControlParams.command] ?: return

            command(object: CommonCallbacks.CompletionCallbackWithParam<EmptyMsg> {
                override fun onSuccess(p0: EmptyMsg?) {

                }

                override fun onFailure(p0: IDJIError) {

                }
            })
        }

        private fun controlVirtualStick(stringParams: String) {
            val virtualStickParams: VirtualStickParams =  Json.decodeFromString<VirtualStickParams>(stringParams.trimIndent())

            if(virtualStick.currentVirtualStickStateInfo.value?.state?.isVirtualStickEnable == false) {
                virtualStick.enableVirtualStick(object: CommonCallbacks.CompletionCallback {
                    override fun onSuccess() {

                    }

                    override fun onFailure(p0: IDJIError) {
                        ToastUtils.showToast("Virtual Stick is disable.")


                    }
                })
            }

            virtualStick.setRightStickPosition(virtualStickParams.rightHorizontal, virtualStickParams.rightVertical)
            virtualStick.setLeftStickPosition(virtualStickParams.leftHorizontal, virtualStickParams.leftVertical)
        }

        private fun controlGimbal(stringParams: String) {
            val gimbalParams: GimbalParams = Json.decodeFromString<GimbalParams>(stringParams.trimIndent())

            gimbal.controlCameraAngle(gimbalParams.pitch, gimbalParams.yaw, gimbalParams.roll, gimbalParams.relative)
        }

        private fun controlCamera(stringParams: String) {
            val cameraParams: CameraParams = Json.decodeFromString<CameraParams>(stringParams.trimIndent())
            val command: (CameraParams) -> Unit = mapOf<String, (CameraParams) -> Unit>(
                "zoom" to ::zoomCamera,
            ).getOrDefault(cameraParams.command, ::recordMultimedia)

            command(cameraParams)
        }

        private fun zoomCamera(params: CameraParams) {
            camera.zoomCamera(params.ratio)
        }

        private fun recordMultimedia(params: CameraParams) {
            val command: () -> Unit = mapOf<String, ()-> Unit>(
                "takePhoto" to camera::takePhoto,
                "startRecording" to camera::startRecording,
                "stopRecording" to camera::stopRecording,
            )[params.command] ?: return

            command()
        }

        private fun getControl(): ControlInfo {
            return _controlQueue.value?.removeFirst() ?: ControlInfo()
        }

         private fun isEmpty(): Boolean {
            return _controlQueue.value?.isEmpty() ?: true
        }

        val controlQueue: MutableLiveData<ArrayDeque<ControlInfo>>
            get() = _controlQueue
    }
}