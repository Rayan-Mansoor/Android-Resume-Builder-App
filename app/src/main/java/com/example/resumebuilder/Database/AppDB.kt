package com.example.resumebuilder.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.resumebuilder.Models.EducationDetails
import com.example.resumebuilder.Models.ExperienceDetails
import com.example.resumebuilder.Models.PersonalDetails
import com.example.resumebuilder.Models.ProjectDetails
import com.example.resumebuilder.Models.SkillDetails

@Database(entities = [PersonalDetails::class, EducationDetails::class, SkillDetails::class, ExperienceDetails::class, ProjectDetails::class], version = 1)
abstract class AppDB : RoomDatabase() {
    abstract fun AppDAO() : AppDAO
}