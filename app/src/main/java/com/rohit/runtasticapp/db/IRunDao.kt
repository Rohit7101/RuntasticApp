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
    suspend fun getAllRunsSortedByDate(): LiveData<List<Run>>

    @Query("SELECT * from running_table ORDER By caloriesBurned DESC")
    suspend fun getAllRunsSortedByCaloriesBurned(): LiveData<List<Run>>

    @Query("SELECT * from running_table ORDER By avgSpeedInKMH DESC")
    suspend fun getAllRunsSortedByAvgSpeed(): LiveData<List<Run>>

    @Query("SELECT * from running_table ORDER By distanceInMetres DESC")
    suspend fun getAllRunsSortedByDistance(): LiveData<List<Run>>

    @Query("SELECT SUM(timeInMillies) FROM running_table")
    suspend fun getTotalTimeInMillies():LiveData<Long>

    @Query("SELECT SUM(caloriesBurned) FROM running_table")
    suspend fun getTotalCaloriesBurned():LiveData<Int>

    @Query("SELECT SUM(distanceInMetres) FROM running_table")
    suspend fun getTotalDistance():LiveData<Long>

    @Query("SELECT SUM(timeInMillies) FROM running_table")
    suspend fun getTotalAvgSpeed():LiveData<Float>
}
