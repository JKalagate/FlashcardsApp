package com.example.flashcardsapp

import android.animation.ObjectAnimator
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.ArrayList


class AdapterAllLessons(val db: SQLiteDatabase, var lessons: ArrayList<LessonModelClass>) :
    RecyclerView.Adapter<AdapterAllLessons.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.row_list_lessons, parent, false
        )
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //>>>>>-----------Set name and image in the correct order---------------//
        val lessonImage: ByteArray = lessons[holder.adapterPosition].imageResource
        val bitmap: Bitmap = BitmapFactory.decodeByteArray(lessonImage, 0, lessonImage.size)
        holder.ivListImage.setImageBitmap(bitmap)

        //>>>>>--------Package = lesson's name for intent---------------//
        holder.tvListName.setText(lessons[holder.adapterPosition].sortLessonName)

        val lessonName = holder.tvListName.text.toString()

        //--Get fun from funActivity--//
        val funActivity = FunActivity()
        //>>>>----------Get size of a specify column----------//
        val listSize = funActivity.getColumn(db, "lesson_name", lessonName, 2).size

        //----------------------Progress Bar--------------------------//

        //>>>>----------Get size of made progress----------//
        val progress = funActivity.getColumnNull(
            db, "lesson_name", lessonName, "work_on_word"
        ).size

        //>>>>>-- For each cardview add progessbar-----//
        val progressList: ArrayList<String> = ArrayList()
        progressList.add(lessonName)

        for (i in progressList) {
            holder.listProgress.max = listSize
            holder.tvListAmount.setText("$progress/$listSize")

        }

        //>>>>>--- Animation of progressbar------//
        val currentProgress = progress

        ObjectAnimator.ofInt(holder.listProgress, "progress", currentProgress)
            .setDuration(0)
            .start()

        //---------Go to PlayActivity = start game--------//
        holder.cvList.setOnClickListener{
            val intent = Intent(holder.context, PlayActivity::class.java)
            intent.putExtra("EXTRA_SIZE", listSize.toString())
            intent.putExtra("EXTRA_NAME4", lessonName)
            intent.putExtra("EXTRA_PROGRESS", progress.toString())
            holder.context.startActivity(intent)
        }

        //------------------------Open button sheet dialog----------------------//
        holder.ivListOptions.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(holder.context, R.style.BottomSheetTheme)

            //>>>>------------Get elements from sheet dialog------------//
            val btnSv = LayoutInflater.from(holder.context).inflate(
                R.layout.buttom_sheet_dialog,
                it.findViewById(R.id.bottom_sheet) as LinearLayout?
            )

            //>>>>--Add new words to lesson, go to activi AddNewWordsActivity--//
            btnSv.findViewById<View>(R.id.ll_add_words).setOnClickListener {
                val intent = Intent(holder.context, AddNewWordsActivity::class.java)
                intentAdapter(holder.context, intent, "EXTRA_NAME", lessonName)
                bottomSheetDialog.dismiss()
            }

            //>>>>--Check and edit words , go to activi ViewEditWordLesson--//
            btnSv.findViewById<View>(R.id.ll_check_words).setOnClickListener {
                val intent = Intent(holder.context, ViewEditWordLesson::class.java)
                intentAdapter(holder.context, intent, "EXTRA_NAME2", lessonName)
                bottomSheetDialog.dismiss()
            }

            //>>>>--Change lesson name or image , go to activi CreateLessonActivity--//
            btnSv.findViewById<View>(R.id.ll_edit_lesson).setOnClickListener {
                val id_edit = lessons[holder.adapterPosition].id.toString()
                val intent = Intent(holder.context, CreateLessonActivity::class.java)
                intent.putExtra("EXTRA_ID", id_edit)
                intent.putExtra("EXTRA_NAME3", lessonName)
                holder.context.startActivity(intent)
                bottomSheetDialog.dismiss()
            }

            //>>>>----------Change progressBar status to zero------------//
            btnSv.findViewById<View>(R.id.ll_refresh_lesson).setOnClickListener {
                var wordList: ArrayList<String> = ArrayList()
                var idList: ArrayList<String> = ArrayList()
                var transList: ArrayList<String> = ArrayList()

                val contentValue = ContentValues()

                val mBuilder = AlertDialog.Builder(holder.context)
                    .setTitle(R.string.Refresh_Warning)
                    .setMessage("Refresh this: '$lessonName'?")
                    .setIcon(R.drawable.ic_refresh)
                    .setPositiveButton(R.string.Ok, DialogInterface.OnClickListener { dialog, which ->

                        // >>>>>------Set 0 to progressbar in view------------//
                        lessons.set(
                            holder.adapterPosition, LessonModelClass(
                                lessons[holder.adapterPosition].id,
                                lessons[holder.adapterPosition].word,
                                lessons[holder.adapterPosition].transWord,
                                lessons[holder.adapterPosition].sortLessonName,
                                lessons[holder.adapterPosition].imageResource
                            )
                        )
                        //>>>>---ProgressBar = zero -> don't refresh------//
                        if (progress != 0) {
                            notifyDataSetChanged()

                        }

                        //>>>>--After refresh -> fill columns responsible for the progressbar in sql-----//
                        idList = funActivity.getColumn(db, "lesson_name", lessonName, 0)
                        wordList = funActivity.getColumn(db, "lesson_name", lessonName, 2)
                        transList = funActivity.getColumn(db, "lesson_name", lessonName, 3)

                        val word = wordList.iterator()
                        val id = idList.iterator()
                        val trans = transList.iterator()

                        //>>>>-------Fill one row and go next--------//
                        while (word.hasNext() && id.hasNext() && trans.hasNext()) {

                            contentValue.put(myTable.WORK_ON_WORD, word.next())
                            contentValue.put(myTable.WORK_ON_TRANS_WORD, trans.next())

                            //-------Change data in sqlDatabase-------//
                            db.update(
                                myTable.TABLE_NAME, contentValue,
                                BaseColumns._ID + "=?",
                                arrayOf(id.next())
                            )
                        }

                    })

                    .setNegativeButton(R.string.Cancel, DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()

                    })

                mBuilder.show()
                bottomSheetDialog.dismiss()

            }

            //>>>>-------Delete lesson from sqlDatabse----------//
            btnSv.findViewById<View>(R.id.ll_delete_lesson).setOnClickListener {
                val mBuilder = AlertDialog.Builder(holder.context)
                    .setTitle(R.string.Delete_Warning)
                    .setMessage("Delete this: '$lessonName'?")
                    .setIcon(R.drawable.ic_delete)
                    .setPositiveButton(R.string.Delete, DialogInterface.OnClickListener { dialog, which ->

                        //>>>>------Delete lesson's name and image-------//
                        db.delete(
                            myTable.TABLE_NAME,
                            myTable.SORT_LESSON_NAME + "=?",
                            arrayOf(lessons[holder.adapterPosition].sortLessonName)
                        )
                        lessons.removeAt(holder.adapterPosition)
                        notifyItemRemoved(holder.adapterPosition)

                        //>>>>------Delete all word in this lesson-------//
                        db.delete(
                            myTable.TABLE_NAME,
                            myTable.LESSON_NAME + "=?",
                            arrayOf("$lessonName")
                        )

                    })

                    .setNegativeButton(R.string.Cancel, DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()

                    })
                mBuilder.show()
                bottomSheetDialog.dismiss()

            }

            bottomSheetDialog.setContentView(btnSv)
            bottomSheetDialog.show()


        }
    }

    override fun getItemCount(): Int {
        return lessons.size

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivListImage: ImageView = itemView.findViewById(R.id.iv_list_image)
        val tvListName: TextView = itemView.findViewById(R.id.tv_list_name)
        val ivListOptions: ImageView = itemView.findViewById(R.id.iv_list_options)
        val listProgress: ProgressBar = itemView.findViewById(R.id.list_progress)
        val tvListAmount: TextView = itemView.findViewById(R.id.tv_list_amount)
        val cvList: CardView = itemView.findViewById(R.id.cv_list)
        val context: Context = itemView.context

    }

    private fun intentAdapter(context: Context, intent: Intent,
        nameIntent: String, parcel: String) {
        intent.putExtra(nameIntent, parcel)
        context.startActivity(intent)

    }


}

