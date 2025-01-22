package com.example.djimsdkproject.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentManager
import com.example.djimsdkproject.R

class ControlPushFragment: DJIFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_control, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val flightButton: Button = view.findViewById(R.id.button_flight_control)
        val virtualStickButton: Button = view.findViewById(R.id.button_virtual_stick)
        val gimbalButton: Button = view.findViewById(R.id.button_gimbal)
        val zoomButton: Button = view.findViewById(R.id.button_zoom)
        val backButton: Button = view.findViewById(R.id.back_button)

        flightButton.setOnClickListener {

        }

        virtualStickButton.setOnClickListener {

        }

        gimbalButton.setOnClickListener {

        }

        zoomButton.setOnClickListener {

        }

        backButton.setOnClickListener {
            val fragmentManager: FragmentManager = parentFragmentManager

            fragmentManager.popBackStack()
        }
    }
}