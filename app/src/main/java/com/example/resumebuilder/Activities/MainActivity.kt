package com.example.resumebuilder.Activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.resumebuilder.Database.AppDB
import com.example.resumebuilder.Models.PersonalDetails
import com.example.resumebuilder.Adapter.RecentRCVadapter
import com.example.resumebuilder.Notifications.ReminderWorker
import com.example.resumebuilder.R
import com.example.resumebuilder.Utils.MyApp
import com.example.resumebuilder.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {



    private lateinit var binding : ActivityMainBinding
    private  var cvArrayList : ArrayList<PersonalDetails> = ArrayList()
    private lateinit var imgadapter : RecentRCVadapter
    private lateinit var db : AppDB


    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }


//    private lateinit var progressDialog : ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val periodicRequest =
            PeriodicWorkRequestBuilder<ReminderWorker>(15, TimeUnit.MINUTES)
                .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "ReminderWork",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )



//       applicationContext.deleteDatabase("resume-database")

//        progressDialog = ProgressDialog(this)
//        progressDialog.setTitle("Loading")
//        progressDialog.show()


        db = Room.databaseBuilder(applicationContext, AppDB::class.java,"resume-database").build()
        db.openHelper.writableDatabase

        binding.imageButton.setOnClickListener {
            val appInstance = application as MyApp
            appInstance.setphoneNo(null)

            val intent = Intent(this, DataCollectionActivity::class.java)
            startActivity(intent)
        }

        setupRecyclerView()




    }

    override fun onResume() {
        super.onResume()

//        progressDialog = ProgressDialog(this)
//        progressDialog.setTitle("Loading")
//        progressDialog.show()

        if (::imgadapter.isInitialized){
            Log.e("MainActivity", "notify dataset change called")
            setupRecyclerView()

        }
//        progressDialog.dismiss()

    }

    private fun setupRecyclerView(){

        GlobalScope.launch {
            val cvList = db.AppDAO().getAllPersonalDetails()
            cvArrayList.clear()
            cvArrayList.addAll(cvList)

            withContext(Dispatchers.Main){
                imgadapter = RecentRCVadapter(this@MainActivity, cvArrayList,object :RecentRCVadapter.Delete{

                    override fun deleteFromList(position: Int, personalDetails: PersonalDetails) {

                        GlobalScope.launch {
                            db.AppDAO().deleteCV(personalDetails.PhoneNo)
                        }

                        cvArrayList.removeAt(position)
                        imgadapter.notifyItemChanged(position)
                        imgadapter.notifyDataSetChanged()



                        val animation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.glow_animation)
                        binding.rcvList.startAnimation(animation)

//                        progressDialog.dismiss()
                    }

                })
                binding.rcvList.adapter = imgadapter
                binding.rcvList.layoutManager = LinearLayoutManager(this@MainActivity)


            }
        }

    }

}