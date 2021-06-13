package com.rohit.runtasticapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.rohit.runtasticapp.R
import com.rohit.runtasticapp.ui.viewModels.MainViewModel
import com.rohit.runtasticapp.ui.viewModels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint


class SettingsFragment : Fragment(R.layout.fragment_settings_fagment) {

    private val viewmodel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}