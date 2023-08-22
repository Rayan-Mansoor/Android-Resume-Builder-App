package com.example.resumebuilder.Models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(entity = PersonalDetails::class, parentColumns = ["PhoneNo"], childColumns = ["FP"])],primaryKeys = ["FP","name"])
data class SkillDetails(var name : String, var level : String, val FP : String)
