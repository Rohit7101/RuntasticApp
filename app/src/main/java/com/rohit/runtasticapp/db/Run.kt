package com.rohit.runtasticapp.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_table")
class Run(
        var img: Bitmap? = null,
        var timeStamp: Long = 0L,
        var avgSpeedInKMH: Float = 0F,
        var distanceInMetres: Int = 0,
        var timeInMillies: Long = 0L,
        var caloriesBurned: Int = 0,
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}