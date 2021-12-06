package com.rohit.runtasticapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.rohit.runtasticapp.R
import com.rohit.runtasticapp.ui.viewModels.MainViewModel
import com.rohit.runtasticapp.utils.Constants
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_setup.*
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_setup) {

    @Inject
    lateinit var sharedPref: SharedPreferences

    @set:Inject
    var isFirstAppOpen = true

    private val viewmodel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isFirstAppOpen) {
            val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.setupFragment, true).build()
            findNavController().navigate(R.id.action_setupFragment_to_runFragment,
            savedInstanceState, navOptions)
        }
        setUpClickListener()
    }


    private fun writePersonalDataToSharedPref(): Boolean {
            val name = et_name.text.toString()
            val weight = et_weight.text.toString()
            if(name.isEmpty() || weight.isEmpty()) {
                return false
            }
            sharedPref.edit().putString(Constants.KEY_NAME, name)
           .putFloat(Constants.KEY_WEIGHT, weight.toFloat())
            .putBoolean(Constants.KEY_FIRST_TIME_TOGGLE, false).apply()
            val toolbarText = "Lets go, $name!"
        return true
    }


    private fun setUpClickListener() {

        tvContinue.setOnClickListener {
            if (checkValidation()&& writePersonalDataToSharedPref()) {
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            }
        }
    }

    private fun checkValidation(): Boolean {
        if (et_name.text.toString().isEmpty()) {
            il_name.error = "Please enter name"
            return false
        } else if (et_weight.text.toString().isEmpty()) {
            il_name.isErrorEnabled = false
            il_weight.error = "Please enter weight"
            return false
        } else if (et_weight.text.toString().toInt() < 1 || et_weight.text.toString().toInt() > 150) {
            il_weight.error = "Please enter correct weight"
            return false
        } else {
            il_weight.isErrorEnabled = false
            return true
        }
    }

}