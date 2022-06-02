package com.example.flashcardsapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import java.net.IDN
import java.util.ArrayList


class DatabaseHelperClass(context: Context)
    : SQLiteOpenHelper(context, myTable.DATABASE_NAME, null, 1) {


    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(myTable.myCommand.createTable)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(myTable.myCommand.deleteTable)
        onCreate(db);

    }

}
//----------------Names of columns-----------------//
object myTable : BaseColumns {

    const val DATABASE_NAME: String ="fcDB"
    const val TABLE_NAME: String ="LESSON_TABLE"

    const val LESSON_NAME: String ="lesson_name"
    const val WORD: String = "word"
    const val TRANS_WORD: String = "trans_word"
    const val IMAGE_RESOURCE : String = "Image_resource"
    const val SORT_LESSON_NAME: String = "sort_lesson_name"
    const val WORK_ON_WORD: String= "work_on_word"
    const val WORK_ON_TRANS_WORD: String = "work_on_trans_word"

    //------------------Initalize Columns-------------------//
    object myCommand {

        const val createTable: String = "CREATE TABLE $TABLE_NAME (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "$LESSON_NAME TEXT ," +
                "$WORD TEXT ," +
                "$TRANS_WORD TEXT , " +
                "$IMAGE_RESOURCE BlOB , " +
                "$SORT_LESSON_NAME TEXT , " +
                "$WORK_ON_WORD TEXT  , " +
                "$WORK_ON_TRANS_WORD TEXT )"

        const val deleteTable = "DROP TABLE ID EXISTS $TABLE_NAME"


    }

}