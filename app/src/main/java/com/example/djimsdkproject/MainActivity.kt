package com.example.djimsdkproject

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.djimsdkproject.pages.BasicControllerFragment
import com.example.djimsdkproject.pages.LiveStreamFragment
import com.example.djimsdkproject.viewModel.Camera
import com.example.djimsdkproject.viewModel.Gimbal

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startButton: Button = findViewById<Button>(R.id.start_button)

        val liveStreamFragment: LiveStreamFragment = LiveStreamFragment()
        val basicControllerFragment: BasicControllerFragment = BasicControllerFragment()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

        startButton.setOnClickListener {
            startButton.visibility = View.INVISIBLE

            transaction.add(R.id.video_frame, liveStreamFragment)
            transaction.add(R.id.controller_frame, basicControllerFragment)

            transaction.commit()
        }
    }
}