package com.example.resumebuilder.Utils

import android.app.Application
import android.os.Build
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner

private const val NOTIFICATION_CHANNEL_ID = "Resume_Builder_Update"

class MyApp : Application(){

    private var phoneNo: String? = null
    private var grade : String? = null

    fun getPhoneNo(): String? {
        return phoneNo
    }

    fun setphoneNo(value: String?) {
        phoneNo = value
    }

    fun getgrade(): String? {
        return grade
    }

    fun setgrade(value: String) {
        grade = value
    }

    override fun onCreate() {
        super.onCreate()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelName = "Update Reminder"
            val importance = NotificationManager.IMPORTANCE_DEFAULT


            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)

        }
    }

}