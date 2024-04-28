package com.goobar.io.starting_the_project

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class WorksAdapter (private var works: List<DailyWorks>,context: Context):
    RecyclerView.Adapter<WorksAdapter.WorkViewHolder>() {

        private val db: DailyWorksHelper = DailyWorksHelper(context)

    class WorkViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val dateView : TextView = itemView.findViewById(R.id.dateTextView)
        val titleTextView : TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView : TextView = itemView.findViewById(R.id.contentTextView)
        val imageTextView : ImageView = itemView.findViewById(R.id.iamgeViewnew1)
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton200)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.daily_works_item, parent, false)
        return WorkViewHolder(view)
    }

    override fun getItemCount(): Int = works.size

    override fun onBindViewHolder(holder: WorkViewHolder, position: Int) {
        val work = works[position]
        holder.dateView.text = work.date
        holder.titleTextView.text = work.title
        holder.contentTextView.text = work.content
        holder.imageTextView.setImageBitmap(work.image?.let { BitmapFactory.decodeByteArray(work.image, 0, it.size) })

        holder.updateButton.setOnClickListener{
            Log.d("WorksAdapter", "Update button clicked") // this is checking for error via LogCat
            val intent = Intent(holder.itemView.context, UpdateActivity::class.java).apply {
                putExtra("work_id", work.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener{
            db.deleteWork(work.id)
            refreshData(db.getAllWorks())
            Toast.makeText(holder.itemView.context, "Note Deleted", Toast.LENGTH_SHORT).show()
        }

    }

    fun refreshData(newWorks:List<DailyWorks>){
        works = newWorks
        notifyDataSetChanged()
    }

    fun addWorks(newWorks: List<DailyWorks>) {
        works = newWorks
        notifyDataSetChanged()
    }
}