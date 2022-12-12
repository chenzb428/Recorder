package com.chenzb.recorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.chenzb.recorder.data.enum.RecorderFormat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var recorderManager = RecorderManager.Builder()
            .setMode(RecorderFormat.M4A)
            .build()

        findViewById<AppCompatButton>(R.id.record_start_bt).setOnClickListener {
            recorderManager.startRecording(this)
        }

        findViewById<AppCompatButton>(R.id.record_stop_bt).setOnClickListener {
            recorderManager.stopRecording()
        }
    }
}