package com.example.djimsdkproject.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.example.djimsdkproject.R
import com.example.djimsdkproject.databinding.FragmentBasicControllerBinding
import com.example.djimsdkproject.viewModel.BasicController
import com.example.djimsdkproject.viewModel.Camera

class BasicControllerFragment: DJIFragment() {

    private val camera: Camera = Camera()
    private var _binding: FragmentBasicControllerBinding? = null
    private val binding: FragmentBasicControllerBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBasicControllerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.controlLogText.text = fun(): String {
            var text = ""

            BasicController.controlQueue.value?.forEach {
                text += "${it.command}: ${it.params} (${it.runTime} ms)\n"
            }

            return text
        }()

        binding.controlStartButton.setOnClickListener {
            BasicController.runControlWithQueue()
        }

        binding.controlStopButton.setOnClickListener {
            BasicController.destroy()
        }

        binding.takePhotoButton.setOnClickListener {
            camera.takePhoto()
        }

        binding.recordButton.setOnClickListener {
            if(camera.isRecording == false) {
                binding.recordButton.text = getString(R.string.stop_button)
                camera.startRecording()

                return@setOnClickListener
            }

            binding.recordButton.text = getString(R.string.record_button)
            camera.stopRecording()
        }

        binding.addButton.setOnClickListener {
            val fragmentManager: FragmentManager = parentFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.controller_frame, ControlListFragment())

            fragmentTransaction.commit()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}