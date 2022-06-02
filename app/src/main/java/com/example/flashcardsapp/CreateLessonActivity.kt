package com.example.flashcardsapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.graphics.drawable.toBitmap
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_create_lesson.*
import java.io.ByteArrayOutputStream
import java.io.InputStream


class CreateLessonActivity : AppCompatActivity() {

    private var funActivity = FunActivity()

    //-------Instruction, what kind of image user can get from phone---------//
    private val cropActivityResultContracts = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setAspectRatio(6, 4)
                .setOutputCompressQuality(20)
                .setMultiTouchEnabled(true)
                .setMaxCropResultSize(1800, 1500)
                .getIntent(this@CreateLessonActivity)

        }
        //------------Give back cropped Image----------//
        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>
    //------------------------------------------------------------------------------//


    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_lesson)

        //----Different titles depend on what user does: create or edit lesson----//
        if (intent.hasExtra("EXTRA_ID")) {
            tv_create_lesson.setText("Editing your lesson")
        } else {
            tv_create_lesson.setText("Creating new lesson")
        }

        //---If user is editing show the specific image and lesson name--//
        getNameAndImage()


        //--------------------Start adding and croping image -----------------//
        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContracts) {
            it?.let { uri ->
                iv_image_acl.setImageURI(uri)

                val inputStream: InputStream? = contentResolver.openInputStream(uri)

            }
        }

        iv_image_acl.setOnClickListener {
            cropActivityResultLauncher.launch(null)
        }


        //------Add image and lesson name to sqlDatabse------//
        btn_save_acl.setOnClickListener {

            val stringName = et_lesson_name_acl.text.toString()
            val ImagerRes = imageViewToByte(iv_image_acl)

            val db = funActivity.sqlDb(applicationContext)

            //>>>>-----Get all lesson's names from sql to arrayList ----------//
            val column = funActivity.getColumnNames(db, "sort_lesson_name", 5)

            val contentValue = ContentValues()
            val contentValue2 = ContentValues()

            if (et_lesson_name_acl.length() > 0) {

                //>>>>---if activity has id -> editing, else -> create-----//
                if (!column.contains(stringName)) {

                    if (intent.hasExtra("EXTRA_ID")) {

                        //>>>>----New an image or a lesson name -----//
                        contentValue.put(myTable.SORT_LESSON_NAME, stringName)
                        contentValue.put(myTable.IMAGE_RESOURCE, ImagerRes)

                        //>>>>----Put changes-----//
                        db.update(
                            myTable.TABLE_NAME, contentValue, BaseColumns._ID + "=?",
                            arrayOf(intent.getStringExtra("EXTRA_ID"))
                        )

                        //>>>>--Important = add lessonName to row signifying words-----//
                        contentValue2.put(myTable.LESSON_NAME, stringName)

                        //>>>>----Put changes-----//
                        db.update(
                            myTable.TABLE_NAME, contentValue2, myTable.LESSON_NAME + "=?",
                            arrayOf(intent.getStringExtra("EXTRA_NAME3"))
                        )

                        //>>>---Back to MainActivty------//
                        onBackPressed()


                    } else {
                        //>>>>----Create lesson put image and name to SqlDatabse------//
                        contentValue.put(myTable.IMAGE_RESOURCE, ImagerRes)
                        contentValue.put(myTable.SORT_LESSON_NAME, stringName)
                        db.insertOrThrow(myTable.TABLE_NAME, null, contentValue)

                        //>>>----Back to MainActivity-----//
                        onBackPressed()
                    }

                } else {
                    funActivity.delayToast(R.string.you_have_lesson_with_this_name, this)
                    //Toast.makeText(this, R.string.input_name_lesson, Toast.LENGTH_SHORT).show()
                }

            } else {
                et_lesson_name_acl.error = getString(R.string.invalid_input)

            }

        }

    }

    //--Compress bitmap to byte Array to save image in sqlDatabase--//
    private fun imageViewToByte(image: ImageView): ByteArray {
        val bitmap: Bitmap = image.drawable.toBitmap()
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 20, stream)
        val byteArray: ByteArray = stream.toByteArray()
        return byteArray

    }

    //----If user is editing show old image and lesson name-----//
    private fun getNameAndImage() {

        //---Edit sqlDatabse----//
        val db = funActivity.sqlDb(applicationContext)

        if (intent.hasExtra("EXTRA_ID")) {

            //>>>>----Select specific by id the image and the lesson name-----//
            val id = intent.getStringExtra("EXTRA_ID")
            val selectQuery = "SELECT * FROM LESSON_TABLE WHERE _id = $id"

            val cursor: Cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                val name = cursor.getString(5)
                et_lesson_name_acl.setText(name)
                val image = cursor.getBlob(4)
                val bitmap: Bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
                iv_image_acl.setImageBitmap(bitmap)

            }
            cursor.close()

        }


    }


}