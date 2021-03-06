package com.rohit.runtasticapp.ui.fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.android.material.snackbar.Snackbar
import com.rohit.runtasticapp.R
import com.rohit.runtasticapp.db.Run
import com.rohit.runtasticapp.services.TrackingService
import com.rohit.runtasticapp.ui.viewModels.MainViewModel
import com.rohit.runtasticapp.utils.Constants
import com.rohit.runtasticapp.utils.Constants.ACTION_PAUSE_SERVICE
import com.rohit.runtasticapp.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.rohit.runtasticapp.utils.Constants.ACTION_STOP_SERVICE
import com.rohit.runtasticapp.utils.Constants.MAP_ZOOM
import com.rohit.runtasticapp.utils.Constants.POLYLINE_COLOR
import com.rohit.runtasticapp.utils.Constants.POLYLINE_WIDTH
import com.rohit.runtasticapp.utils.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*
import timber.log.Timber
import java.util.*
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private val viewmodel: MainViewModel by viewModels()
    private var map: GoogleMap? = null
    private var isTracking = false
    private var pathPoints = mutableListOf<MutableList<LatLng>>()
    private var curTImeInMillies = 0L
    private var menu: Menu?= null
    private var weight = 75f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupGoogleMap(savedInstanceState)
        setClickListeners()
        subscribeToObservers()

    }

    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
                updateTracking(it)
        })
        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            addLatestPolylines()
            moveCameraToUser()
        })

        TrackingService.timeRunInMilliseconds.observe(viewLifecycleOwner, Observer {
            curTImeInMillies = it
            val formattedTime = TrackingUtility.getFormattedStopwatchTime(curTImeInMillies,true)
            tvTimer.text = formattedTime
        })
    }



    private fun toggleRun(){
            if(isTracking){
                menu?.getItem(0)?.isVisible = true
                sendCommandToService(ACTION_PAUSE_SERVICE)
            }else{
                sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu,menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if(curTImeInMillies>0L){
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.miCancelTracking ->{
                showCancelTrackingDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCancelTrackingDialog(){
        val dialog = MaterialAlertDialogBuilder(requireContext(),R.style.AlertDialogTheme)
         .setTitle("Cancel the Run?")
                .setMessage("Are you sure to cancel the current run and delete all its data?")
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton("Yes"){_,_->
                    stopRun()
                }
                .setNegativeButton("No"){dialogInterface,_->
                    dialogInterface.cancel()
                }.create()
            dialog.show()
    }

    private fun stopRun() {
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    private fun updateTracking(isTracking:Boolean){
        Timber.d("observing tracking   "+isTracking)
        this.isTracking = isTracking
        if(!isTracking){
            btnToggleRun.text = "Start"
            btnFinishRun.visibility = View.VISIBLE
        }else{
            menu?.getItem(0)?.isVisible = true
            btnToggleRun.text = "Stop"
            btnFinishRun.visibility = View.GONE
        }

    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {

            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(pathPoints.last().last(),
            MAP_ZOOM))
        }
    }


    private fun zoomToSeeWholeTrack(){
        val bounds = LatLngBounds.builder()
        for(polyline in pathPoints){
            for(position in polyline){
                bounds.include(position)
            }
        }

        map?.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                        bounds.build(),
                        mapView.width,
                        mapView.height,
                        (mapView.height * 0.05f).toInt()
                )
        )
    }

    private fun endRunAndSaveToDB(){
        map?.snapshot { bitmap ->
        var distanceInMetres = 0
            for(polyline in pathPoints){
                distanceInMetres += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed = round((distanceInMetres/1000f) /(curTImeInMillies/1000f/60/60)*10)/10f
            val dateTimeStamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMetres/1000f)*weight).toInt()
            val run = Run(bitmap,dateTimeStamp,avgSpeed,distanceInMetres,curTImeInMillies,caloriesBurned)
            viewmodel.insertRun(run)
            Snackbar.make(requireActivity().findViewById(R.id.rootView),"Run saved succesfully",
            Snackbar.LENGTH_LONG).show()
            stopRun()
        }
    }


    private fun addAllPolylines() {
        for (polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                    .color(POLYLINE_COLOR)
                    .width(POLYLINE_WIDTH)
                    .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolylines() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polyLineOptions = PolylineOptions()
                    .color(POLYLINE_COLOR)
                    .width(POLYLINE_WIDTH)
                    .add(preLastLatLng)
                    .add(lastLatLng)
            map?.addPolyline(polyLineOptions)
        }
    }

    private fun setupGoogleMap(savedInstanceState: Bundle?) {
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            map = it
            addAllPolylines()
        }
    }

    private fun setClickListeners() {
        btnToggleRun.setOnClickListener {
            toggleRun()
        }
        btnFinishRun.setOnClickListener {
            zoomToSeeWholeTrack()
            endRunAndSaveToDB()
        }
    }

    private fun sendCommandToService(action: String) {

        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }


}