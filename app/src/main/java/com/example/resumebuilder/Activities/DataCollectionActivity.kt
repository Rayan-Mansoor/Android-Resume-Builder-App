package com.example.resumebuilder.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.resumebuilder.Models.PersonalDetails
import com.example.resumebuilder.Utils.MyApp
import com.example.resumebuilder.databinding.ActivityDataCollectionBinding

class DataCollectionActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDataCollectionBinding


    companion object {
        fun newIntent(context: Context, personalDetails: PersonalDetails): Intent {
            return Intent(context, DataCollectionActivity::class.java).putExtra("phoneIntent", personalDetails.PhoneNo)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDataCollectionBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val phoneNumber = intent.getStringExtra("phoneIntent")
        val appInstance = application as MyApp
        if (phoneNumber != null) {
            appInstance.setphoneNo(phoneNumber)
        }

        binding.personalDetailBtn.setOnClickListener {
            val intent = Intent(this, PersonalDetailsActivity::class.java)
            startActivity(intent)
        }

        binding.EducationBtn.setOnClickListener {
            val intent = Intent(this, EducationActivity::class.java)
            startActivity(intent)
        }

        binding.skillsBtn.setOnClickListener {
            val intent = Intent(this, SkillsActivity::class.java)
            startActivity(intent)
        }

        binding.ExperienceBtn.setOnClickListener {
            val intent = Intent(this, ExperienceActivity::class.java)
            startActivity(intent)
        }

        binding.ProjectsBtn.setOnClickListener {
            val intent = Intent(this, ProjectsActivity::class.java)
            startActivity(intent)
        }

        binding.ViewResumeBtn.setOnClickListener {
            val intent = Intent(this, DisplayResumeActivity::class.java)
            startActivity(intent)
        }
    }
}