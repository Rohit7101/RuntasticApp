package com.rohit.runtasticapp.utils

import android.content.Context
import android.location.Location
import android.os.Build
import com.google.android.gms.maps.model.LatLng
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit
import java.util.jar.Manifest

object TrackingUtility {

    fun hasLocationPermissions(context: Context) =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                EasyPermissions.hasPermissions(context, android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
            } else {
                EasyPermissions.hasPermissions(context, android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }


        fun getFormattedStopwatchTime(ms:Long,includeMillies:Boolean = false):String{
            var milliSeconds = ms
            val hours = TimeUnit.MILLISECONDS.toHours(milliSeconds)
            milliSeconds -= TimeUnit.HOURS.toMillis(hours)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(milliSeconds)
            milliSeconds -= TimeUnit.MINUTES.toMillis(minutes)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(milliSeconds)
            if(!includeMillies){
            return "${if(hours<10)"0" else ""}$hours:" +
                    "${if(minutes<10)"0" else ""}$minutes:"+
                    "${if(seconds<10)"0" else ""}$seconds"
            }
            milliSeconds -= TimeUnit.SECONDS.toMillis(seconds)
            milliSeconds/= 10

            return "${if(hours<10)"0" else ""}$hours:" +
                    "${if(minutes<10)"0" else ""}$minutes:"+
                    "${if(seconds<10)"0" else ""}$seconds"
                    "${if(milliSeconds<10)"0" else ""}$milliSeconds"

        }

    fun calculatePolylineLength(polyline : MutableList<LatLng>):Float{
        var distance = 0f
        for( i in 0..polyline.size-2){
            val pos1 = polyline[i]
            val pos2 = polyline[i+1]
            val result = FloatArray(1)
            Location.distanceBetween(
                    pos1.latitude,
                    pos1.longitude,
                    pos2.latitude,
                    pos2.longitude,result)

            distance += result[0]

        }
        return distance

    }

}