package com.example.djimsdkproject

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.djimsdkproject.databinding.ActivityMainBinding
import com.example.djimsdkproject.pages.BasicControllerFragment
import com.example.djimsdkproject.pages.LiveStreamFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

        binding.startButton.setOnClickListener {
            binding.startButton.visibility = View.INVISIBLE

            transaction.add(binding.videoFrame.id, LiveStreamFragment())
            transaction.add(binding.controllerFrame.id, BasicControllerFragment())

            transaction.commit()
        }
    }
}