package com.example.todolistinkotlin.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Created by Raghavendra B N on 23/11/2023.
 */
interface RestApi {
    @Headers("Content-Type: application/json")
    @POST("eventdata")
    fun send_data(@Body userData: Api_Request): Call<Api_response>
}