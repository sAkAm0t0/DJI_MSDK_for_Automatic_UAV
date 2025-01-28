package com.example.djimsdkproject.data

import kotlinx.serialization.Serializable

data class ControlInfo(
    val command: String = "",
    val params: String = "{}",
    val runTime: Long = 0,
)

@Serializable
data class FlightControlParams(
    val command: String = "take off"
)

@Serializable
data class VirtualStickParams(
    val leftVertical: Int = 0,
    val leftHorizontal: Int = 0,
    val rightVertical: Int = 0,
    val rightHorizontal: Int = 0,
)

@Serializable
data class GimbalParams(
    val pitch: Double = 0.0,
    val yaw: Double = 0.0,
    val roll: Double = 0.0,
    val relative: Boolean = false,
)

@Serializable
data class CameraParams(
    val command: String = "zoom",
    val ratio: Double = 1.0,
)
