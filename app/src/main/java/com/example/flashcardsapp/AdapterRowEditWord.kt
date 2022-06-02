package com.example.flashcardsapp

import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_create_lesson.*
import java.util.ArrayList

class AdapterRowEditWord(val db: SQLiteDatabase, var lessons: ArrayList<LessonModelClass>) :
    RecyclerView.Adapter<AdapterRowEditWord.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.row_edit_word, parent, false
        )

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.etEditWord.setText(lessons[holder.adapterPosition].word)
        holder.etEditTrans.setText(lessons[holder.adapterPosition].transWord)

        val word = lessons[holder.adapterPosition].word

        //---------Delete one row-------//
        holder.ivEditDelete.setOnClickListener {
            val mBuilder = AlertDialog.Builder(holder.context)
                .setTitle(R.string.Delete_Warning)
                .setMessage("Delete this: '$word'?")
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton(R.string.Delete, DialogInterface.OnClickListener { dialog, which ->

                    //---------Delete words from sqlDatabse-------//
                    db.delete(
                        myTable.TABLE_NAME,
                        BaseColumns._ID + "=?",
                        arrayOf(lessons[holder.adapterPosition].id.toString())
                    )
                    lessons.removeAt(holder.adapterPosition)
                    notifyItemRemoved(holder.adapterPosition)
                })

                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()

                })
            mBuilder.show()

        }

        //------Add changed words to sqlDatabase--------//
        holder.ivEditSave.setOnClickListener {

            //>>>>----Get user's new words-----///
            val stringWord = holder.etEditWord.text.toString()
            val stringTransWord = holder.etEditTrans.text.toString()

            //>>>>-----Set new words to sqlDatabase-----//
            val contentValue = ContentValues()
            contentValue.put(myTable.WORD, stringWord)
            contentValue.put(myTable.TRANS_WORD, stringTransWord)
            contentValue.put(myTable.WORK_ON_WORD, stringWord)
            contentValue.put(myTable.WORK_ON_TRANS_WORD, stringTransWord)

            //>>>--------Check all editText length -> has to be > 0 ----------//
            if ((stringWord.length <= 0) && (stringTransWord.length <= 0)) {
                holder.etEditWord.toInt()
                holder.etEditTrans.toInt()
            } else if (stringTransWord.length <= 0) {
                holder.etEditTrans.toInt()
            } else if ((stringWord.length <= 0)) {
                holder.etEditWord.toInt()
            } else {

                //>>>>----Put changes-----//
                db.update(
                    myTable.TABLE_NAME, contentValue,
                    BaseColumns._ID + "=?",
                    arrayOf(lessons[holder.adapterPosition].id.toString())
                )

                val mToast = FunActivity()
                mToast.delayToast(R.string.Change_was_made, holder.context)

            }


        }
    }

    override fun getItemCount(): Int {
        return lessons.size

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val etEditWord: EditText = itemView.findViewById(R.id.et_edit_word)
        val etEditTrans: EditText = itemView.findViewById(R.id.et_edit_trans)
        val ivEditSave: ImageView = itemView.findViewById(R.id.iv_edit_save)
        val ivEditDelete: ImageView = itemView.findViewById(R.id.iv_edit_delete)
        val context: Context = itemView.context
    }

    //-----Error for all editTexts' places -----//
    fun EditText.toInt(): Int {
        val value: Int = try {

            this.text.toString().toInt()
        } catch (nfe: NumberFormatException) {

            this.error = "Invalid_input"
            0
        }
        return value
    }


}