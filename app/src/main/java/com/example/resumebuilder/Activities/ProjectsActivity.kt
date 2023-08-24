package com.example.resumebuilder.Activities

import android.content.res.Resources
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.resumebuilder.Adapter.ProjectsRCVadapter
import com.example.resumebuilder.Adapter.SkillRCVadapter
import com.example.resumebuilder.Database.AppDB
import com.example.resumebuilder.Models.EducationDetails
import com.example.resumebuilder.Models.ProjectDetails
import com.example.resumebuilder.Models.SkillDetails
import com.example.resumebuilder.R
import com.example.resumebuilder.Utils.MyApp
import com.example.resumebuilder.databinding.ActivityEducationBinding
import com.example.resumebuilder.databinding.ActivityProjectsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max


class ProjectsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProjectsBinding
    private lateinit var db : AppDB
    private  var projectArrayList : ArrayList<ProjectDetails> = ArrayList()
    private lateinit var projAdapter : ProjectsRCVadapter

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityProjectsBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = Room.databaseBuilder(applicationContext, AppDB::class.java,"resume-database").build()
        db.openHelper.writableDatabase

        setupRecyclerView()


        binding.saveProjBtn.setOnClickListener {
            val appInstance = application as MyApp
            var projDetObj = ProjectDetails(binding.projET.text.toString(), binding.yearET.text.toString(), appInstance.getPhoneNo()!!)
            GlobalScope.launch {

                db.AppDAO().addProjectDetails(projDetObj)
                withContext(Dispatchers.Main){
                    setupRecyclerView()
                    val animation = AnimationUtils.loadAnimation(this@ProjectsActivity, R.anim.glow_animation)
                    binding.projRCV.startAnimation(animation)
                }
            }

            Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()
        }

        binding.closeBtn.setOnClickListener {
            finish()
        }

        binding.newProjBtn.setOnClickListener{
            binding.projET.setText("")
            binding.yearET.setText("")
        }
        



    }

    private fun setupRecyclerView(){
        val appInstance = application as MyApp
        if (appInstance.getPhoneNo() == null){
            return
        }

        GlobalScope.launch {
            val cvList = db.AppDAO().getProjectDetails(appInstance.getPhoneNo()!!)
            projectArrayList.clear()
            projectArrayList.addAll(cvList)

            withContext(Dispatchers.Main){
                projAdapter = ProjectsRCVadapter(this@ProjectsActivity, projectArrayList, object :
                    ProjectsRCVadapter.Delete{

                    override fun deleteFromList(position: Int, projectDetails: ProjectDetails) {

                        GlobalScope.launch {
                            db.AppDAO().deleteProject(projectDetails.FP, projectDetails.Name)
                        }

                        projectArrayList.removeAt(position)
                        projAdapter.notifyItemChanged(position)
                        projAdapter.notifyDataSetChanged()

                        val animation = AnimationUtils.loadAnimation(this@ProjectsActivity, R.anim.glow_animation)
                        binding.projRCV.startAnimation(animation)



                    }

                }, object :
                    ProjectsRCVadapter.Populate{
                    override fun populateFields(position: Int, projectDetails: ProjectDetails) {
                        binding.projET.setText(projectDetails.Name)
                        binding.yearET.setText(projectDetails.Year)
                    }

                })
                binding.projRCV.adapter = projAdapter
                binding.projRCV.layoutManager = LinearLayoutManager(this@ProjectsActivity)
            }
        }
    }


}