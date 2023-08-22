package com.example.resumebuilder.Models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(entity = PersonalDetails::class, parentColumns = ["PhoneNo"], childColumns = ["FP"])],primaryKeys = ["FP","Name"])
data class ProjectDetails(var Name : String, var Year : String, var FP : String)
