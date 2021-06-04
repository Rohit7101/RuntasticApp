package com.rohit.runtasticapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    companion object{
        const val timeInMillies = 2000L
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        launchCoroutineForSplash()
        
    }

    private fun launchCoroutineForSplash(){
        CoroutineScope(Dispatchers.IO).launch {
            delay(timeInMillies)
            moveToHomeScreen()
        }
    }

    private fun moveToHomeScreen() {
        startActivity(Intent(this,HomeActivity::class.java))
        finish()
    }


}