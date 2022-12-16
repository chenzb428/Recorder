package com.chenzb.recorder

import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.chenzb.recorder.databinding.ActivityMainBinding
import com.chenzb.recorder.utils.TimeUtils
import com.chenzb.recorder_base.RecorderManager
import com.chenzb.recorder_base.callback.RecorderCallback
import com.chenzb.recorder_m4a.presenter.M4aRecorderPresenter
import java.io.File

class MainActivity : AppCompatActivity(), View.OnClickListener, RecorderCallback {

    companion object {

        val permissions = arrayOf(WRITE_EXTERNAL_STORAGE, RECORD_AUDIO)

        const val PERMISSION_REQUEST_CODE = 10001
    }

    private lateinit var binding: ActivityMainBinding

    private var recorderManager: RecorderManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recorderManager = RecorderManager.Builder()
            .setPresenter(M4aRecorderPresenter.create())
            .setAudioChannels(2)
            .setAudioSamplingRate(48000)
            .setAudioEncodingBitRate(256000)
            .setRecorderCallback(this)
            .build()

        binding.recordStartBt.setOnClickListener(this)
        binding.recordPausedBt.setOnClickListener(this)
        binding.recordResumeBt.setOnClickListener(this)
        binding.recordStopBt.setOnClickListener(this)
        binding.recordCancelBt.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.record_start_bt -> {
                if (checkRecorderPermission()) {
                    recorderManager?.startRecording(this)
                } else {
                    ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
                }
            }

            R.id.record_paused_bt -> recorderManager?.pauseRecording()

            R.id.record_resume_bt -> recorderManager?.resumeRecording()

            R.id.record_stop_bt -> recorderManager?.stopRecording()

            R.id.record_cancel_bt -> recorderManager?.cancelRecording()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        recorderManager = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            // 所有权限请求成功
            if (requestCode == PERMISSION_REQUEST_CODE) {
                recorderManager?.startRecording(this)
            }
        } else {
            // 存在拒绝的权限
            grantResults.forEachIndexed { index: Int, grantResult: Int ->
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[index])) {
                        // TODO 用户永久拒绝
                    } else {
                        // TODO 用户拒绝了.....
                    }
                }
            }
        }
    }

    override fun onStartRecord() {
        binding.recordStartBt.isEnabled = false
        binding.recordStopBt.isEnabled = true
        binding.recordCancelBt.isEnabled = true

        Toast.makeText(this, "开始录音.....", Toast.LENGTH_SHORT).show()
    }

    override fun onPauseRecord() {
        binding.recordResumeBt.setTextColor(Color.BLACK)
        binding.recordPausedBt.setTextColor(Color.RED)

    }

    override fun onResumeRecord() {
        binding.recordResumeBt.setTextColor(Color.RED)
        binding.recordPausedBt.setTextColor(Color.BLACK)
    }

    override fun onStopRecord(file: File?) {
        binding.recordAmplitudeTv.text = "amplitude: 0"
        binding.recordTimeTv.text = TimeUtils.formatHourMinSec(0L)
        binding.recordResumeBt.setTextColor(Color.BLACK)
        binding.recordPausedBt.setTextColor(Color.BLACK)
        binding.recordStartBt.isEnabled = true
        binding.recordStopBt.isEnabled = false
        binding.recordCancelBt.isEnabled = false

        Toast.makeText(this, "录音结束，文件 -> " + file?.absolutePath, Toast.LENGTH_SHORT).show()
    }

    override fun onCancelRecord() {
        binding.recordAmplitudeTv.text = "amplitude: 0"
        binding.recordTimeTv.text = TimeUtils.formatHourMinSec(0L)
        binding.recordResumeBt.setTextColor(Color.BLACK)
        binding.recordPausedBt.setTextColor(Color.BLACK)
        binding.recordStartBt.isEnabled = true
        binding.recordStopBt.isEnabled = false
        binding.recordCancelBt.isEnabled = false

        Toast.makeText(this, "取消录音", Toast.LENGTH_SHORT).show()
    }

    override fun onRecordingProgress(progress: Long, amplitude: Int) {
        runOnUiThread {
            binding.recordTimeTv.text = TimeUtils.formatHourMinSec(progress)
            binding.recordAmplitudeTv.text = "amplitude: $amplitude"
        }
    }

    private fun checkRecorderPermission(): Boolean {
        val noGranted = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        return noGranted.isEmpty()
    }
}