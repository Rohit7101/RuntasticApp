package com.rohit.runtasticapp.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getService
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.rohit.runtasticapp.R
import com.rohit.runtasticapp.ui.HomeActivity
import com.rohit.runtasticapp.utils.Constants.ACTION_PAUSE_SERVICE
import com.rohit.runtasticapp.utils.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.rohit.runtasticapp.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.rohit.runtasticapp.utils.Constants.ACTION_STOP_SERVICE
import com.rohit.runtasticapp.utils.Constants.FASTEST_LOCATION_UPDATE_INTERVAL
import com.rohit.runtasticapp.utils.Constants.LOCATION_UPDATE_INTERVAL
import com.rohit.runtasticapp.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.rohit.runtasticapp.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.rohit.runtasticapp.utils.Constants.NOTIFICATION_ID
import com.rohit.runtasticapp.utils.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : LifecycleService(){

    var isFirstRun = true
    var serviceKilled = false

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder:NotificationCompat.Builder

    lateinit var currentNotificationBuilder:NotificationCompat.Builder

    private var timeRunInSeconds = MutableLiveData<Long>()
    companion object{
        var timeRunInMilliseconds = MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<MutableList<MutableList<LatLng>>>()
    }

    private fun postInitialValues(){
        isTracking.value = false
        pathPoints.value = mutableListOf()
        timeRunInMilliseconds.postValue(0L)
        timeRunInSeconds.postValue(0L)
    }

   private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimeStamp = 0L

    private fun startTimer(){
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!){
                // time differnce between now and run started
                lapTime = System.currentTimeMillis() - timeStarted

                // posting new laptime
                timeRunInMilliseconds.postValue(timeRun+lapTime)
                if(timeRunInMilliseconds.value!! >= lastSecondTimeStamp+1000L){
                    timeRunInSeconds.postValue(timeRunInSeconds.value!!+1)
                    lastSecondTimeStamp+=1000L
                }
            }
            delay(50L)
        }
        timeRun += lapTime
    }

    private fun pauseService(){
    isTracking.postValue(false)
        isTimerEnabled = false
    }


    override fun onCreate() {
        super.onCreate()
        currentNotificationBuilder = baseNotificationBuilder
        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        isTracking.observe(this, Observer {
            updateLocationTracking(it)
            updateNotificationTrackingSystem(it)
        })
    }



    private fun updateNotificationTrackingSystem(isTracking: Boolean){
        val notificationActionText = if(isTracking)"pause" else "Resume"
        val pendingIntent = if(isTracking){
            val pauseIntent = Intent(this,TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService( this,1,pauseIntent, FLAG_UPDATE_CURRENT)
        }else{
           val resumeIntent = Intent(this,TrackingService::class.java).apply {
                    action = ACTION_START_OR_RESUME_SERVICE
                }
            PendingIntent.getService(this,2,resumeIntent, FLAG_UPDATE_CURRENT)
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        currentNotificationBuilder.javaClass.getDeclaredField("mAction").apply {

            isAccessible = true
            set(currentNotificationBuilder,ArrayList<NotificationCompat.Action>())
        }

        if(!serviceKilled){
            currentNotificationBuilder = baseNotificationBuilder.addAction(
                    R.drawable.ic_pause_black_24dp,notificationActionText,pendingIntent)
            notificationManager.notify(NOTIFICATION_ID,currentNotificationBuilder.build())

        }

    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking :Boolean){
        if(isTracking){
            if(TrackingUtility.hasLocationPermissions(this)){
                val request = LocationRequest().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_UPDATE_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY

                }

                fusedLocationProviderClient.requestLocationUpdates(request,locationCallback,
                        Looper.getMainLooper())
            }
        }else{
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult?) {
                super.onLocationResult(result)
                if(isTracking.value!!){
                    result?.locations?.let { locations ->
                        for(location in locations){
                            addPathPoint(location)
                        }
                    }
                }
            }
        }

    private fun addPathPoint(location:Location?){

        location?.let {
            val pos = LatLng(location.latitude,location.longitude)
            pathPoints?.value?.apply {
                this.last().add(pos)
                pathPoints.postValue(this)

            }
        }
    }

    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))



    private fun killService(){
        serviceKilled = true
        isFirstRun = true
        pauseService()
        postInitialValues()
        stopForeground(true)
        stopSelf()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                ACTION_START_OR_RESUME_SERVICE ->{
                    Timber.d("Start service")
                    if(isFirstRun){
                        startForegroundService()
                        isFirstRun = false
                    }else{
                        Timber.d("resume service")
                        startTimer()
                    }
                }
                ACTION_STOP_SERVICE ->{
                    Timber.d("Stop service")
                    killService()

                }
                ACTION_PAUSE_SERVICE ->{
                    Timber.d("pause service")
                    pauseService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)

    }




    private fun startForegroundService(){
        startTimer()
        isTracking.postValue(true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        startForeground(NOTIFICATION_ID,baseNotificationBuilder.build())
        timeRunInSeconds.observe(this, Observer {
            if(!serviceKilled){
                val notification = currentNotificationBuilder.setContentText(
                        TrackingUtility.getFormattedStopwatchTime(it * 1000L))
                notificationManager.notify(NOTIFICATION_ID, notification.build())
            }

        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}