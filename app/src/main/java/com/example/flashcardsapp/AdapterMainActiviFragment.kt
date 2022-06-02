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
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList

class AdapterBigCardview(val db: SQLiteDatabase, var lessons: ArrayList<LessonModelClass>) :
    RecyclerView.Adapter<AdapterBigCardview.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.row_main_activity_fragment, parent, false
        )
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //>>>>>-----------Set name and image in the correct order---------------//
        holder.tvNameCvbv.setText(lessons[holder.adapterPosition].sortLessonName)
        val lessonImage: ByteArray = lessons[holder.adapterPosition].imageResource
        val bitmap: Bitmap = BitmapFactory.decodeByteArray(lessonImage, 0, lessonImage.size)
        holder.ivImageCvbv.setImageBitmap(bitmap)

        //>>>>>--------Package = lesson's name for intent---------------//
        val lessonName = lessons[holder.adapterPosition].sortLessonName


        //>>>>>--------- Amount of flashcards in lesson----------------//
        val lessonFun = FunActivity()
        val listSize = lessonFun.getColumn(db, "lesson_name", lessonName, 2).size


        //>>>>>------------------Progress Bar--------------------------//

        val progress2 = lessonFun.getColumnNull(
            db, "lesson_name", lessonName, "work_on_word").size


                //>>>>>-- For each cardview add progessbar-----//
        val progressList : ArrayList<String> = ArrayList()
        progressList.add(lessonName)

        for (i in progressList){
            holder.pbCvbv.max = listSize
            holder.tvNumberCvbv.setText("$progress2/$listSize")

        }

                 //>>>>>--- Animation of progressbar------//
        val currentProgress = progress2 //.toInt()

        ObjectAnimator.ofInt(holder.pbCvbv, "progress", currentProgress)
            .setDuration(0)
            .start()


        //>>>>>--------------Delete lesson and lesson's words-------------------//
        holder.ivDeleteCvbv.setOnClickListener {

            val mBuilder = AlertDialog.Builder(holder.context)
                .setTitle(R.string.Delete_Warning)
                .setMessage("Delete this: '$lessonName'?" )
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton(R.string.Delete, DialogInterface.OnClickListener { dialog, which ->

                //>>>>>----------Delete lesson-------------//
                    db.delete(
                        myTable.TABLE_NAME,
                        myTable.SORT_LESSON_NAME + "=?",
                        arrayOf(lessons[holder.adapterPosition].sortLessonName)
                    )
                    lessons.removeAt(holder.adapterPosition)
                    notifyItemRemoved(holder.adapterPosition)

                //>>>--- Delete flashcards in lesson--------//
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

        }

        //>>>>>------ Change image or name of the lesson -------------//
        holder.ivEditCvbv.setOnClickListener {

            val id_edit = lessons[holder.adapterPosition].id.toString()
            val intent = Intent(holder.context, CreateLessonActivity::class.java)
            intent.putExtra("EXTRA_ID", id_edit)
            intent.putExtra("EXTRA_NAME3", lessonName)
            holder.context.startActivity(intent)

        }

        //>>>>>---------------Go to add new flashcards--------------------//
        holder.ivAddCvbv.setOnClickListener {

            val intent = Intent(holder.context, AddNewWordsActivity::class.java)
            intentAdapter(holder.context, intent,"EXTRA_NAME", lessonName )
        }

        //>>>>>-------------- Check flashcards ------------------------//
        holder.ivStorageCvbv.setOnClickListener {

            val intent = Intent(holder.context, ViewEditWordLesson::class.java)
            intentAdapter(holder.context, intent,"EXTRA_NAME2", lessonName )

        }

        //>>>>>---------------- Start game-------------------------//
        holder.cvCvbv.setOnClickListener {
            val intent = Intent(holder.context, PlayActivity::class.java)
            intent.putExtra("EXTRA_SIZE", listSize.toString())
            intent.putExtra("EXTRA_NAME4", lessonName)
            intent.putExtra("EXTRA_PROGRESS", progress2.toString())
            holder.context.startActivity(intent)
        }

        // >>>>>------------Refresh to zero progressbar-------------------//
        holder.ivRefreshCvbv.setOnClickListener {

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
                    lessons.set(holder.adapterPosition, LessonModelClass(
                        lessons[holder.adapterPosition].id,
                        lessons[holder.adapterPosition].word,
                        lessons[holder.adapterPosition].transWord,
                        lessons[holder.adapterPosition].sortLessonName,
                        lessons[holder.adapterPosition].imageResource))

                    if (progress2 != 0) {
                        notifyDataSetChanged()

                    }

                    //>>>>--After refresh -> , fill columns responsible for the progressbar in sql-----//
                    idList = lessonFun.getColumn(db,"lesson_name", lessonName, 0)
                    wordList = lessonFun.getColumn(db,"lesson_name", lessonName, 2)
                    transList = lessonFun.getColumn(db,"lesson_name", lessonName, 3)

                    val word = wordList.iterator()
                    val id = idList.iterator()
                    val trans = transList.iterator()

                    //>>>>-------Fill one row and go next--------//
                    while (word.hasNext() && id.hasNext() && trans.hasNext()){

                        contentValue.put(myTable.WORK_ON_WORD, word.next())
                        contentValue.put(myTable.WORK_ON_TRANS_WORD, trans.next())

                        db.update(
                            myTable.TABLE_NAME, contentValue,
                            BaseColumns._ID + "=?",
                            arrayOf(id.next()))
                    }

                })

                .setNegativeButton(R.string.Cancel, DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()

                })

            mBuilder.show()

           }
    }

    override fun getItemCount(): Int {
        return lessons.size

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImageCvbv: ImageView = itemView.findViewById(R.id.iv_image_cvbv)
        val tvNameCvbv: TextView = itemView.findViewById(R.id.tv_name_cvbv)
        val ivDeleteCvbv: ImageView = itemView.findViewById(R.id.iv_delete_cvbv)
        val ivEditCvbv: ImageView = itemView.findViewById(R.id.iv_edit_cvbv)
        val ivAddCvbv: ImageView = itemView.findViewById(R.id.iv_add_cvbv)
        val ivStorageCvbv: ImageView = itemView.findViewById(R.id.iv_storage_cvbv)
        val pbCvbv: ProgressBar = itemView.findViewById(R.id.pb_cvbv)
        val tvNumberCvbv: TextView = itemView.findViewById(R.id.tv_number_cvbv)
        val ivRefreshCvbv: ImageView = itemView.findViewById(R.id.iv_refresh_cvbv)
        val cvCvbv: CardView = itemView.findViewById(R.id.cv_cvbv)
        val context: Context = itemView.context
    }

}

//-------------------Intent function---------------------//
private fun intentAdapter (context: Context, intent: Intent,
                           nameIntent:String, parcel:String) {
    intent.putExtra(nameIntent, parcel)
    context.startActivity(intent)


}
