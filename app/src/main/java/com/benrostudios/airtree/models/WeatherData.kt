package com.benrostudios.airtree.models


import com.google.gson.annotations.SerializedName

data class WeatherData(
    var components: Components,
    var dt: Int, // 1616094000
    var main: Main
)