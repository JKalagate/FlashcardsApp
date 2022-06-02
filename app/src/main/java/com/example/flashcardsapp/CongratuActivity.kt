package com.example.flashcardsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_congratu.*

class CongratuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_congratu)

        //-Show random congratulations, after user wins the game-//
        val list:ArrayList<Int> = ArrayList()

        val one = R.string.one
        val two = R.string.two
        val three = R.string.three
        val four = R.string.four
        val five = R.string.five
        val six = R.string.six
        val seven = R.string.seven
        val eight = R.string.eight
        val nine = R.string.nine

        list.add(one); list.add(two); list.add(three)
        list.add(four); list.add(five); list.add(six)
        list.add(seven); list.add(eight); list.add(nine)

        val number = (0 until list.size).random()

        tv_congra.setText(list.get(number))

        ll_congra.setOnClickListener {
            funIntent()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        funIntent()
    }

    //---------Go to MainActivity-------//
    private fun funIntent() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        //----Restart the stack----//
        finishAffinity()
        startActivity(intent)
    }

}