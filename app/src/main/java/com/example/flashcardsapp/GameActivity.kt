package com.example.flashcardsapp

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.provider.BaseColumns
import android.util.Log
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    private var counter = 0;
    private var wordArray: ArrayList<GameModelClass> = ArrayList()
    private var transArray: ArrayList<GameModelClass> = ArrayList()
    private var lessonName: String? = " "
    private var reviseSize: String? = " "
    private var revised: Int = 0
    private var displayNum: String = "";
    private var displayWord: String = ""
    private var gameNum1: String = "";
    private var gameNum2: String = "";
    private var gameNum3: String = "";
    private var gameNum4: String = ""
    private var gameTrans1: String = "";
    private var gameTrans2: String = "";
    private var gameTrans3: String = "";
    private var gameTrans4: String = ""
    private var numForGN1 = -1;
    private var numForGN2 = -1;
    private var numForGN3 = -1;
    private var numForGN4 = -1
    private var numForDis = -1
    private var fcSize: Int = 0
    private val funActivity = FunActivity()
    private  val green = "#D00EEA19";
    private val red = "#E6FB0606";
    private val black = "#383940"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        //--------------Set Lesson Name in activity---------------//
        lessonName = intent.getStringExtra("EXTRA_NAME5")
        tv_game_name.setText(lessonName)

        //---Set number of flashcards to revise---//
        reviseSize = intent.getStringExtra("EXTRA_NUM")
        tv_game_numbers.setText("0/$reviseSize")

        //---Fun edit sql---//
        val db = funActivity.sqlDb(applicationContext)

        //---------------Get size of one column from sqlDatabase ----------//
        fcSize = funActivity.getColumnNotNull(
            db, "lesson_name", lessonName!!, "work_on_word", 6
        ).size


        //----Game mechanics-----//
        startGame()

        //---------------Button 1--------------//
        btn_game_1.setOnClickListener {
            executionButton(gameNum1, btn_game_1)
        }

        //---------------Button 2--------------//
        btn_game_2.setOnClickListener {
            executionButton(gameNum2, btn_game_2)
        }

        //---------------Button 3--------------//
        btn_game_3.setOnClickListener {
            executionButton(gameNum3, btn_game_3)
        }

        //---------------Button 4 --------------//
        btn_game_4.setOnClickListener {
            executionButton(gameNum4, btn_game_4)

        }

    }


    private fun executionButton(gameNum: String, btn: Button) {
        //>>>---- What a correct answer does----//
        if (displayNum == gameNum) {
            //>>>>-----Set null to correct answer------//
            corectAndDelete(displayNum)
            btn.setTextColor(Color.parseColor(green))
            unableButtons()
            ll_game.isEnabled = true
            changeNumbers()
            checkStation()
        } else {
            btn.setTextColor(Color.parseColor(red))
            unableButtons()
            ll_game.isEnabled = true
            checkStation()
        }

        tv_game_empty_space.setText(R.string.Click_an_empty_space)

    }


    //--------------Back pressed button-------------//
    override fun onBackPressed() {
        //>>>>>>--------Click two times to back MainActivity---------//
        counter++

        Handler(Looper.myLooper()!!).postDelayed({
            counter = 0
        }, 1000)

        if (counter == 2) {
            super.onBackPressed()

            val intent = Intent(applicationContext, MainActivity::class.java)
            finishAffinity()
            startActivity(intent)
        } else {
            funActivity.delayToast(R.string.Click_two_times_to_leave, this)

        }

    }

    //---Hide the flashcard after a correct answer---//
    private fun corectAndDelete(number: String) {

        //>>>>-----Change value of flashcards to null-------//
        val db = funActivity.sqlDb(applicationContext)

        val contentValue = ContentValues()
        contentValue.putNull(myTable.WORK_ON_WORD)
        contentValue.putNull(myTable.WORK_ON_TRANS_WORD)

        db.update(
            myTable.TABLE_NAME, contentValue,
            BaseColumns._ID + "=?",
            arrayOf(number)
        )

    }

    //-------------------Game mechanics------------------//
    private fun startGame() {

        //>>>>---Get two columns (word and wordId) from Sql-------//
        val db = funActivity.sqlDb(applicationContext)

        wordArray = funActivity.getColumnsStringId(
            db, "lesson_name",
            lessonName!!, "work_on_word", 6
        )


        transArray = funActivity.getColumnsStringId(
            db, "lesson_name",
            lessonName!!, "work_on_trans_word", 7
        )


        //>>>>--------Draw id of word------------//
        while (true) {
            try {

                numForDis = (0 until wordArray.size).random()

                numForGN1 = (0 until wordArray.size).random()
                numForGN2 = (0 until wordArray.size).random()
                numForGN3 = (0 until wordArray.size).random()
                numForGN4 = (0 until wordArray.size).random()

            } catch (e: NoSuchElementException) {
                Log.d("TAG", "Error GameActivity")
            }


            val firstRule: ArrayList<Int> = ArrayList(4)

            firstRule.add(numForGN1);firstRule.add(numForGN2)
            firstRule.add(numForGN3);firstRule.add(numForGN4)

            //>>>>>-------If the sql has more then 3 flashcards----------//
            if (fcSize > 3) {
                //>>>---Min one transWord has to have the same id what word----//
                if (firstRule.contains(numForDis)) {

                    //>>>-------Examples of transWorld can't have the same id------//
                    if ((numForGN1 != numForGN2) && (numForGN1 != numForGN3) && (numForGN1 != numForGN4)
                        && (numForGN2 != numForGN3) && (numForGN2 != numForGN4) && (numForGN3 != numForGN4)
                    )
                        break
                }

                //>>--If the sql has 1 flashcards implement only first rule---//
            } else if (fcSize == 1) {
                (firstRule.contains(numForDis))
                break

            } else {
                if (firstRule.contains(numForDis)) {
                    if ((numForGN1 != numForGN2) && (numForGN4 != numForGN3)
                    )
                        break
                }
            }


        }

        //>>>------From id get a selected flashcard----------//
        try {
            displayNum = wordArray[numForDis].id.toString()
            displayWord = wordArray[numForDis].word

            //>>>--Tralnstale words id-----//
            gameNum1 = transArray[numForGN1].id.toString()
            gameNum2 = transArray[numForGN2].id.toString()
            gameNum3 = transArray[numForGN3].id.toString()
            gameNum4 = transArray[numForGN4].id.toString()

            //>>>---get translate words by id-------//
            gameTrans1 = transArray[numForGN1].word
            gameTrans2 = transArray[numForGN2].word
            gameTrans3 = transArray[numForGN3].word
            gameTrans4 = transArray[numForGN4].word
        } catch (e: IndexOutOfBoundsException) {
            Log.d("TAG", "Error2 GameActivity ")
        }

        //>>>---the selected flashcard show in textView------//
        tv_game_main.setText(displayWord)

        btn_game_1.setText(gameTrans1)
        btn_game_2.setText(gameTrans2)
        btn_game_3.setText(gameTrans3)
        btn_game_4.setText(gameTrans4)

    }

    private fun changeNumbers() {
        //>>>>-----Rise number of correct answer--------//
        revised++
        //>>>>----Decrease number of id to draw--------//
        fcSize--
        tv_game_numbers.setText("$revised/$reviseSize")

    }


    //---------Enable all buttons---------//
    private fun enableButtons() {
        val list = buttonList()
        for (i in list) {
            i.isEnabled = true
        }
    }

    //---------unable all buttons---------//
    private fun unableButtons() {
        val list = buttonList()
        for (i in list) {
            i.isEnabled = false
        }
    }

    //--------All buttons in ArrayList---------//
    private fun buttonList(): ArrayList<Button> {
        val list: ArrayList<Button> = ArrayList()
        list.add(btn_game_1); list.add(btn_game_3)
        list.add(btn_game_2); list.add(btn_game_4)

        return list
    }


    private fun checkStation() {

        //>>>>--------Layout is a check Button------------//
        ll_game.setOnClickListener {

            //>>>>---- For all buttoms set color black------//
            val list = buttonList()
            for (i in list) {
                i.setTextColor(Color.parseColor(black))
            }

            //-------- if user has revised all falshcard Go to MainActivity----------//
            if (revised >= reviseSize!!.toInt()) {
                val intent = Intent(applicationContext, CongratuActivity::class.java)
                startActivity(intent)

                //-------- if user hasn't revised all falshcard play again----------//
            } else {
                startGame()
                enableButtons()
            }

            tv_game_empty_space.setText("")
            ll_game.isEnabled = false

        }

    }


}

