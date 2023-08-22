package com.example.resumebuilder.Utils

import android.app.Application
import android.os.Build
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner

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
}