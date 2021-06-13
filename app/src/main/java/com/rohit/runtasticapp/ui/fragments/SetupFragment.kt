package com.rohit.runtasticapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rohit.runtasticapp.R
import com.rohit.runtasticapp.ui.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_setup.*

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_setup) {
    private val viewmodel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpClickListener()
    }

    private fun setUpClickListener(){

        tvContinue.setOnClickListener {
           // if(checkValidation()) {
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            //}
        }
    }

    private fun checkValidation():Boolean{
        if(et_name.text.toString().isEmpty()){
            il_name.error = "Please enter name"
            return false
        }
        else if(et_weight.text.toString().isEmpty()){
            il_name.isErrorEnabled = false
            il_weight.error = "Please enter weight"
            return false
        }

        else if(et_weight.text.toString().toInt() <1 ||  et_weight.text.toString().toInt() > 100){
            il_weight.error = "Please enter correct weight"
            return false
        }

        else {
            il_weight.isErrorEnabled = false
            return true
        }
    }

}