package com.example.resumebuilder.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.resumebuilder.Database.AppDB
import com.example.resumebuilder.Models.PersonalDetails
import com.example.resumebuilder.R
import com.example.resumebuilder.Utils.MyApp
import com.example.resumebuilder.databinding.ActivityPersonalDetailsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PersonalDetailsActivity : AppCompatActivity() {
    private val SELECT_IMAGE_REQUEST = 1
    private lateinit var binding : ActivityPersonalDetailsBinding
    private lateinit var db : AppDB
    private lateinit var imageURI : Uri
    val TAG = "CURRENT NAME "

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPersonalDetailsBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = Room.databaseBuilder(applicationContext, AppDB::class.java,"resume-database").build()
        db.openHelper.writableDatabase

        imageURI = Uri.parse("000")

        binding.YourImg.setOnClickListener {
            selectImageFromGallery()
        }


        val appInstance = application as MyApp
        GlobalScope.launch {
            var prevPerObj = appInstance.getPhoneNo()?.let { db.AppDAO().getPersonalDetails(it) }
            if (prevPerObj != null){
                withContext(Dispatchers.Main){
                    binding.nameET.setText(prevPerObj.Name)
                    binding.emailET.setText(prevPerObj.Email)
                    binding.phoneET.setText(prevPerObj.PhoneNo)
                    binding.YourImg.setImageURI(Uri.parse(prevPerObj.yourImg))
                }
            }
        }


        binding.saveBtn.setOnClickListener {
            var perDetObj = PersonalDetails(binding.nameET.text.toString(), binding.emailET.text.toString(), binding.phoneET.text.toString(), imageURI.toString())
            GlobalScope.launch {
                Log.d("PersonalDetailsActivity",perDetObj.PhoneNo)

                val appInstance = application as MyApp
                appInstance.setphoneNo(perDetObj.PhoneNo)
                Log.d("abcd", appInstance.getPhoneNo()!!)

                db.AppDAO().addPeronalDetails(perDetObj)

                Log.d("PersonalDetailsActivity", imageURI.toString())


                finish()

            }

            Toast.makeText(applicationContext, "Changes Saved", Toast.LENGTH_SHORT).show()

        }
    }

    private fun selectImageFromGallery() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            imageURI = uri
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            contentResolver.takePersistableUriPermission(uri, flag)
            Log.d("PhotoPicker", "Selected URI: $uri")
            Glide.with(this)
                .load(uri)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.baseline_person_24)  // Optional placeholder image
                        .error(R.drawable.baseline_error_24))  // Optional error image
                .diskCacheStrategy(DiskCacheStrategy.ALL)  // Caching strategy
                .into(binding.YourImg)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageURI = data.data!!

            grantUriPermission(
                packageName,
                imageURI,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

//            binding.YourImg.setImageURI(imageURI)



        }
    }
}