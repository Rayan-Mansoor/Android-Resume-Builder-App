package com.example.resumebuilder.Notifications

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.resumebuilder.Activities.DataCollectionActivity
import com.example.resumebuilder.Activities.MainActivity
import com.example.resumebuilder.Database.AppDB
import com.example.resumebuilder.Models.PersonalDetails
import com.example.resumebuilder.R
import kotlin.coroutines.CoroutineContext

private const val NOTIFICATION_CHANNEL_ID = "Resume_Builder_Update"

class ReminderWorker(private val context: Context, private val workerParameters: WorkerParameters) : CoroutineWorker(context,workerParameters) {
    private lateinit var db : AppDB

    override suspend fun doWork(): Result {
        Log.d("ReminderWorker", "Worker up and running")
        db = Room.databaseBuilder(applicationContext, AppDB::class.java,"resume-database").build()
        db.openHelper.writableDatabase
        val randomCV = db.AppDAO().getRandomCV()

        notifyUser(randomCV)
        return Result.success()
    }

    private fun notifyUser(randomPerson : PersonalDetails) {
        val intent = DataCollectionActivity.newIntent(context, randomPerson)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat
            .Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.resume_icon)
            .setContentTitle("Hey! ${randomPerson.Name}")
            .setContentText("Hey! Make sure to update your resume with your latest achievements")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        if (ActivityCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS") == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify(0, notification)
        }
    }
}