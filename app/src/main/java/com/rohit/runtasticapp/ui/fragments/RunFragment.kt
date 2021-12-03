package com.rohit.runtasticapp.ui.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rohit.runtasticapp.R
import com.rohit.runtasticapp.adapters.RunAdapter
import com.rohit.runtasticapp.ui.viewModels.MainViewModel
import com.rohit.runtasticapp.utils.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.rohit.runtasticapp.utils.SortTypes
import com.rohit.runtasticapp.utils.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_run.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.jar.Manifest

@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run), EasyPermissions.PermissionCallbacks {

    private val viewmodel: MainViewModel by viewModels()
    private lateinit var runAdapter: RunAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()
        setUpRecyclerView()
        when(viewmodel.sortType) {
            SortTypes.DATE -> spFilter.setSelection(0)
            SortTypes.RUNNING_TIME -> spFilter.setSelection(1)
            SortTypes.DISTANCE -> spFilter.setSelection(2)
            SortTypes.AVG_SPEED -> spFilter.setSelection(3)
            SortTypes.CALORIES_BURNED -> spFilter.setSelection(4)
        }

        spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                when(pos) {
                    0 -> viewmodel.sortRuns(SortTypes.DATE)
                    1 -> viewmodel.sortRuns(SortTypes.RUNNING_TIME)
                    2 -> viewmodel.sortRuns(SortTypes.DISTANCE)
                    3 -> viewmodel.sortRuns(SortTypes.AVG_SPEED)
                    4 -> viewmodel.sortRuns(SortTypes.CALORIES_BURNED)
                }
            }
        }

        notifyUIOnRunsCompletion()
        setClickListeners()

    }

    private fun notifyUIOnRunsCompletion() {
        viewmodel.mediatorRunsLiveData.observe(viewLifecycleOwner, Observer {
            runAdapter.submitList(it)
        })
    }

    private fun setClickListeners() {
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment2_to_trackingFragment)
        }
    }

    private fun setUpRecyclerView(){
        rvRuns.apply {
            runAdapter = RunAdapter()
            adapter = runAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun requestPermissions() {
        if (TrackingUtility.hasLocationPermissions(requireContext())) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(this, "You need to accept location permissions to use this app.",
                    REQUEST_CODE_LOCATION_PERMISSION, android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
        } else {
            EasyPermissions.requestPermissions(this, "You need to accept location permissions to use this app.",
                    REQUEST_CODE_LOCATION_PERMISSION, android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }
}