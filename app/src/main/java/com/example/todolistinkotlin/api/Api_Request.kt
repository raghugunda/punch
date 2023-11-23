package com.example.todolistinkotlin.api

import com.google.gson.annotations.SerializedName

/**
 * Created by Raghavendra B N on 23/11/2023.
 */
data class Api_Request (
    @SerializedName("title") val title: String?,
    @SerializedName("time") val time: String?,
    @SerializedName("date") val date: String?
        )