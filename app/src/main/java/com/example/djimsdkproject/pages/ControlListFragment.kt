package com.example.djimsdkproject.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.djimsdkproject.R
import com.example.djimsdkproject.databinding.FragmentAddControlBinding

class ControlListFragment: DJIFragment() {

    private var _binding: FragmentAddControlBinding? = null
    private val binding: FragmentAddControlBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddControlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentManager: FragmentManager = parentFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack(null)

        binding.buttonFlightControl.setOnClickListener {
            fragmentTransaction.replace(R.id.controller_frame, ControlInputFragment("flightControl"))

            fragmentTransaction.commit()
        }

        binding.buttonVirtualStick.setOnClickListener {
            fragmentTransaction.replace(R.id.controller_frame, ControlInputFragment("virtualStick"))

            fragmentTransaction.commit()
        }

        binding.buttonGimbal.setOnClickListener {
            fragmentTransaction.replace(R.id.controller_frame, ControlInputFragment("gimbal"))

            fragmentTransaction.commit()

        }

        binding.buttonZoom.setOnClickListener {
            fragmentTransaction.replace(R.id.controller_frame, ControlInputFragment("camera"))

            fragmentTransaction.commit()
        }

        binding.backButton.setOnClickListener {
            fragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val fragmentManager: FragmentManager = parentFragmentManager
        fragmentManager.popBackStack()
    }
}