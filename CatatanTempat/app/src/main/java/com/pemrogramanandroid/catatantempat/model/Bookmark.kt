package com.pemrogramanandroid.catatantempat.model

import android.content.Context
import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pemrogramanandroid.catatantempat.util.imageUtil

@Entity
class Bookmark {

    @PrimaryKey(autoGenerate = true) var id: Long? = null
    var placeId: String? = null
    var name: String = ""
    var address: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var phone: String = ""
    var notes: String = ""

    fun setImage(image: Bitmap, context: Context){
        id?.let {
            imageUtil.saveBitmapToFile(context,image, generateImageFileName(it))
        }
    }
    //statik variabel
    companion object{
        fun generateImageFileName(id : Long): String{
            return "Bookmark$id"
        }
    }
}