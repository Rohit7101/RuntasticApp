package com.rohit.runtasticapp.ui.viewModels

import androidx.lifecycle.ViewModel
import com.rohit.runtasticapp.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor( val mainRepository: MainRepository):ViewModel() {

    val totalTimeRun = mainRepository.getTotalTimeInMillies()
    val totalDistance = mainRepository.getTotalDistance()
    val totalCaloriesBurned = mainRepository.getTotalCaloriesBurned()
    val totalAvgSpeed = mainRepository.getTotalAvgSpeed()

    val runsSortedByDate = mainRepository.getAllRunsSortedByDate()

}