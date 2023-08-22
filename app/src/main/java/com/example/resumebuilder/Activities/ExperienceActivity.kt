package com.example.resumebuilder.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.RadioButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.resumebuilder.Adapter.EducationRCVadapter
import com.example.resumebuilder.Adapter.ExperienceRCVadapter
import com.example.resumebuilder.Adapter.ProjectsRCVadapter
import com.example.resumebuilder.Database.AppDB
import com.example.resumebuilder.Models.EducationDetails
import com.example.resumebuilder.Models.ExperienceDetails
import com.example.resumebuilder.Models.ProjectDetails
import com.example.resumebuilder.Models.SkillDetails
import com.example.resumebuilder.R
import com.example.resumebuilder.Utils.MyApp
import com.example.resumebuilder.databinding.ActivityExperienceBinding
import com.example.resumebuilder.databinding.ActivitySkillsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExperienceActivity : AppCompatActivity() {
    private lateinit var binding : ActivityExperienceBinding
    private lateinit var db : AppDB
    private  var experienceArrayList : ArrayList<ExperienceDetails> = ArrayList()
    private lateinit var expAdapter : ExperienceRCVadapter

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityExperienceBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = Room.databaseBuilder(applicationContext, AppDB::class.java,"resume-database").build()
        db.openHelper.writableDatabase

        setupRecyclerView()

        val initialYear = 2023
        val initialMonth = 0 // Month is zero-based (0 for January)
        val initialDay = 1
        binding.startDatePicker.init(initialYear, initialMonth, initialDay, null)
        binding.endDatePicker.init(initialYear, initialMonth, initialDay, null)

        binding.saveExpBtn.setOnClickListener {

                val appInstance = application as MyApp
                var expDetObj = ExperienceDetails(binding.compET.text.toString(), binding.roleET.text.toString(),binding.startDatePicker.month+1, binding.endDatePicker.month+1 ,binding.startDatePicker.year, binding.endDatePicker.year ,appInstance.getPhoneNo()!!)
                GlobalScope.launch {

                    db.AppDAO().addExperienceDetails(expDetObj)
                    withContext(Dispatchers.Main){
                        setupRecyclerView()
                        val animation = AnimationUtils.loadAnimation(this@ExperienceActivity, R.anim.glow_animation)
                        binding.expRCV.startAnimation(animation)
                    }

                }

            Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()



        }

        binding.closeBtn.setOnClickListener {
            finish()
        }

        binding.newExpBtn.setOnClickListener{
            binding.compET.setText("")
            binding.roleET.setText("")
            binding.startDatePicker.init(initialYear, initialMonth, initialDay, null)
            binding.endDatePicker.init(initialYear, initialMonth, initialDay, null)
        }
    }

    private fun setupRecyclerView(){
        val appInstance = application as MyApp
        if (appInstance.getPhoneNo() == null){
            return
        }

        GlobalScope.launch {
            val cvList = db.AppDAO().getExperienceDetails(appInstance.getPhoneNo()!!)
            experienceArrayList.clear()
            experienceArrayList.addAll(cvList)

            withContext(Dispatchers.Main){
                expAdapter = ExperienceRCVadapter(this@ExperienceActivity, experienceArrayList, object :
                    ExperienceRCVadapter.Delete{

                    override fun deleteFromList(position: Int, experienceDetails: ExperienceDetails) {

                        GlobalScope.launch {
                            db.AppDAO().deleteExperience(experienceDetails.FP, experienceDetails.company)
                        }

                        experienceArrayList.removeAt(position)
                        expAdapter.notifyItemChanged(position)
                        expAdapter.notifyDataSetChanged()

                        val animation = AnimationUtils.loadAnimation(this@ExperienceActivity, R.anim.glow_animation)
                        binding.expRCV.startAnimation(animation)

                    }

                }, object :
                    ExperienceRCVadapter.Populate{
                    override fun populateFields(position: Int, experienceDetails: ExperienceDetails) {
                        binding.compET.setText(experienceDetails.company)
                        binding.roleET.setText(experienceDetails.role)
                        binding.startDatePicker.init(experienceDetails.startDateYear, experienceDetails.startDateMonth, 1, null)
                        binding.endDatePicker.init(experienceDetails.endDateYear, experienceDetails.endDateMonth, 1, null)
                    }

                })
                binding.expRCV.adapter = expAdapter
                binding.expRCV.layoutManager = LinearLayoutManager(this@ExperienceActivity)
            }
        }
    }
}