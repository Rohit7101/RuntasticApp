package com.rohit.runtasticapp.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converters {

    @TypeConverter
    fun convertBitmapToBytesArray(bitmap:Bitmap):ByteArray{
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream)
        return outputStream.toByteArray()
    }

    fun convertByteArrayToBitmap(byteArray: ByteArray):Bitmap{
        return BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
    }
}