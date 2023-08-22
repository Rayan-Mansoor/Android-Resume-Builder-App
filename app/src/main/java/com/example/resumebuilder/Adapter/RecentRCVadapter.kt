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
import com.example.resumebuilder.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecentRCVadapter(private var context : Context, private var recentList: List<PersonalDetails>,val delClick: Delete) : RecyclerView.Adapter<RecentRCVadapter.RCVholder>() {

    class RCVholder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val image =  itemView.findViewById<ImageView>(R.id.rctPic)
        val name = itemView.findViewById<TextView>(R.id.rctName)
        val phone = itemView.findViewById<TextView>(R.id.rctPhone)
        val del =  itemView.findViewById<ImageView>(R.id.rctDel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RCVholder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_recent_cv,parent,false)
        return RCVholder(itemView)
    }

    override fun onBindViewHolder(holder: RCVholder, position: Int) {
        val currentItem = recentList[position]
        holder.name.text = currentItem.Name
        holder.phone.text = currentItem.PhoneNo
        holder.image.setImageURI(Uri.parse(currentItem.yourImg))

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DataCollectionActivity::class.java)
            intent.putExtra("phoneIntent", currentItem.PhoneNo)
            context.startActivity(intent)
        }

        holder.del.setOnClickListener {
            delClick.deleteFromList(position,currentItem)
//            removeItem(position)
        }
    }

    override fun getItemCount(): Int {
        return recentList.size
    }

    fun removeItem(position: Int) {
        Log.d("RecentRCVadapter", position.toString())

        val db = Room.databaseBuilder(context, AppDB::class.java, "resume-database").build()
        db.openHelper.writableDatabase

        GlobalScope.launch {
            db.AppDAO().deleteCV(recentList[position].PhoneNo)

            val updatedList = recentList.toMutableList()
            updatedList.removeAt(position)

            withContext(Dispatchers.Main) {
                recentList = updatedList
                notifyItemRemoved(position)
            }
        }
    }



    interface Delete{
        fun deleteFromList(position:Int, personalDetails: PersonalDetails)
    }


}