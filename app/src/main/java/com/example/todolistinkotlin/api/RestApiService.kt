package com.example.todolistinkotlin.api

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Raghavendra B N on 23/11/2023.
 */
class RestApiService {
    fun send_data(senddata: Api_Request, onResult: (Api_response?) -> Unit){
        try {
            val retrofit = ServiceBuilder.buildService(RestApi::class.java)
            retrofit.send_data(senddata).enqueue(
                object : Callback<Api_response> {
                    override fun onFailure(call: Call<Api_response>, t: Throwable) {
                        Log.d("ToDoListViewModel","Failure")
                        onResult(null)
                    }
                    override fun onResponse(call: Call<Api_response>, response: Response<Api_response>) {
                        Log.d("ToDoListViewModel",response.code().toString())

                        val addedUser = response.body()
                        onResult(addedUser)
                    }
                }
            )
        } catch (e: Exception) {
            Log.d("ToDoListViewModel","Failure")
        }
    }
}