package com.example.flashcardsapp

import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var counter = 0;
    private val funActivity = FunActivity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //------Go to create Lesson-------//
        btn_ma_cl.setOnClickListener {
            val intent = Intent(applicationContext, CreateLessonActivity::class.java)
            startActivity(intent)
        }
        //------Go to see list of Lessons-------//
        btn_all_Lesson.setOnClickListener {
            val intent = Intent(applicationContext, ViewListLesson::class.java)
            startActivity(intent)
        }

        //-----------See stats----------//
        btn_all_stats.setOnClickListener {
            funActivity.delayToast(R.string.stats_coming_soon, this)
        }

        //----------See options---------//
        btn_all_options.setOnClickListener {
            funActivity.delayToast(R.string.option_coming_soon, this)
        }

        //----------See warning---------//
        btn_all_warning.setOnClickListener {
            funActivity.delayToast(R.string.warning_coming_soon, this)
        }

        fragment()

    }

    //----------Show main_activity_cardview------------//
    private fun fragment () {
        val fm = supportFragmentManager
        val lessonFragment = ViewMainActiviFragment()

        fm.beginTransaction().add(R.id.fragment_container_main, lessonFragment)
            .commit()

    }

    override fun onBackPressed() {
        //>>>>>>--------Click two times to close app---------//
        counter++

        Handler(Looper.myLooper()!!).postDelayed({
            counter = 0
        }, 1000)

        if (counter == 2) {
            super.onBackPressed()

        } else {
            funActivity.delayToast(R.string.Click_two_times_to_leave, this)
        }

    }



}