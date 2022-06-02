package com.example.flashcardsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.view_list_lessons.*

class ViewListLesson : AppCompatActivity() {

    private val funActivity = FunActivity()
    private  val viewMainActiviFragment = ViewMainActiviFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_list_lessons)

        onStart()

    }

    override fun onStart() {
        super.onStart()

        //---Edit sqlDatabse---//
        val db = funActivity.sqlDb(applicationContext)

        //---Cursor set in all rows where are Image_resource" in not null---//
        val cursor = viewMainActiviFragment.setCursor(applicationContext)

        //---Get Elements from sqlDatabase---//
        val list = viewMainActiviFragment.getElementsFromSql(cursor, applicationContext)

        //----Show the selected arrayList in recyclerview-----//
        recyclerviewAll.setHasFixedSize(true)
        recyclerviewAll.layoutManager = LinearLayoutManager(applicationContext)
        recyclerviewAll.adapter = AdapterAllLessons(db, list)
    }

}




