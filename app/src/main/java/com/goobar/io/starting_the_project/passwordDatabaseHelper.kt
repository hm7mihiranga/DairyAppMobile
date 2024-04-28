package com.goobar.io.starting_the_project

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class passwordDatabaseHelper(context:Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "diary.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "password_store"
        private const val COLUMN_ID = "id"
        private const val COLUMN_PSW = "psw"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val creatTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_PSW TEXT)"
        db?.execSQL(creatTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertpsw(psww:For_password){
        // Refresh the database by clearing other rows
        clearOtherRows(psww.toString())
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PSW, psww.psw)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()

    }

    // function for retrive the data from database
    fun getPassword(): String? {
        val db = readableDatabase
        val cursor = db.query(TABLE_NAME, arrayOf(COLUMN_PSW), null, null, null, null, null)
        var hashedPassword: String? = null
        if (cursor.moveToFirst()) {
            hashedPassword = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PSW))
        }
        cursor.close()
        db.close()
        return hashedPassword
    }


    // Clear Other rows and store only one password
    fun clearOtherRows(currentPsw: String) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_PSW != ?", arrayOf(currentPsw))
        db.close()
    }


}