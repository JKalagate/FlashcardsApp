package com.example.flashcardsapp

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_play.*

class PlayActivity : AppCompatActivity() {

    private var mAmount = 0

    //--Size of flashcards and progressbar-- //
    private  var lessonSize: String? = ""
    private var progressSize: String? =""
    private var lessonName: String? =" "

    private  var funActivity = FunActivity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        et_play_amount.setText("0")

        //--------------Set Lesson Name in activity---------------//
        lessonName = intent.getStringExtra("EXTRA_NAME4")
        tv_play_name.setText(lessonName)

        //---Set amount of flashcards and progressbar in activity---//
        lessonSize = intent.getStringExtra("EXTRA_SIZE")
        tv_play_number.setText("Number of flashcards in the lesson: $lessonSize")

        progressSize = intent.getStringExtra("EXTRA_PROGRESS")
        tv_play_quantity.setText("$progressSize/$lessonSize")

        //--progressbar--//
        animOfProgBar()

        //-------- Increse number of flashcards to revise -------//
        btn_play_add.setOnClickListener {
            mAmount = et_play_amount.text.toString().toInt()
            if (mAmount < lessonSize!!.toInt() - progressSize!!.toInt()) {
                mAmount++
                et_play_amount.setText(mAmount.toString())
            }
        }

        //-------- Decrease number of flashcards to revise -------//
        btn_play_min.setOnClickListener {
            mAmount = et_play_amount.text.toString().toInt()
            if (mAmount > 0) {
                mAmount--
                et_play_amount.setText(mAmount.toString())
            }

        }

        //------------------ Start to revice flashcards---------------------//
        btn_play_start.setOnClickListener {
            if (et_play_amount.length() >= 1) {

                mAmount = et_play_amount.text.toString().toInt()
                if (mAmount >= 1 && mAmount <= (lessonSize!!.toInt() - progressSize!!.toInt())) {
                    val intent = Intent(applicationContext, GameActivity::class.java)
                    intent.putExtra("EXTRA_NUM", mAmount.toString())
                    intent.putExtra("EXTRA_NAME5", lessonName)
                    startActivity(intent)
                } else {
                    funActivity.delayToast(R.string.Wrong_number_of_flashcards, this)
                }
            } else {
                et_play_amount.error = getString(R.string.invalid_input)
            }
        }

    }

    //--------------------- Animation of progressbar------------------------//
    private fun animOfProgBar () {
        pb_play.max = lessonSize!!.toInt()
        //>>>>------------Edit SqlDatabase-----------//
        val db = funActivity.sqlDb(applicationContext)
        //>>>>------------Get Size of progressBar-----------//
        var currentProgress = funActivity.getColumnNull(
            db, "lesson_name", lessonName!!, "work_on_word").size

        ObjectAnimator.ofInt(pb_play, "progress" , currentProgress)
            .start()
    }
}



