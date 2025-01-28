package com.example.djimsdkproject.data

const val DEFAULT_STR = "N/A"
const val IN_INNER_NETWORK_STR = "IN_INNER"
const val IN_OUT_NETWORK_STR = "IN_OUT"
const val NO_NETWORK_STR = "NO_NETWORK"
const val ONLINE_STR = "ONLINE"
const val MAIN_FILE_DETAILS_STR = "MAIN_FRAGMENT_PAGE_TITLE"

val FLIGHT_CONTROL_PARAM = mapOf<String, String>(
    "command" to "takeOff, landing",
    "runTime" to "0",
)

val VIRTUAL_STICK_PARAM = mapOf<String, String>(
    "leftVertical" to "0",
    "leftHorizontal" to "0",
    "rightVertical" to "0",
    "rightHorizontal" to "0",
    "runTime" to "0",
)

val GIMBAL_PARAM = mapOf<String, String>(
    "pitch" to "0.0",
    "yaw" to "0.0",
    "roll" to "0.0",
    "runTime" to "0",
    "relative" to "false, true",
)

val ZOOM_PARAM = mapOf<String, String>(
    "command" to "zoom",
    "ratio" to "1.0",
    "runTime" to "0",
)