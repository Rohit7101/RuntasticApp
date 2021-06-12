package com.rohit.runtasticapp.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface IRunDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run:Run)

    @Delete
    suspend fun deleteRun(run:Run)

    @Query("SELECT * from running_table ORDER By timeStamp DESC")
     fun getAllRunsSortedByDate(): LiveData<List<Run>>

    @Query("SELECT * from running_table ORDER By caloriesBurned DESC")
     fun getAllRunsSortedByCaloriesBurned(): LiveData<List<Run>>

    @Query("SELECT * from running_table ORDER By avgSpeedInKMH DESC")
     fun getAllRunsSortedByAvgSpeed(): LiveData<List<Run>>

    @Query("SELECT * from running_table ORDER By distanceInMetres DESC")
     fun getAllRunsSortedByDistance(): LiveData<List<Run>>

    @Query("SELECT SUM(timeInMillies) FROM running_table")
     fun getTotalTimeInMillies():LiveData<Long>

    @Query("SELECT SUM(caloriesBurned) FROM running_table")
     fun getTotalCaloriesBurned():LiveData<Int>

    @Query("SELECT SUM(distanceInMetres) FROM running_table")
     fun getTotalDistance():LiveData<Long>

    @Query("SELECT SUM(timeInMillies) FROM running_table")
     fun getTotalAvgSpeed():LiveData<Float>

}
