package com.example.flows

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MainActivity : AppCompatActivity() {

    lateinit var countdownTimeInput: EditText
    lateinit var countdownTimeDisplay: TextView
    lateinit var countDownStart: Button
    lateinit var stopCountDown: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val TAG: String = "MainActivity"

        countdownTimeInput = findViewById(R.id.editTextNumber)
        countdownTimeDisplay = findViewById(R.id.textView)
        countDownStart = findViewById(R.id.startCountdown)
        stopCountDown = findViewById(R.id.stopCountdown)

        var start: Job? = null

        countDownStart.setOnClickListener {
            start = GlobalScope.launch(Dispatchers.Unconfined) {
                var timeCountdown: Int
                withContext(Dispatchers.Main)
                {
                    timeCountdown = Integer.parseInt(countdownTimeInput.text.toString())
                }
                countdown(timeCountdown)
                    .onStart {
                        withContext(Dispatchers.Main)
                        {
                            Log.d(TAG,timeCountdown.toString())
                            countdownTimeDisplay.text = timeCountdown.toString()
                        }
                    }
                    .onCompletion {
                        withContext(Dispatchers.Main)
                        {
                            Toast.makeText(this@MainActivity,"Completed countdown of ${timeCountdown} seconds",Toast.LENGTH_SHORT).show()
                        }
                    }
                    .collect{
                        withContext(Dispatchers.Main)
                        {
                            Log.d(TAG,it.toString())
                            countdownTimeDisplay.text = it.toString()
                        }
                    }
            }
        }

        stopCountDown.setOnClickListener {
            start?.cancel()
            countdownTimeDisplay.text = "0"
        }
    }

    fun countdown(time: Int): Flow<Int>{
        return flow {
            for (i in time-1 downTo 0 )
            {
                delay(1000)
                emit(i)
            }
        }
    }
}