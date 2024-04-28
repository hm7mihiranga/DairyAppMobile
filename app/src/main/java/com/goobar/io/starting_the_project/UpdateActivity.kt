package com.goobar.io.starting_the_project

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.goobar.io.starting_the_project.databinding.ActivityUpdateWorkBinding
import java.io.ByteArrayOutputStream
import android.app.DatePickerDialog
import android.content.Intent
import android.provider.MediaStore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class UpdateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateWorkBinding
    private lateinit var db: DailyWorksHelper
    private var cal = Calendar.getInstance()
    private val CAMERA_REQUEST_CODE = 100
    private var workID: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateWorkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DailyWorksHelper(this)

        //Capture Images
        binding.updateimageButton.setOnClickListener {
            val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            val chooserIntent = Intent.createChooser(pickImageIntent, "Select or take a new Picture")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))

            startActivityForResult(chooserIntent, CAMERA_REQUEST_CODE)
        }


        // Calandar View
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        binding.updatebutton.setOnClickListener {
            DatePickerDialog(this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


        // Corrected intent extra key
        workID = intent.getIntExtra("work_id", -1)
        if (workID == -1) {
            // Show a message and finish the activity if work ID is invalid
            Toast.makeText(this, "Invalid work ID. Closing activity.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val work = db.getWorkByID(workID)
        binding.updatetextView8.setText(work.date)
        binding.updatetext002.setText(work.title)
        binding.updatetext001.setText(work.content)

        // Check if work.image is not null before decoding
        work.image?.let { imageByteArray ->
            val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
            binding.updatecapturedImageView.setImageBitmap(bitmap)
        }

        binding.updatesaveButton.setOnClickListener {
            val newDate = binding.updatetextView8.text.toString()
            val newTitle = binding.updatetext002.text.toString()
            val newContent = binding.updatetext001.text.toString()

            // Get the Bitmap from the ImageView
            val drawable = binding.updatecapturedImageView.drawable as BitmapDrawable
            val newImage = drawable.bitmap

            // Convert the Bitmap into a ByteArray for storage
            val stream = ByteArrayOutputStream()
            newImage.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val imageInByte = stream.toByteArray()

            // Create an updated DailyWorks object
            val updatedWork = DailyWorks(workID, newDate, newTitle, newContent, imageInByte)

            // Update the entry in the database
            db.updateWork(updatedWork)

            // Provide user feedback
            Toast.makeText(this, "Work entry updated successfully!", Toast.LENGTH_SHORT).show()

            // Finish the activity
            finish()
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
                binding.updatecapturedImageView.setImageBitmap(it)
            }
        }
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.updatetextView8.text = sdf.format(cal.time)
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        ByteArrayOutputStream().use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            return outputStream.toByteArray()
        }
    }
}
