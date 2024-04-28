package com.goobar.io.starting_the_project

import android.os.Bundle
import android.content.Intent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.goobar.io.starting_the_project.databinding.ActivityPasswordVerificationBinding
import org.mindrot.jbcrypt.BCrypt
import java.util.Calendar

class PasswordVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPasswordVerificationBinding
    private lateinit var db: passwordDatabaseHelper

    private val imagesArray = arrayOf(
        R.drawable.sunday,
        R.drawable.monday,
        R.drawable.tuesday,
        R.drawable.wednesday,
        R.drawable.thursday,
        R.drawable.friday,
        R.drawable.saturday
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the current day of the week
        val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val todaysImageResId = imagesArray[dayOfWeek - 1] // -1 because Calendar.DAY_OF_WEEK starts from 1
        // Find the ImageView and set the image
        val imageView = findViewById<ImageView>(R.id.topImage)
        imageView.setImageResource(todaysImageResId)

        // Set the OnClickListener to close the image
        imageView.setOnClickListener {
            imageView.visibility = View.GONE
        }
        // Load the animation
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        imageView.startAnimation(fadeInAnimation)


        // Set the background to the intro image
        binding.backgroundLayout.background = ContextCompat.getDrawable(this, R.drawable.intro)

        db = passwordDatabaseHelper(this)
        val hashedPsw = db.getPassword()

        // Add New Password Function
        binding.AddnewButton.setOnClickListener {
            val enteredPsw = binding.passwordEditText.text.toString()
            if (hashedPsw != null){
                val isCorrect = BCrypt.checkpw(enteredPsw, hashedPsw)
                if(isCorrect){
                    val intent = Intent(this, AddPSW::class.java)
                    startActivity(intent)
                    finish()
                }
                else{
                    Toast.makeText(this,"Please Enter the Existing Password", Toast.LENGTH_SHORT).show()
                }
            }

        }


        // Confirm Password Function
        binding.confirmButton.setOnClickListener {
            val enteredPsw = binding.passwordEditText.text.toString()

            if (hashedPsw != null) {
                val isCorrect = BCrypt.checkpw(enteredPsw, hashedPsw)
                if (isCorrect) {
                    Toast.makeText(this, "Password is correct", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Homepage::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Password is incorrect", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No stored password found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
