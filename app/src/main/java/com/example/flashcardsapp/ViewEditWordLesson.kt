package com.example.flashcardsapp

import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_edit_word_lesson.*
import kotlin.collections.ArrayList

class ViewEditWordLesson : AppCompatActivity() {

    private val funActivity = FunActivity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_edit_word_lesson)

        }

    override fun onStart() {
        super.onStart()
        //--------------Set Lesson Name in activity---------------//
        val lessonName = intent.getStringExtra("EXTRA_NAME2")
        tv_edit_lesson_name.setText(lessonName)

        //-------Edit sqlDatabse-------//
        val db = funActivity.sqlDb(this)

        //---Cursor set in all rows where are "lessonName" in sqlDatabse---//
        val cursor: Cursor = db.query(
            myTable.TABLE_NAME,
            null, myTable.LESSON_NAME + "=?",
            arrayOf("$lessonName"), null, null, null, null
        )

        //--From specific rows in sql get ids, words and translated Words
        //-- and put these all to arrayList --//
        val wordsList = ArrayList<LessonModelClass>()
        if (cursor.moveToFirst()) {
            do {
                val words = LessonModelClass()
                words.id = cursor.getInt(0)
                words.word = cursor.getString(2)
                words.transWord = cursor.getString(3)
                wordsList.add(words)
            } while (cursor.moveToNext())

        }
        cursor.close()

        //----Show the selected arrayList in recyclerview-----//
        if (wordsList.size > 0) {
            rv_edit.setHasFixedSize(true)
            rv_edit.setNestedScrollingEnabled(false)
            rv_edit.layoutManager = LinearLayoutManager(applicationContext)
            rv_edit.adapter = AdapterRowEditWord(db, wordsList)


        } else {
            funActivity.delayToast(R.string.No_data, applicationContext)
        }
    }


}








