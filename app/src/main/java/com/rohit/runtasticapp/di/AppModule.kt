package com.rohit.runtasticapp.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.rohit.runtasticapp.db.RunningDatabase
import com.rohit.runtasticapp.utils.Constants.RUNNING_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.managers.ApplicationComponentManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRunningDatabase(@ApplicationContext context: Context)  =

        Room.databaseBuilder(context, RunningDatabase::class.java, RUNNING_DATABASE_NAME).build()


    @Singleton
    @Provides
    fun providesRunDao(db:RunningDatabase) =
         db.getRunDao()
    
}