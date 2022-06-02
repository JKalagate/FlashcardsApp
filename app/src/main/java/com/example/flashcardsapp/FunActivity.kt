package com.example.flashcardsapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import kotlinx.coroutines.Delay

open class FunActivity {

    //-------Toast with delay---------//
     fun delayToast(msg:Int, context: Context) {
        val mToast = Toast.makeText(
            context, msg, Toast.LENGTH_SHORT)
        mToast.show()
        Handler(Looper.myLooper()!!).postDelayed({
            mToast.cancel()
        }, 800)
    }

    // ----------------Get all data from word column in our table-------------------------------

     fun getColumn(db: SQLiteDatabase, columnName: String, lessonName: String, index: Int)
            : ArrayList<String> {

        var wordsListColumn = ArrayList<String>()

        val selectQuery = "SELECT * FROM LESSON_TABLE WHERE $columnName =?"
        val cursor: Cursor = db.rawQuery(selectQuery, arrayOf("$lessonName"))

        while (cursor.moveToNext()) {
            var element = (cursor.getString(index))
            wordsListColumn.add(element)

        }
        return wordsListColumn
    }

    //--------------------------Get two columns: id and string-----------------------

     fun getColumnsStringId(db: SQLiteDatabase, columnName: String,
                                   lessonName: String, notNullColumn: String, index: Int)
            : ArrayList<GameModelClass> {


        var StringIdList = ArrayList<GameModelClass>()

        val selectQuery = "SELECT * FROM LESSON_TABLE WHERE $columnName =? AND $notNullColumn IS NOT NULL"
        val cursor: Cursor = db.rawQuery(selectQuery, arrayOf("$lessonName"))



        while (cursor.moveToNext()) {
            var element = GameModelClass(cursor.getInt(0), cursor.getString(index))
            StringIdList.add(element)
        }

        return StringIdList
    }

    //---------------------------Get one element from table's row--------------------------------//
    fun getOneElementFromColumn (db: SQLiteDatabase, columnName: String,
                                 lessonName: String, index: Int): String{

        val selectQuery = "SELECT * FROM LESSON_TABLE WHERE $columnName =?"
        val cursor: Cursor = db.rawQuery(selectQuery, arrayOf("$lessonName"))

        var element = " "

        while (cursor.moveToNext()) {
            element = cursor.getString(index)
        }
        return element

    }

    //----------------------------------Get one column but without null------------------------//

    fun getColumnNotNull(db: SQLiteDatabase, columnName: String, lessonName: String, notNullColumn: String,index: Int)
            : ArrayList<String> {

        var getColumnNotNull = ArrayList<String>()

        val selectQuery = "SELECT * FROM LESSON_TABLE WHERE $columnName =? AND $notNullColumn IS NOT NULL"
        val cursor: Cursor = db.rawQuery(selectQuery, arrayOf("$lessonName"))

        while (cursor.moveToNext()) {
            var element = (cursor.getString(index))
            getColumnNotNull.add(element)

        }
        return getColumnNotNull
    }

    //---------------------Get one column name but without null------------------------//

    fun getColumnNames(db: SQLiteDatabase, columnName: String, index: Int)
            : ArrayList<String> {

        var getColumnNotNull = ArrayList<String>()

        val selectQuery = "SELECT * FROM LESSON_TABLE WHERE $columnName IS NOT NULL"
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        while (cursor.moveToNext()) {
            var element = (cursor.getString(index))
            getColumnNotNull.add(element)

        }
        return getColumnNotNull
    }

    //-------------------Get one column where column is null----------------//
    fun getColumnNull(db: SQLiteDatabase, columnName: String, lessonName: String, nullColumn: String)
            : ArrayList<String> {

        var getColumnNotNull = ArrayList<String>()

        val selectQuery = "SELECT * FROM LESSON_TABLE WHERE $columnName = ? AND $nullColumn IS NULL"
        val cursor: Cursor = db.rawQuery(selectQuery, arrayOf("$lessonName"))

        while (cursor.moveToNext()) {
            var element = (cursor.getString(5))
            getColumnNotNull.add(element)

        }
        return getColumnNotNull
    }


    //--------------Edit SqlDatabase------------//
    fun sqlDb (context: Context) :SQLiteDatabase {
        val dbHelper = DatabaseHelperClass(context)
        val db = dbHelper.writableDatabase
        return db
    }


}