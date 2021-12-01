package com.rohit.runtasticapp.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohit.runtasticapp.db.Run
import com.rohit.runtasticapp.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor( val mainRepository: MainRepository):ViewModel() {

    fun insertRun(run: Run) {
        viewModelScope.launch {
            mainRepository.inserRun(run)
        }
    }

}