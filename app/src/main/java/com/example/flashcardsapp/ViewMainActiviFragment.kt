package com.example.flashcardsapp

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteBlobTooBigException
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.view_main_activi_fragment.*

open class ViewMainActiviFragment : Fragment() {

    private val funActivity = FunActivity()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.view_main_activi_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()

        //---Get context in fragment---//
        val activity = activity as Context
        //---Edit sqlDAtabse---//
        val db = funActivity.sqlDb(activity)

        //---Cursor set in all rows where are Image_resource" is not null---//
        val cursor = setCursor(activity)

        //---Get Elements from sqlDatabase---//
        val lessons = getElementsFromSql(cursor, activity)

        //----Show the selected arrayList in recyclerview-----//
        recyclerviewLesson.setHasFixedSize(true)
        recyclerviewLesson.layoutManager = LinearLayoutManager(
            activity, LinearLayoutManager.HORIZONTAL, false)
        (recyclerviewLesson.layoutManager as LinearLayoutManager).reverseLayout = true
        (recyclerviewLesson.layoutManager as LinearLayoutManager).stackFromEnd = true
        recyclerviewLesson.adapter = AdapterBigCardview(db, lessons
        )

    }


    fun setCursor (context: Context) :Cursor{
        //---Edit sqlDAtabse---//
        val db = funActivity.sqlDb(context)
        //---Cursor set in all rows where are Image_resource" is not null---//
        val selectQuery = "SELECT * FROM LESSON_TABLE WHERE Image_resource IS NOT NULL"
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        return cursor
    }


    //---Get Elements from sqlDatabase---//
    fun getElementsFromSql(cursor: Cursor, context: Context): ArrayList<LessonModelClass> {
        //--From specific rows in sql get ids, images and lesson names
        //-- and put these all to arrayList --//
        val lessons = ArrayList<LessonModelClass>()
        //------If image too big show expetion----//
        try {

            if ((cursor.moveToFirst())) {
                do {
                    val lesson = LessonModelClass()
                    lesson.id = cursor.getInt(0)
                    lesson.imageResource = cursor.getBlob(4)
                    lesson.sortLessonName = cursor.getString(5)
                    lessons.add(lesson)

                } while (cursor.moveToNext())
            }
            cursor.close()

        } catch (ex:Exception) {
            when(ex) {
                is SQLiteBlobTooBigException,
                is NullPointerException -> {
                    funActivity.delayToast(R.string.too_big_format, context)

                }
            }
        }
        return lessons
    }


}


