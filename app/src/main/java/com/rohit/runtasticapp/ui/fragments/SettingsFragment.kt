package com.rohit.runtasticapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.rohit.runtasticapp.R
import com.rohit.runtasticapp.ui.viewModels.MainViewModel
import com.rohit.runtasticapp.ui.viewModels.StatisticsViewModel
import com.rohit.runtasticapp.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_settings_fagment.*
import kotlinx.android.synthetic.main.fragment_setup.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings_fagment) {

    private val viewmodel: MainViewModel by viewModels()
    private val conditionA = false
    private val conditionB = true

        @Inject
        lateinit var sharedPref:SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFieldsFromSharedPreferences()
        setClickListener()
    }

    private fun setClickListener() {
        btnApplyChanges.setOnClickListener {


            val success = applyChangesToSharedPreferences()
            if(success){
                Snackbar.make(requireView(),"saved changes",Snackbar.LENGTH_LONG).show()
            }else{
                Snackbar.make(requireView(),"Please fill out all fields",Snackbar.LENGTH_LONG).show()

            }

          //  checkFunction()


        }
    }

    private fun checkFunction() {
        if(!conditionA){
            Timber.d("i am 1")
            return
        }
        Timber.d("i am 2")

        Toast.makeText(requireContext(), "1 am 1", Toast.LENGTH_SHORT).show()
        if(!conditionB){
            return
        }
        Toast.makeText(requireContext(), "1 am 2", Toast.LENGTH_SHORT).show()

    }


    private fun loadFieldsFromSharedPreferences(){
      val name =  sharedPref.getString(Constants.KEY_NAME,"")
      val weight =  sharedPref.getFloat(Constants.KEY_WEIGHT,0f)
        Timber.d("shared name$name")
        Timber.d("shared weight"+weight.toString())
        etName.setText(name)
        if(weight>1){etWeight.setText(weight.toString())}
    }
    private fun applyChangesToSharedPreferences():Boolean{
        val name = tilName.editText?.text.toString().trim()
        val weight = etWeight.text.toString()
        if(name.isEmpty()||weight.isEmpty()){
            return false
        }
        sharedPref.edit().putString(Constants.KEY_NAME,name)
        .putFloat(Constants.KEY_WEIGHT,weight.toFloat()).apply()
        return true
    }
}