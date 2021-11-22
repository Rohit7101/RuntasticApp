package com.rohit.runtasticapp.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.rohit.runtasticapp.R
import com.rohit.runtasticapp.ui.HomeActivity
import com.rohit.runtasticapp.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun providesFusedLocationProviderClient(@ApplicationContext app: Context) =
            FusedLocationProviderClient(app)

    @ServiceScoped
    @Provides
    fun providesMainActivityPendingIntent(@ApplicationContext app:Context)=
             PendingIntent.getActivity(app,0,
                    Intent(app, HomeActivity::class.java).also {
                        it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
                    }, PendingIntent.FLAG_UPDATE_CURRENT)



        @Provides
        @ServiceScoped
    fun providesBaseNotificationBuilder(@ApplicationContext app:Context,
    pendingIntent: PendingIntent)=
            NotificationCompat.Builder(app, Constants.NOTIFICATION_CHANNEL_ID)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
                    .setContentTitle("Runtastic App")
                    .setContentText("00:00:00")
                    .setContentIntent(pendingIntent)

}