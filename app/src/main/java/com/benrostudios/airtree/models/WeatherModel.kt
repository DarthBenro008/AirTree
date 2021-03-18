package com.benrostudios.airtree.models


import com.google.gson.annotations.SerializedName

data class WeatherModel(
    var coord: Coord,
    var list: List<WeatherData>
)