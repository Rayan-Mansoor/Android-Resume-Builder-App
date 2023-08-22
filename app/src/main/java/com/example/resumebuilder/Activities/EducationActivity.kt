package com.example.resumebuilder.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.resumebuilder.Adapter.EducationRCVadapter
import com.example.resumebuilder.Adapter.ExperienceRCVadapter
import com.example.resumebuilder.Adapter.ProjectsRCVadapter
import com.example.resumebuilder.Adapter.SkillRCVadapter
import com.example.resumebuilder.Database.AppDB
import com.example.resumebuilder.Models.EducationDetails
import com.example.resumebuilder.Models.ExperienceDetails
import com.example.resumebuilder.Models.ProjectDetails
import com.example.resumebuilder.Models.SkillDetails
import com.example.resumebuilder.R
import com.example.resumebuilder.Utils.MyApp
import com.example.resumebuilder.databinding.ActivityEducationBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EducationActivity : AppCompatActivity() {
    private lateinit var binding :ActivityEducationBinding
    private lateinit var db : AppDB
    private  var educationArrayList : ArrayList<EducationDetails> = ArrayList()
    private lateinit var eduAdapter : EducationRCVadapter

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEducationBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = Room.databaseBuilder(applicationContext, AppDB::class.java,"resume-database").build()
        db.openHelper.writableDatabase

        val appInstance = application as MyApp
        if (appInstance.getPhoneNo() != null){
            setupRecyclerView()
        }



        binding.saveEduBtn.setOnClickListener {
            val appInstance = application as MyApp
            var eduDetObj = EducationDetails(binding.courseET.text.toString(), binding.uniET.text.toString(), binding.gradeET.text.toString(), binding.eduYearET.text.toString(), appInstance.getPhoneNo()!!)
            GlobalScope.launch {

                val appInstance = application as MyApp
                appInstance.setgrade(eduDetObj.grade)

                db.AppDAO().addeducationDetails(eduDetObj)

                withContext(Dispatchers.Main){
                    setupRecyclerView()
                    val animation = AnimationUtils.loadAnimation(this@EducationActivity, R.anim.glow_animation)
                    binding.eduRCV.startAnimation(animation)

                }

            }

            Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()
        }

        binding.closeBtn.setOnClickListener {
            finish()
        }

        binding.newEduBtn.setOnClickListener{
            binding.courseET.setText("")
            binding.uniET.setText("")
            binding.gradeET.setText("")
            binding.eduYearET.setText("")
        }
   }

    private fun setupRecyclerView(){
        val appInstance = application as MyApp
        if (appInstance.getPhoneNo() == null){
            return
        }

        GlobalScope.launch {
            val cvList: List<EducationDetails> = db.AppDAO().getEducationDetails(appInstance.getPhoneNo()!!)
            educationArrayList.clear()
            educationArrayList.addAll(cvList)

            withContext(Dispatchers.Main){
                eduAdapter = EducationRCVadapter(this@EducationActivity, educationArrayList, object :
                    EducationRCVadapter.Delete{

                    override fun deleteFromList(position: Int, educationDetails: EducationDetails) {

                        GlobalScope.launch {
                            db.AppDAO().deleteEducation(educationDetails.FP, educationDetails.uni , educationDetails.course)
                        }

                        educationArrayList.removeAt(position)
                        eduAdapter.notifyItemChanged(position)
                        eduAdapter.notifyDataSetChanged()
                        val animation = AnimationUtils.loadAnimation(this@EducationActivity, R.anim.glow_animation)
                        binding.eduRCV.startAnimation(animation)

                    }

                }, object :
                    EducationRCVadapter.Populate{
                    override fun populateFields(position: Int, educationDetails: EducationDetails) {
                        binding.courseET.setText(educationDetails.course)
                        binding.uniET.setText(educationDetails.uni)
                        binding.gradeET.setText(educationDetails.grade)
                        binding.eduYearET.setText(educationDetails.year)
                    }

                })
                binding.eduRCV.adapter = eduAdapter
                binding.eduRCV.layoutManager = LinearLayoutManager(this@EducationActivity)
            }
        }
    }
}