package com.example.todolistinkotlin

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.todolistinkotlin.api.Api_Request
import com.example.todolistinkotlin.api.RestApiService
import com.example.todolistinkotlin.database.ToDoListDataEntity
import com.example.todolistinkotlin.database.ToDoListDatabase
import com.example.todolistinkotlin.notification.AlarmReceiver
import java.util.*
import java.util.logging.Logger

/**
 *   Created by Sundar Pichai on 5/8/19.
 */
class ToDoListViewModel(val context: Application) : AndroidViewModel(context) {
    var toDoListData = MutableLiveData<ToDoListData>()

    var database: ToDoListDatabase? = null

    var getAllData = mutableListOf(ToDoListDataEntity())
    val toDoList = MutableLiveData<List<ToDoListDataEntity>>()
    val LOG = Logger.getLogger(this.javaClass.name)

    init {
        database = ToDoListDatabase.getInstance(context)
        database?.toDoListDao()?.getAll()?.let {
            getAllData = it as MutableList<ToDoListDataEntity>
        }
    }

    var title = ObservableField<String>("")
    var date = ObservableField<String>("")
    var time = ObservableField<String>("")

    var month = 0
    var day = 0
    var year = 0

    var hour = 0
    var minute = 0

    var position: Int = -1
    var index: Long = -1


    @RequiresApi(Build.VERSION_CODES.M)
    fun click(v: View) {


        LOG.info( "click")
        if (title.get().toString().isNotBlank() && date.get().toString().isNotBlank() && time.get().toString().isNotBlank()) {
            if (checkInternetConnection(context)){
                LOG.info("Sending Data To Server")
                send_data_to_server(title.get().toString(), date.get().toString(), time.get().toString(), id = index)
            }else{
                LOG.info("Internet Not Available. Data Saved In Local Database")
                addData(title.get().toString(), date.get().toString(), time.get().toString(), id = index)
            }

            title.set("")
            date.set("")
            time.set("")
        }else{
            LOG.info("Enter All Filed data")
            Toast.makeText(context,"Enter All Filed data",Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun send_data_to_server(title: String, date: String, time: String, id: Long) {
        try {
            val apiService = RestApiService()
            val api_data = Api_Request(
                title = title ,
                date = date ,
                time = time )

            apiService.send_data(api_data) {
                if (it?.statusCode == 200) {
                    Toast.makeText(context, "Data Updated", Toast.LENGTH_LONG).show()
                    LOG.info("Data Updated. Status Code:-"+it.statusCode.toString())
                } else {
                    Toast.makeText(context, "Data Not Synced To Server, Sync Manually", Toast.LENGTH_LONG).show()
                    LOG.info("Data Not Updated. Status Code:-"+it?.statusCode.toString())
                }
                addData(title, date, time, id = index) //Saving data to Local
            }
        }catch (e:Exception){
            Toast.makeText(context, "Unable to Connect to Server", Toast.LENGTH_LONG).show()
            LOG.info("Data Not Updated. Exception:-"+e.message.toString())
        }

    }

    fun checkInternetConnection(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            networkCapabilities != null && (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR
            ) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || networkCapabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_BLUETOOTH
            ))
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo
            nwInfo != null && nwInfo.isConnected
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @WorkerThread
    private fun addData(title: String, date: String, time: String, id: Long) {
        //database?.toDoListDao()?.insert(ToDoListDataEntity(title = title, date = date, time = time))
        if (position != -1) {
            LOG.info("Data Updated")
            database?.toDoListDao()?.update(title = title, date = date, time = time, id = id)
        } else {
            LOG.info("Data Inserted")
            val newId = database?.toDoListDao()?.insert(ToDoListDataEntity(title = title, date = date, time = time, isShow = 0))

            val cal : Calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault())

            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.DAY_OF_MONTH, day)

            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)


            LOG.info( "Alarm Title"+"$month , $date : ${cal.time}")
            newId?.let {
                setAlarm(cal, 0, it, title,hour,minute)
            }

        }

        database?.toDoListDao()?.getAll().let {
            getAllData = it as MutableList<ToDoListDataEntity>
            getPreviousList()
        }
    }


    fun getPreviousList() {
        toDoList.value = getAllData
    }

    fun delete(id: Long) {
        database?.toDoListDao()?.Delete(id)
        database?.toDoListDao()?.getAll().let {
            getAllData = it as MutableList<ToDoListDataEntity>
            getPreviousList()
        }
        LOG.info("Data Deleted")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setAlarm(calender: Calendar, i: Int, id: Long, title: String, hour:Int,minute:Int) {

        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("INTENT_NOTIFY", true)
        intent.putExtra("isShow", i)
        intent.putExtra("id", id)
        intent.putExtra("title", title)
        intent.putExtra("date","Time-> $hour:$minute")
        val pandingIntent: PendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (i == 0) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,  calender.timeInMillis , pandingIntent)
            LOG.info("Notification Sent")
        } else {
            LOG.info("Notification Not Sent")
            alarmManager.cancel(pandingIntent)
        }
    }
}