package com.example.djimsdkproject.pages

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.example.djimsdkproject.R
import com.example.djimsdkproject.databinding.FragmentLivestreamBinding
import com.example.djimsdkproject.viewModel.LiveStream
import com.example.djimsdkproject.util.ToastUtils
import dji.sdk.keyvalue.value.common.ComponentIndexType
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.manager.datacenter.MediaDataCenter
import dji.v5.manager.datacenter.livestream.LiveStreamStatus
import dji.v5.manager.datacenter.livestream.LiveVideoBitrateMode
import dji.v5.manager.datacenter.livestream.StreamQuality
import dji.v5.manager.datacenter.livestream.VideoResolution
import dji.v5.manager.interfaces.ICameraStreamManager
import dji.v5.utils.common.NumberUtils
import dji.v5.utils.common.StringUtils

class LiveStreamFragment : DJIFragment() {
    private var _binding: FragmentLivestreamBinding? = null
    private val binding: FragmentLivestreamBinding
        get() = _binding!!

    private val cameraStreamManager = MediaDataCenter.getInstance().cameraStreamManager

    private val liveStreamVM: LiveStream by viewModels()

    private lateinit var cameraIndex: ComponentIndexType
    private lateinit var tvLiveInfo: TextView
    private lateinit var tvLiveError: TextView
    private lateinit var svCameraStream: SurfaceView
    private var isLocalLiveShow: Boolean = true

    private var cameraStreamSurface: Surface? = null
    private var cameraStreamWidth = -1
    private var cameraStreamHeight = -1
    private var cameraStreamScaleType: ICameraStreamManager.ScaleType = ICameraStreamManager.ScaleType.CENTER_INSIDE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLivestreamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        svCameraStream = binding.svCameraStream

        initRGCamera()
        initRGQuality()
        initRGBitRate()
        initCameraStreamScaleType()
        initLiveStreamScaleType()
        initCameraStream()
        initLiveData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopLive()
        _binding = null
    }

    @SuppressLint("SetTextI18n")
    private fun initLiveData() {
        liveStreamVM.liveStreamStatus.observe(viewLifecycleOwner) { status ->
            var liveStreamStatus = status
            if (liveStreamStatus == null) {
                liveStreamStatus = LiveStreamStatus(0, 0, 0, 0, 0, false, VideoResolution(0, 0))
            }
            val liveWidth = liveStreamStatus.resolution?.width ?: 0
            val liveHeight = liveStreamStatus.resolution?.height ?: 0
            val sourceWidth = liveStreamVM.getAircraftStreamFrameInfo(cameraIndex)?.width ?: 0
            val sourceHeight = liveStreamVM.getAircraftStreamFrameInfo(cameraIndex)?.height ?: 0
            val sourceFps = liveStreamVM.getAircraftStreamFrameInfo(cameraIndex)?.frameRate ?: 0
            val liveGCD = NumberUtils.gcd(liveWidth, liveHeight)
            val sourceGCD = NumberUtils.gcd(sourceWidth, sourceHeight)

            val statusStr = StringBuilder().append(liveStreamStatus)
                .append("source width = $sourceWidth\n")
                .append("source height = $sourceHeight\n")
                .append("source fps = $sourceFps\n")

            if (liveGCD != 0) {
                statusStr.append("live ratio = ${liveWidth / liveGCD}/${liveHeight / liveGCD}\n")
            } else {
                statusStr.append("live ratio = NA\n")
            }

            if (sourceGCD != 0) {
                statusStr.append("source ratio = ${sourceWidth / sourceGCD}/${sourceHeight / sourceGCD}\n")
            } else {
                statusStr.append("source ratio = NA\n")
            }
            tvLiveInfo.text = statusStr.toString()
        }

        liveStreamVM.liveStreamError.observe(viewLifecycleOwner) { error ->
            if (error == null) {
                tvLiveError.text = ""
                tvLiveError.visibility = View.GONE
            } else {
                tvLiveError.text = "error : $error"
                tvLiveError.visibility = View.VISIBLE
            }
        }
    }

    private fun initRGCamera() {
        cameraIndex = ComponentIndexType.find(0)
        cameraStreamSurface = svCameraStream.holder.surface
        if (cameraStreamSurface != null && svCameraStream.width != 0) {
            putCameraStreamSurface()
        }
        liveStreamVM.setCameraIndex(cameraIndex)
    }

    private fun putCameraStreamSurface() {
        if (!isLocalLiveShow) {
            return
        }
        if (cameraIndex == ComponentIndexType.UNKNOWN) {
            return
        }
        cameraStreamSurface?.let {
            cameraStreamManager.putCameraStreamSurface(
                cameraIndex,
                it,
                cameraStreamWidth,
                cameraStreamHeight,
                cameraStreamScaleType
            )
        }
    }

    private fun removeCameraStreamSurface() {
        cameraStreamSurface?.let {
            cameraStreamManager.removeCameraStreamSurface(it)
        }
    }


    private fun initRGQuality() {
        liveStreamVM.setLiveStreamQuality(StreamQuality.find(3))
    }

    private fun initLiveStreamScaleType() {
        liveStreamVM.setLiveStreamScaleType(ICameraStreamManager.ScaleType.find(1))
    }

    private fun initCameraStreamScaleType() {
        cameraStreamScaleType = ICameraStreamManager.ScaleType.find(2)
        putCameraStreamSurface()
    }

    @SuppressLint("SetTextI18n")
    private fun initRGBitRate() {
        liveStreamVM.setLiveVideoBitRateMode(LiveVideoBitrateMode.AUTO)
    }

    private fun initCameraStream() {
        svCameraStream.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {}
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                cameraStreamWidth = width
                cameraStreamHeight = height
                cameraStreamSurface = holder.surface
                putCameraStreamSurface()
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraStreamManager.removeCameraStreamSurface(holder.surface)
            }
        })
    }

    private fun startLive() {
        if (!liveStreamVM.isStreaming()) {
            liveStreamVM.startStream(object : CommonCallbacks.CompletionCallback {
                override fun onSuccess() {
                    ToastUtils.showShortToast(StringUtils.getResStr(R.string.msg_start_live_stream_success))
                }

                override fun onFailure(error: IDJIError) {
                    ToastUtils.showLongToast(
                        StringUtils.getResStr(
                            R.string.msg_start_live_stream_failed,
                            error.description()
                        )
                    )
                }
            });
        }
    }

    private fun stopLive() {
        liveStreamVM.stopStream(null)
    }
}