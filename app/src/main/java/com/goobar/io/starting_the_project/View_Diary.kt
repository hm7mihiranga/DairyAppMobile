package com.goobar.io.starting_the_project

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.goobar.io.starting_the_project.databinding.ActivityViewDiaryBinding

class View_Diary : AppCompatActivity() {

    private lateinit var binding: ActivityViewDiaryBinding // Fix: Add lateinit keyword
    private lateinit var db: DailyWorksHelper
    private lateinit var worksAdapter: WorksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DailyWorksHelper(this)
        worksAdapter = WorksAdapter(db.getSomeWorks(), this)

        binding.worksRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.worksRecyclerView.adapter = worksAdapter

        binding.addButton.setOnClickListener {
            val intent = Intent(this, Add_new_entry::class.java)
            startActivity(intent)
            // Add any necessary logic related to the button click here
        }

        // In your View_Diary activity
        binding.loadMoreTextView.setOnClickListener {
            // Get all works from the database
            val allWorks = db.getAllWorks()

            // Add the new works to the existing adapter
            worksAdapter.addWorks(allWorks)

            // Calculate the position to scroll to
            val positionToScroll = worksAdapter.itemCount - allWorks.size

            // Scroll to the position where the new items start
            binding.worksRecyclerView.scrollToPosition(if (positionToScroll > 0) positionToScroll else 0)
        }

    }

    override fun onResume() {
        super.onResume()
        worksAdapter.refreshData(db.getSomeWorks())
    }
}
