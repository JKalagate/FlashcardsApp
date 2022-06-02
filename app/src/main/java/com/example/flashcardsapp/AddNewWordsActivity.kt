package com.example.flashcardsapp

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.size
import kotlinx.android.synthetic.main.activity_add_new_words.*
import kotlinx.android.synthetic.main.activity_create_lesson.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.row_add_word.*
import kotlin.math.log

class AddNewWordsActivity : AppCompatActivity() {

    private var layoutListAddWord: LinearLayout? = null

    private val funActivity = FunActivity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_words)

        //----------Set name lesson in activity----------//
        val lessonName = intent.getStringExtra("EXTRA_NAME")
        tv_lesson_name.setText(lessonName)

        layoutListAddWord = findViewById(R.id.layout_list_add_words)

    //---Add one row---//
        addOneRow()

   //-------------Add row of words-----------------//
        btn_add_words.setOnClickListener {
            addOneRow()

        }
       //---------- Submit our words and save in sqlDatabase--------//
        btn_submit_words.setOnClickListener {

            //>>>>----- Get words from all rows--------//
            val childCount = layoutListAddWord?.childCount
            for (i in 0 until childCount!!) {
                val example = layoutListAddWord?.getChildAt(i)

                val word = example?.findViewById<View>(R.id.et_word_row) as EditText
                val transWord = example?.findViewById<View>(R.id.et_trans_word_row) as EditText

                var stringWord = word.text.toString()
                var stringTransWord = transWord.text.toString()

                //>>>>-------Edit sqlDatabse---------//
                val db = funActivity.sqlDb(applicationContext)

                //>>>>>-----Show error if editText < 0 ------------//
                if ((stringWord.length <= 0) && (stringTransWord.length <= 0)) {
                    word.toInt()
                    transWord.toInt()
                } else if (stringTransWord.length <= 0) {
                    transWord.toInt()
                } else if ((stringWord.length <= 0)) {
                    word.toInt()
                } else {

                    //>>>>-Add new data to SqlDatabase and back to MainAcctivity--//
                    val contentValue = ContentValues()
                    contentValue.put(myTable.LESSON_NAME, lessonName)
                    contentValue.put(myTable.WORD, stringWord)
                    contentValue.put(myTable.TRANS_WORD, stringTransWord)
                    contentValue.put(myTable.WORK_ON_WORD, stringWord)
                    contentValue.put(myTable.WORK_ON_TRANS_WORD, stringTransWord)
                    db.insertOrThrow(myTable.TABLE_NAME, null, contentValue)

                    //>>>>----Back to MainActivity-----//
                    onBackPressed()

                }
            }

        }

    }

    //---------Delete one row--------//
    private fun removeView(lessonView: View?) {
        layout_list_add_words.removeView(lessonView)

    }

    //---Add a row = 2 editText, deleteIcon,
    private fun addOneRow() {
        val lessonView = layoutInflater.inflate(
            R.layout.row_add_word, null, false
        )
        //>>>>------Delete icon--------//
        val ivDelete = lessonView.findViewById<View>(R.id.iv_delete)

        //>>>>------Delete one row--------//
        ivDelete.setOnClickListener {
            removeView(lessonView)

        }
        //>>>-----Show a Row------//
        layoutListAddWord?.addView(lessonView)

    }

    //-----Error for all editTexts' places -----//
    fun EditText.toInt(): Int {
        val value: Int = try {

            this.text.toString().toInt()
        } catch (nfe: NumberFormatException) {

            this.error = getString(R.string.invalid_input)
            0
        }
        return value
    }
}