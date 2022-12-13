package com.chenzb.recorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.chenzb.recorder.callback.RecorderCallback
import com.chenzb.recorder.data.enum.RecorderFormat
import com.chenzb.recorder.databinding.ActivityMainBinding
import com.chenzb.recorder.presenter.impl.IRecorderPresenter
import java.io.File

class MainActivity : AppCompatActivity(), View.OnClickListener, RecorderCallback {

    private lateinit var binding: ActivityMainBinding

    private var recorderManager: RecorderManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recorderManager = RecorderManager.Builder()
            .setFormat(RecorderFormat.M4A)
            .build()

        binding.recordStartBt.setOnClickListener(this)
        binding.recordPausedBt.setOnClickListener(this)
        binding.recordResumeBt.setOnClickListener(this)
        binding.recordStopBt.setOnClickListener(this)

        recorderManager?.setRecorderCallback(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.record_start_bt -> recorderManager?.startRecording(this)

            R.id.record_paused_bt -> recorderManager?.pauseRecording()

            R.id.record_resume_bt -> recorderManager?.resumeRecording()

            R.id.record_stop_bt -> recorderManager?.stopRecording()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        recorderManager = null
    }

    override fun onStartRecord() {
        Toast.makeText(this, "开始录音.....", Toast.LENGTH_SHORT).show()
    }

    override fun onStopRecord(file: File?) {
        Toast.makeText(this, "录音结束，文件 -> " + file?.absolutePath, Toast.LENGTH_SHORT).show()
    }
}