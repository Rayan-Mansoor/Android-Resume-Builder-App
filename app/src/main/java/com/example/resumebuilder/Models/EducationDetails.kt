package com.example.resumebuilder.Models

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(foreignKeys = [ForeignKey(entity = PersonalDetails::class, parentColumns = ["PhoneNo"], childColumns = ["FP"])],primaryKeys = ["FP","course"])
data class EducationDetails(var course : String, var uni :String, var grade : String, var year : String, val FP : String)
