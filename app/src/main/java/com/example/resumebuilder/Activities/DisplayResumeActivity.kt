package com.example.resumebuilder.Activities

import android.app.ActionBar
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.room.Room
import com.example.resumebuilder.databinding.ActivityDisplayResumeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.resumebuilder.Database.AppDB
import com.example.resumebuilder.Models.EducationDetails
import com.example.resumebuilder.Utils.MyApp
import com.example.resumebuilder.Models.PersonalDetails
import com.example.resumebuilder.Models.SkillDetails
import java.io.IOException
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.core.content.FileProvider
import com.example.resumebuilder.Models.ExperienceDetails
import com.example.resumebuilder.Models.ProjectDetails
import java.io.File
import java.io.FileOutputStream
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.resumebuilder.R
import com.example.resumebuilder.Utils.FileUtils

class DisplayResumeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDisplayResumeBinding
    private lateinit var db : AppDB
    private lateinit var perDet : PersonalDetails
    private lateinit var eduDet : List<EducationDetails>
    private lateinit var sklDet : List<SkillDetails>
    private lateinit var expDet : List<ExperienceDetails>
    private lateinit var projDet : List<ProjectDetails>
    private lateinit var imageUri : Uri
    private lateinit var pdfUri : Uri

    private val REQUEST_CODE_PERMISSIONS = 101


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDisplayResumeBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = Room.databaseBuilder(applicationContext, AppDB::class.java,"resume-database").build()
        db.openHelper.writableDatabase


