package com.example.djimsdkproject.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.djimsdkproject.R
import com.example.djimsdkproject.viewModel.BasicController
import com.example.djimsdkproject.viewModel.Camera

class BasicControllerFragment: DJIFragment() {

    private val camera: Camera = Camera()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_basic_controller, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val startButton: Button = view.findViewById(R.id.control_start_button)
        val destroyButton: Button = view.findViewById(R.id.control_stop_button)
        val takePhotoButton: Button = view.findViewById(R.id.take_photo_button)
        val recordButton: Button = view.findViewById(R.id.record_button)
        val addButton: Button = view.findViewById(R.id.add_button)

        startButton.setOnClickListener {
            BasicController.runControlQueue()
        }

        destroyButton.setOnClickListener {
            BasicController.destroy()
        }

        takePhotoButton.setOnClickListener {
            camera.takePhoto()
        }

        recordButton.setOnClickListener {
            if(camera.isRecording == false) {
                recordButton.text = getString(R.string.stop_button)
                camera.startRecordVideo()

                return@setOnClickListener
            }

            recordButton.text = getString(R.string.record_button)
            camera.stopRecordVideo()
        }

        addButton.setOnClickListener {
            val fragmentManager: FragmentManager = parentFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.controller_frame, ControlPushFragment())

            fragmentTransaction.commit()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        BasicController.destroy()
    }
}