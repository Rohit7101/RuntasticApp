package com.rohit.runtasticapp.repositories

import com.rohit.runtasticapp.db.IRunDao
import com.rohit.runtasticapp.db.Run
import javax.inject.Inject

class MainRepository @Inject constructor(private val runDao: IRunDao) {

    suspend fun inserRun(run:Run) = runDao.insertRun(run)
    suspend fun deleteRun(run:Run) = runDao.deleteRun(run)
    fun getAllRunsSortedByDate() = runDao.getAllRunsSortedByDate()
    fun getAllRunsSortedByAvgSpeed() = runDao.getAllRunsSortedByAvgSpeed()
    fun getAllRunsSortedByDistance() = runDao.getAllRunsSortedByDistance()
    fun getTotalTimeInMillies() = runDao.getTotalTimeInMillies()
    fun getTotalCaloriesBurned() = runDao.getAllRunsSortedByCaloriesBurned()
    fun getTotalDistance() = runDao.getTotalDistance()
    fun getTotalAvgSpeed() = runDao.getTotalAvgSpeed()
}