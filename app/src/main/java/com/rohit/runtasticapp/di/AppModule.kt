package com.rohit.runtasticapp.di

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.rohit.runtasticapp.db.RunningDatabase
import com.rohit.runtasticapp.utils.Constants.KEY_FIRST_TIME_TOGGLE
import com.rohit.runtasticapp.utils.Constants.KEY_NAME
import com.rohit.runtasticapp.utils.Constants.KEY_WEIGHT
import com.rohit.runtasticapp.utils.Constants.RUNNING_DATABASE_NAME
import com.rohit.runtasticapp.utils.Constants.SHARED_PREFERENCES_NAME
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
    fun providesRunDao(db:RunningDatabase) = db.getRunDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app:Context): SharedPreferences{
       return app.getSharedPreferences(SHARED_PREFERENCES_NAME,MODE_PRIVATE)

    }

    @Singleton
    @Provides
    fun provideName(sharedPreferences: SharedPreferences):String{
      return sharedPreferences.getString(KEY_NAME,"")?:""
    }

    @Singleton
    @Provides
    fun provideWeight(sharedPreferences: SharedPreferences):Float{
        return sharedPreferences.getFloat(KEY_WEIGHT,8f)
    }

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPreferences: SharedPreferences):Boolean{
        return sharedPreferences.getBoolean(KEY_FIRST_TIME_TOGGLE,true)
    }
}

