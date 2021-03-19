package com.benrostudios.airtree.models


import com.google.gson.annotations.SerializedName

data class Components(
    var co: Double = 0.0, // 226.97
    var nh3: Double = 0.0, // 0.74
    var no: Double = 0.0, // 0
    var no2: Double = 0.0, // 0.5
    var o3: Double = 0.0, // 75.82
    var pm10: Double = 0.0, // 8.45
    @SerializedName("pm2_5")
    var pm25: Double = 0.0, // 6.73
    var so2: Double = 0.0// 0.17
)