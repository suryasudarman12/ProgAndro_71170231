package com.pemrogramanandroid.catatantempat.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object imageUtil {

    fun saveBitmapToFile(context: Context, bitmap: Bitmap, filename: String){
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG,100, stream)
        val byteArray = stream.toByteArray()

        saveBytesToFile(context, byteArray, filename)

    }
    private fun saveBytesToFile(context: Context, byteArray: ByteArray, filename: String){
        val outputStream: FileOutputStream
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
            outputStream.write(byteArray)
            outputStream.close()
        }catch (e:Exception){

            e.printStackTrace()
        }
    }

    fun LoadBitmapFromfile(context: Context, filename: String): Bitmap?{
        val filePath = File(context.filesDir, filename).absolutePath
        return BitmapFactory.decodeFile(filePath)
    }
}