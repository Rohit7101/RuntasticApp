package com.rohit.runtasticapp.ui.viewModels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohit.runtasticapp.db.Run
import com.rohit.runtasticapp.repositories.MainRepository
import com.rohit.runtasticapp.utils.SortTypes
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.logging.LogManager
import java.util.logging.Logger
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val mainRepository: MainRepository) : ViewModel() {


    private val runsSortByDate = mainRepository.getAllRunsSortedByDate()
    private val runsSortByDistance = mainRepository.getAllRunsSortedByDistance()
    private val runsSortByCaloriesBurned = mainRepository.getTotalCaloriesBurned()
    private val runsSortByTimeInMillies = mainRepository.getAllRunsSortedByTimeInMillies()
    private val runsSortByAverageSpeed = mainRepository.getAllRunsSortedByAvgSpeed()

    val mediatorRunsLiveData = MediatorLiveData<List<Run>>()

    var sortType = SortTypes.DATE

    init {
        Timber.d("entered MainviewModel init")

        mediatorRunsLiveData.addSource(runsSortByDate) { result ->
            if (sortType == SortTypes.DATE) {
                result?.let { mediatorRunsLiveData.value = it }
            }
        }
        mediatorRunsLiveData.addSource(runsSortByDistance) { result ->
            if (sortType == SortTypes.DISTANCE) {
                result?.let { mediatorRunsLiveData.value = it }
            }
        }
        mediatorRunsLiveData.addSource(runsSortByCaloriesBurned) { result ->
            if (sortType == SortTypes.CALORIES_BURNED) {
                result?.let { mediatorRunsLiveData.value = it }
            }
        }
        mediatorRunsLiveData.addSource(runsSortByTimeInMillies) { result ->
            if (sortType == SortTypes.RUNNING_TIME) {
                result?.let { mediatorRunsLiveData.value = it }
            }
        }
        mediatorRunsLiveData.addSource(runsSortByAverageSpeed) { result ->
            if (sortType == SortTypes.AVG_SPEED) {
                result?.let { mediatorRunsLiveData.value = it }
            }
        }

    }

    fun sortRuns(sortType: SortTypes) {
        when (sortType) {
            SortTypes.DATE -> runsSortByDate.value?.let { mediatorRunsLiveData.value = it }
            SortTypes.RUNNING_TIME -> runsSortByTimeInMillies.value?.let { mediatorRunsLiveData.value = it }
            SortTypes.AVG_SPEED -> runsSortByAverageSpeed.value?.let { mediatorRunsLiveData.value = it }
            SortTypes.DISTANCE -> runsSortByDistance.value?.let { mediatorRunsLiveData.value = it }
            SortTypes.CALORIES_BURNED -> runsSortByCaloriesBurned.value?.let { mediatorRunsLiveData.value = it }
        }.also {
            this.sortType = sortType
        }


    }

    fun insertRun(run: Run) {
        viewModelScope.launch {
            mainRepository.inserRun(run)
        }
    }

}