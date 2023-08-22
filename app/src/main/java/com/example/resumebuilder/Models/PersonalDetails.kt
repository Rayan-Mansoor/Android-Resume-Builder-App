package com.example.resumebuilder.Models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PersonalDetails(var Name : String, var Email :String, @PrimaryKey var PhoneNo : String, var yourImg : String)
