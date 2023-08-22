package com.example.resumebuilder.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.resumebuilder.Models.EducationDetails
import com.example.resumebuilder.Models.ExperienceDetails
import com.example.resumebuilder.Models.PersonalDetails
import com.example.resumebuilder.Models.ProjectDetails
import com.example.resumebuilder.Models.SkillDetails

@Dao
interface AppDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPeronalDetails(personalDetails: PersonalDetails)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addeducationDetails(educationDetails: EducationDetails)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSkillDetails(skillDetails: SkillDetails)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addExperienceDetails(experinceDetails: ExperienceDetails)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addProjectDetails(projectDetails: ProjectDetails)


    suspend fun deleteCV(phoneNo : String){
        deleteEducationDetails(phoneNo)
        deleteExperienceDetails(phoneNo)
        deleteSkillDetails(phoneNo)
        deleteProjectDetails(phoneNo)
        deletePeronalDetails(phoneNo)
    }

    @Query("delete from personaldetails where PhoneNo = :phoneNo ")
    suspend fun deletePeronalDetails(phoneNo : String)

    @Query("delete from educationdetails where FP = :phoneNo ")
    suspend fun deleteEducationDetails(phoneNo : String)

    @Query("delete from skilldetails where FP = :phoneNo ")
    suspend fun deleteSkillDetails(phoneNo : String)

    @Query("delete from skilldetails where FP = :phoneNo and name = :name ")
    suspend fun deleteSkill(phoneNo : String, name : String)

    @Query("delete from projectdetails where FP = :phoneNo and name = :name ")
    suspend fun deleteProject(phoneNo : String, name : String)

    @Query("delete from experiencedetails where FP = :phoneNo and company = :company ")
    suspend fun deleteExperience(phoneNo : String, company : String)

    @Query("delete from educationdetails where FP = :phoneNo and uni = :uni and course = :course")
    suspend fun deleteEducation(phoneNo : String, uni : String, course : String)

    @Query("delete from experiencedetails where FP = :phoneNo ")
    suspend fun deleteExperienceDetails(phoneNo : String)

    @Query("delete from projectdetails where FP = :phoneNo ")
    suspend fun deleteProjectDetails(phoneNo : String)

    @Query("select * from personaldetails where PhoneNo = :phoneNo")
    suspend fun getPersonalDetails(phoneNo : String) : PersonalDetails

    @Query("select * from educationdetails where FP = :phoneNo")
    suspend fun getEducationDetails(phoneNo : String) : List<EducationDetails>

    @Query("select * from personaldetails")
    suspend fun getAllPersonalDetails() : List<PersonalDetails>

    @Query("select * from skilldetails where FP = :phoneNo")
    suspend fun getSkillDetails(phoneNo : String) : List<SkillDetails>

    @Query("select * from experiencedetails where FP = :phoneNo")
    suspend fun getExperienceDetails(phoneNo : String) : List<ExperienceDetails>

    @Query("select * from projectdetails where FP = :phoneNo")
    suspend fun getProjectDetails(phoneNo : String) : List<ProjectDetails>

}