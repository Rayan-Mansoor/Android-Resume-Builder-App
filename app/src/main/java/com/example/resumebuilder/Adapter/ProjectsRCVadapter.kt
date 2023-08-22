package com.example.resumebuilder.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.resumebuilder.Activities.DataCollectionActivity
import com.example.resumebuilder.Database.AppDB
import com.example.resumebuilder.Models.PersonalDetails
import com.example.resumebuilder.Models.ProjectDetails
import com.example.resumebuilder.Models.SkillDetails
import com.example.resumebuilder.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProjectsRCVadapter(private var context : Context, private var projectsList: List<ProjectDetails>,val delClick: Delete, val itemClick : Populate) : RecyclerView.Adapter<ProjectsRCVadapter.RCVholder>() {

    class RCVholder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.sklName)
        val del =  itemView.findViewById<ImageView>(R.id.sklDel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RCVholder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_skill,parent,false)
        return RCVholder(itemView)
    }

    override fun onBindViewHolder(holder: RCVholder, position: Int) {
        val currentItem = projectsList[position]
        holder.name.text = currentItem.Name


        holder.del.setOnClickListener {
            delClick.deleteFromList(position,currentItem)
        }

        holder.itemView.setOnClickListener {
            itemClick.populateFields(position,currentItem)
        }
    }

    override fun getItemCount(): Int {
        return projectsList.size
    }


    interface Delete{
        fun deleteFromList(position:Int, projectDetails: ProjectDetails)
    }

    interface Populate{
        fun populateFields(position:Int, projectDetails: ProjectDetails)
    }


}