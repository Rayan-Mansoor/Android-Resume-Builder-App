package com.example.resumebuilder.Activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.resumebuilder.Notifications.ReminderWorker
import com.example.resumebuilder.R
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {

    companion object {
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,

                ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                    clear()
                    add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }

                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    clear()
                    add(android.Manifest.permission.READ_MEDIA_IMAGES)
                    add(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }.toTypedArray()

    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(baseContext,
                    "Permission request denied",
                    Toast.LENGTH_SHORT).show()
            }
            else{
                startActivity(Intent(this,MainActivity::class.java))
            }

        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        if (!allPermissionsGranted()){
            requestPermissions()
        }

        if (allPermissionsGranted()){
            startActivity(Intent(this,MainActivity::class.java))
        }







    }


    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

}