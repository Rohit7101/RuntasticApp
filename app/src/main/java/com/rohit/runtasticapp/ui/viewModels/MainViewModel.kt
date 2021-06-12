package com.rohit.runtasticapp.ui.viewModels

import androidx.lifecycle.ViewModel
import com.rohit.runtasticapp.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor( val mainRepository: MainRepository):ViewModel() {


}