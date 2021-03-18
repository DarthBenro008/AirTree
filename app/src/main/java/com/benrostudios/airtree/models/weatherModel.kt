package com.benrostudios.airtree.models


import com.google.gson.annotations.SerializedName

data class weatherModel(
    var coord: Coord,
    var list: List<WeatherData>
)