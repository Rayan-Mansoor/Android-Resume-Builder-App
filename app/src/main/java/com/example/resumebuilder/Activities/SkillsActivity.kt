package com.example.resumebuilder.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.RadioButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.resumebuilder.Adapter.RecentRCVadapter
import com.example.resumebuilder.Adapter.SkillRCVadapter
import com.example.resumebuilder.Database.AppDB
import com.example.resumebuilder.Models.EducationDetails
import com.example.resumebuilder.Models.PersonalDetails
import com.example.resumebuilder.Models.SkillDetails
import com.example.resumebuilder.R
import com.example.resumebuilder.Utils.MyApp
import com.example.resumebuilder.databinding.ActivityEducationBinding
import com.example.resumebuilder.databinding.ActivitySkillsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SkillsActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySkillsBinding
    private lateinit var db : AppDB
    private  var skillArrayList : ArrayList<SkillDetails> = ArrayList()
    private lateinit var sklAdapter : SkillRCVadapter

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySkillsBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = Room.databaseBuilder(applicationContext, AppDB::class.java,"resume-database").build()
        db.openHelper.writableDatabase


        setupRecyclerView()

        binding.saveSklBtn.setOnClickListener {

            if (binding.sklGroup.checkedRadioButtonId != -1){
                val selectedRB = findViewById<RadioButton>(binding.sklGroup.checkedRadioButtonId)
                val selBtnText = selectedRB.text.toString()

                val appInstance = application as MyApp
                var sklDetObj = SkillDetails(binding.skillET.text.toString(), selBtnText, appInstance.getPhoneNo()!!)
                GlobalScope.launch {

                    db.AppDAO().addSkillDetails(sklDetObj)
                    withContext(Dispatchers.Main){
                        setupRecyclerView()
                        val animation = AnimationUtils.loadAnimation(this@SkillsActivity, R.anim.glow_animation)
                        binding.sklRCV.startAnimation(animation)
                    }


                }
            }

            Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()


        }

        binding.closeBtn.setOnClickListener {
            finish()
        }

        binding.newSklBtn.setOnClickListener{
            binding.skillET.setText("")
            binding.sklGroup.clearCheck()
        }
    }

    private fun setupRecyclerView(){
        val appInstance = application as MyApp
        if (appInstance.getPhoneNo() == null){
            return
        }

        GlobalScope.launch {
            val cvList = db.AppDAO().getSkillDetails(appInstance.getPhoneNo()!!)
            skillArrayList.clear()
            skillArrayList.addAll(cvList)

            withContext(Dispatchers.Main){
                sklAdapter = SkillRCVadapter(this@SkillsActivity, skillArrayList, object :
                    SkillRCVadapter.Delete{

                    override fun deleteFromList(position: Int, skillDetails: SkillDetails) {

                        GlobalScope.launch {
                            db.AppDAO().deleteSkill(skillDetails.FP, skillDetails.name)
                        }

                        skillArrayList.removeAt(position)
                        sklAdapter.notifyItemChanged(position)
                        sklAdapter.notifyDataSetChanged()
                        val animation = AnimationUtils.loadAnimation(this@SkillsActivity, R.anim.glow_animation)
                        binding.sklRCV.startAnimation(animation)

                    }

                }, object :
                SkillRCVadapter.Populate{
                    override fun populateFields(position: Int, skillDetails: SkillDetails) {
                        binding.skillET.setText(skillDetails.name)

                        when (skillDetails.level) {
                            "5" -> { binding.lvl5.isChecked = true }
                            "4" -> { binding.lvl4.isChecked = true }
                            "3" -> { binding.lvl3.isChecked = true }
                            "2" -> { binding.lvl2.isChecked = true }
                            "1" -> { binding.lvl1.isChecked = true }
                        }
                    }

                })
                binding.sklRCV.adapter = sklAdapter
                binding.sklRCV.layoutManager = LinearLayoutManager(this@SkillsActivity)
            }
        }
    }
}