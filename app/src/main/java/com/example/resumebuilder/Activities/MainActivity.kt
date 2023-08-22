package com.example.resumebuilder.Activities

import android.app.ProgressDialog
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
import com.example.resumebuilder.Database.AppDB
import com.example.resumebuilder.Models.PersonalDetails
import com.example.resumebuilder.Adapter.RecentRCVadapter
import com.example.resumebuilder.R
import com.example.resumebuilder.Utils.MyApp
import com.example.resumebuilder.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    companion object {
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_MEDIA_IMAGES
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }

                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    add(android.Manifest.permission.READ_MEDIA_IMAGES)
                }
            }.toTypedArray()

    }

    private lateinit var binding : ActivityMainBinding
    private  var cvArrayList : ArrayList<PersonalDetails> = ArrayList()
    private lateinit var imgadapter : RecentRCVadapter
    private lateinit var db : AppDB
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
        }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }





//    private lateinit var progressDialog : ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        requestPermissions()

//        GlobalScope.launch {
//            delay(5000)
//            if (!allPermissionsGranted()){
//                Log.d("MainActivity","permission not granted")
//            }
//        }


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
            Log.d("MainActivity", "notify dataset change called")
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