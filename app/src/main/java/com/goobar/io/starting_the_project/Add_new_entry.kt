package com.goobar.io.starting_the_project

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.goobar.io.starting_the_project.databinding.ActivityAddNewEntryBinding
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Add_new_entry : AppCompatActivity() {
    private lateinit var binding: ActivityAddNewEntryBinding
    private var cal = Calendar.getInstance()
    private val CAMERA_REQUEST_CODE = 100
    private lateinit var db: DailyWorksHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textView8.text = "Please pick a date"

        // connect with the database functionality
        db = DailyWorksHelper(this)

        binding.imageButton.setOnClickListener {
            val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            val chooserIntent = Intent.createChooser(pickImageIntent, "Select or take a new Picture")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))

            startActivityForResult(chooserIntent, CAMERA_REQUEST_CODE)
        }

        // This is the function of the save Button(AddnewField)
        binding.saveButton.setOnClickListener {
            val date = binding.textView8.text.toString()
            val title = binding.text002.text.toString()
            val content = binding.text001.text.toString()

            // Initialize a variable for the image byte array
            var imageByteArray: ByteArray? = null

            // Check if the ImageView has a drawable
            if (binding.capturedImageView.drawable != null) {
                // If an image is uploaded, convert it to a byte array
                val imageBitmap = (binding.capturedImageView.drawable as BitmapDrawable).bitmap
                imageByteArray = bitmapToByteArray(imageBitmap)
            }

            // Create a DailyWorks object with the image byte array, handling the case where it might be null
            val dailyWorks = DailyWorks(1, date, title, content, imageByteArray)

            // Insert the DailyWorks object into the database
            db.insertWorks(dailyWorks)

            // Finish the activity and show a toast message
            finish()
            Toast.makeText(this, "Work Saved", Toast.LENGTH_SHORT).show()
        }


        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        binding.button.setOnClickListener {
            DatePickerDialog(this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val isCamera = data == null || data.data == null
            val bitmap = if (isCamera) {
                // From Camera
                data?.extras?.get("data") as? Bitmap
            } else {
                // From Gallery
                val imageUri = data?.data
                val imageStream = contentResolver.openInputStream(imageUri!!)
                BitmapFactory.decodeStream(imageStream)
            }
            bitmap?.let {
                binding.capturedImageView.setImageBitmap(it)
            }
        }
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy HH:mm:ss"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.textView8.text = sdf.format(cal.time)
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        ByteArrayOutputStream().use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            return outputStream.toByteArray()
        }
    }
}
