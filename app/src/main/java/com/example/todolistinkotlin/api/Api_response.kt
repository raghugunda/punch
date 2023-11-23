package com.example.todolistinkotlin.api

import com.google.gson.annotations.SerializedName

/**
 * Created by Raghavendra B N on 23/11/2023.
 */

data class Api_response(
    @SerializedName("statusCode") val statusCode: Int?,
    @SerializedName("statusMessage") val statusMessage: String?
)