//        Log.d("DisplayResumeActivity",MyApp().getPhoneNo()!!)

        GlobalScope.launch {
            val appInstance = application as MyApp
            appInstance.getPhoneNo()
//            Log.d("DisplayResumeActivity", appInstance.getPhoneNo()!!)
            if (appInstance.getPhoneNo() != null){
                perDet = db.AppDAO().getPersonalDetails(appInstance.getPhoneNo()!!)
                eduDet = db.AppDAO().getEducationDetails(appInstance.getPhoneNo()!!)
                sklDet = db.AppDAO().getSkillDetails(appInstance.getPhoneNo()!!)
                expDet = db.AppDAO().getExperienceDetails(appInstance.getPhoneNo()!!)
                projDet = db.AppDAO().getProjectDetails(appInstance.getPhoneNo()!!)
            }

            if (::perDet.isInitialized){
                withContext(Dispatchers.Main){
                    val parsedUri = Uri.parse(perDet.yourImg)

                    Log.d("DisplayResumeActivity", parsedUri.toString())

                    val FileUtils = FileUtils(this@DisplayResumeActivity)

                    val path = FileUtils.getPath(parsedUri)

                    Glide.with(this@DisplayResumeActivity)
                        .load(path)
                        .apply(RequestOptions()
                            .placeholder(R.drawable.baseline_person_24)  // Optional placeholder image
                            .error(R.drawable.baseline_error_24))  // Optional error image
                        .diskCacheStrategy(DiskCacheStrategy.ALL)  // Caching strategy
                        .into(binding.cvImg)

//                    binding.cvImg.setImageURI(parsedUri)
                    binding.cvName.text = perDet.Name
                    binding.cvEmail.text = perDet.Email
                    binding.cvPhone.text = perDet.PhoneNo

                    if (::eduDet.isInitialized){
                        for (i in 0 until eduDet.size){
                            println("Index: $i, Uni: ${eduDet[i].uni}, Grade: ${eduDet[i].grade}, Course: ${eduDet[i].course}, Year: ${eduDet[i].year}")

                            if(i!=0){
                                val params = binding.guideline13.layoutParams as LayoutParams
                                params.guidePercent+=0.16f
                                binding.guideline13.layoutParams = params


                                val params2 = binding.guideline19.layoutParams as LayoutParams
                                params2.guidePercent+=0.16f
                                binding.guideline19.layoutParams = params2


                                val params3 = binding.guideline20.layoutParams as LayoutParams
                                params3.guidePercent+=0.16f
                                binding.guideline20.layoutParams = params3
                            }

                            val cvUniId = resources.getIdentifier("cvUni${i}", "id", packageName)
                            val cvUni = findViewById<TextView>(cvUniId)
                            cvUni.text = eduDet[i].uni
                            cvUni.visibility = View.VISIBLE

                            val cvGradeId = resources.getIdentifier("cvGrade${i}", "id", packageName)
                            val cvGrade = findViewById<TextView>(cvGradeId)
                            cvGrade.text = eduDet[i].grade
                            cvGrade.visibility = View.VISIBLE

                            val cvdegreeId = resources.getIdentifier("cvdegree${i}", "id", packageName)
                            val cvdegree = findViewById<TextView>(cvdegreeId)
                            cvdegree.text = eduDet[i].course
                            cvdegree.visibility = View.VISIBLE

                            val cvYearId = resources.getIdentifier("cvYear${i}", "id", packageName)
                            val cvYear = findViewById<TextView>(cvYearId)
                            cvYear.text = eduDet[i].year
                            cvYear.visibility = View.VISIBLE

                        }
                    }

                    if (::sklDet.isInitialized){
                        for (i in 0 until sklDet.size){

//                            println("Index: $i, Uni: ${eduDet[i].uni}, Grade: ${eduDet[i].grade}, Course: ${eduDet[i].course}, Year: ${eduDet[i].year}")

                            if(i!=0){
                                val params = binding.guideline19.layoutParams as LayoutParams
                                params.guidePercent+=0.04f
                                binding.guideline19.layoutParams = params

                                val params2 = binding.guideline20.layoutParams as LayoutParams
                                params2.guidePercent+=0.04f
                                binding.guideline20.layoutParams = params2
                            }

                            val cvSklId = resources.getIdentifier("cvskl${i}", "id", packageName)
                            val cvSkl = findViewById<TextView>(cvSklId)
                            cvSkl.text = sklDet[i].name
                            cvSkl.visibility = View.VISIBLE

                        }
                    }

                    if (::expDet.isInitialized){
                        for (i in 0 until expDet.size){

                            if(i!=0){
                                val params = binding.guideline20.layoutParams as LayoutParams
                                params.guidePercent+=0.04f
                                binding.guideline20.layoutParams = params
                            }

                            val cvCompId = resources.getIdentifier("cvComp${i}", "id", packageName)
                            val cvComp = findViewById<TextView>(cvCompId)
                            cvComp.text = expDet[i].company

                            cvComp.visibility = View.VISIBLE


                            val cvTenId = resources.getIdentifier("cvTen${i}", "id", packageName)
                            val cvTen = findViewById<TextView>(cvTenId)
                            cvTen.text = expDet[i].startDateMonth.toString() + "/" + expDet[i].startDateYear+ " to " +expDet[i].endDateMonth.toString() + "/" + expDet[i].endDateYear
                            cvTen.visibility = View.VISIBLE
                        }
                    }

                    if (::projDet.isInitialized){
                        for (i in 0 until projDet.size){
                            val cvProjId = resources.getIdentifier("cvProj${i}", "id", packageName)
                            val cvProj = findViewById<TextView>(cvProjId)
                            cvProj.text = projDet[i].Name
                            cvProj.visibility = View.VISIBLE


                            val cvPyearId = resources.getIdentifier("cvPYear${i}", "id", packageName)
                            val cvPyear = findViewById<TextView>(cvPyearId)
                            cvPyear.text = projDet[i].Year
                            cvPyear.visibility = View.VISIBLE
                        }
                    }
                }
            }

        }

        binding.saveCV.setOnClickListener {
            binding.saveCV.visibility = View.GONE
            captureScreen()
            Toast.makeText(this,"Resume PDF saved",Toast.LENGTH_SHORT).show()
            binding.saveCV.visibility = View.VISIBLE
        }

        binding.shareCV.setOnClickListener {
            binding.saveCV.visibility = View.GONE
            binding.shareCV.visibility = View.GONE
            captureScreen()
            sharePdfThroughWhatsApp(this, pdfUri)
            binding.saveCV.visibility = View.VISIBLE
            binding.shareCV.visibility = View.VISIBLE
        }



    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun sharePdfThroughWhatsApp(context: Context, pdfUri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, pdfUri)
            setPackage("com.whatsapp") // Set the package name for WhatsApp
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share PDF via"))
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun captureScreen() {
        val rootView = window.decorView.rootView
        rootView.isDrawingCacheEnabled = true
        val screenshot = Bitmap.createBitmap(rootView.drawingCache)
        rootView.isDrawingCacheEnabled = false

        setScreenshot(screenshot)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setScreenshot(screenshot: Bitmap){
//        binding.cvImg.setImageBitmap(screenshot)
        pdfUri = convertImageToPdfAndSave(applicationContext, screenshot,binding.cvName.text.toString())
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun convertImageToPdfAndSave(context: Context, bitmap: Bitmap, pdfName: String) : Uri {
        val pdfDocument = PdfDocument()

        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        canvas.drawBitmap(bitmap, 0f, 0f, null)

        pdfDocument.finishPage(page)

        val pdfFileName = "${pdfName}_CV.pdf"
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, pdfFileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
        }

        val contentUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val uri = context.contentResolver.insert(contentUri, values)

        uri?.let {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                pdfDocument.writeTo(outputStream)
                pdfDocument.close()
            }
        }

        return uri!!
    }
}