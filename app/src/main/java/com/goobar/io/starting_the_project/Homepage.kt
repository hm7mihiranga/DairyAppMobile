package com.goobar.io.starting_the_project

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView

class Homepage : AppCompatActivity() {
    private var isBottomNavVisible = true

    private lateinit var sharedPreferences: SharedPreferences
    private val prefsFilename = "com.goobar.io.starting_the_project.prefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)


        // for the Animation part
        val imageView = findViewById<ImageView>(R.id.imageView4)
        // Load the animation
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        imageView.startAnimation(fadeInAnimation)


        //Update the Username
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(prefsFilename, MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")

        // Set the username to the appropriate view
        val textView5 = findViewById<TextView>(R.id.textView5)
        textView5.text = username

        val bottomNav: BottomNavigationView = findViewById(R.id.bottomnav)
        bottomNav.visibility = View.GONE

        val homeLayout: View = findViewById(R.id.relativelayout)
        homeLayout.setOnClickListener {
            toggleBottomNavVisibility(bottomNav)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomnav)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.setting3 -> {
                    // Handle setting3 item click
                    val intent = Intent(this, Setting::class.java)
                    startActivity(intent)
                    true
                }
                R.id.add_new -> {
                    // Handle setting3 item click
                    val intent = Intent(this, Add_new_entry::class.java)
                    startActivity(intent)
                    true
                }
                R.id.View_diary -> {
                    val intent = Intent(this, View_Diary::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }


    }

    private fun toggleBottomNavVisibility(bottomNav: BottomNavigationView) {
        if (isBottomNavVisible) {
            bottomNav.visibility = View.GONE
        } else {
            bottomNav.visibility = View.VISIBLE
            bottomNav.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up))
        }
        isBottomNavVisible = !isBottomNavVisible
    }
}