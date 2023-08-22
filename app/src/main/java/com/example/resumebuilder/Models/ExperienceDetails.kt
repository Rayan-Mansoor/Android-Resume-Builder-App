package com.example.resumebuilder.Models

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(foreignKeys = [ForeignKey(entity = PersonalDetails::class, parentColumns = ["PhoneNo"], childColumns = ["FP"])],primaryKeys = ["FP","company"])
data class ExperienceDetails(var company : String, var role : String, val startDateMonth : Int, val endDateMonth : Int, val startDateYear : Int, val endDateYear : Int,val FP : String)
