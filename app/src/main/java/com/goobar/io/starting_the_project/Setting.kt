package com.goobar.io.starting_the_project

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.firebase.storage.FirebaseStorage
import com.goobar.io.starting_the_project.databinding.SettingBinding
import java.io.File

class Setting : AppCompatActivity() {

    private val prefsFilename = "com.goobar.io.starting_the_project.prefs"
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: SettingBinding
    private val storageReference = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(prefsFilename, Context.MODE_PRIVATE)
        binding.userName.setText(sharedPreferences.getString("username", ""))

        binding.submitFeedbackButton.setOnClickListener {
            saveUsername(binding.userName.text.toString())
            val intent = Intent(this, Homepage::class.java)
            intent.putExtra("username", binding.userName.text.toString())
            startActivity(intent)
        }

        binding.backupButton.setOnClickListener {
            backupDatabase()
        }

        binding.restorButton.setOnClickListener {
            restoreDatabase()
        }
    }

    private fun saveUsername(username: String) {
        sharedPreferences.edit {
            putString("username", username)
            apply()
        }
    }

    private fun backupDatabase() {
        // Corrected: Use getDatabasePath() without the full path
        val dbFile = this.getDatabasePath("diary2.db")
        val localDBUri = Uri.fromFile(dbFile)
        val dbBackupRef = storageReference.child("DiaryApp/diary2.db")

        dbBackupRef.putFile(localDBUri).addOnSuccessListener {
            Toast.makeText(this, "Database backup successful", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { exception ->
            // Updated: Log the exception message
            Toast.makeText(this, "Database backup failed: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun restoreDatabase() {
        // Corrected: Use getDatabasePath() without the full path
        val dbFile = this.getDatabasePath("diary2.db")
        val localFile = File.createTempFile("temp_db", ".db")
        val dbBackupRef = storageReference.child("DiaryApp/diary2.db")

        dbBackupRef.getFile(localFile).addOnSuccessListener {
            if (dbFile.exists()) dbFile.delete()
            localFile.copyTo(dbFile)
            Toast.makeText(this, "Database restore successful", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { exception ->
            // Updated: Log the exception message
            Toast.makeText(this, "Database restore failed: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

}
