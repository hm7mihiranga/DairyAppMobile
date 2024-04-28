package com.goobar.io.starting_the_project

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DailyWorksHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME,null,
    DATABASE_VERSION){

    companion object{
            private const val DATABASE_NAME = "diary2.db"
            private const val DATABASE_VERSION = 1
            private const val TABLE_NAME = "dailyworks"
            private const val COLUMN_ID = "id"
            private const val COLUMN_DATE = "date"
            private const val COLUMN_TITLE = "title"
            private const val COLUMN_CONTENT = "content"
            private const val  COLUMN_IMAGE = "image"
        }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_DATE DATE, $COLUMN_TITLE TEXT, $COLUMN_CONTENT TEXT, $COLUMN_IMAGE BLOB)"
        db?.execSQL(createTableQuery)
    }

    // can be save the duplicate table, if yes, delete all tables and remain one table
    override fun onUpgrade(db: SQLiteDatabase?, OldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertWorks(dailyWorks: DailyWorks){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DATE, dailyWorks.date)
            put(COLUMN_TITLE, dailyWorks.title)
            put(COLUMN_CONTENT, dailyWorks.content)
            put(COLUMN_IMAGE, dailyWorks.image)
        }
        val result = db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllWorks(): List<DailyWorks> {
        val workList = mutableListOf<DailyWorks>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_DATE DESC"
        val cursor = db.rawQuery(query,null)

        while(cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
            val image = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE))

            val work = DailyWorks(id,date,title,content,image)
            workList.add(work)

        }
        cursor.close()
        db.close()
        return workList
    }

    // This is for print only 3 lines
    fun getSomeWorks(): List<DailyWorks> {
        val workList = mutableListOf<DailyWorks>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_DATE DESC LIMIT 3"
        val cursor = db.rawQuery(query,null)

        while(cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
            val image = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE))

            val work = DailyWorks(id,date,title,content,image)
            workList.add(work)

        }
        cursor.close()
        db.close()
        return workList
    }

    // This is for the Update the database and UI parts
    fun updateWork(work: DailyWorks){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DATE, work.date)
            put(COLUMN_TITLE, work.title)
            put(COLUMN_CONTENT, work.content)
            put(COLUMN_IMAGE, work.image)
        }
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(work.id.toString())
        db.update(TABLE_NAME, values, whereClause,whereArgs)
        db.close()
    }

    fun getWorkByID(workID: Int): DailyWorks{
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $workID"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
        val image = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE))

        cursor.close()
        db.close()
        return DailyWorks(id,date,title,content,image)
    }

    fun deleteWork(workID: Int){
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(workID.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }
}
