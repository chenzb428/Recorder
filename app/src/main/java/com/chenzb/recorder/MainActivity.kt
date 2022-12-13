package com.chenzb.recorder

import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.chenzb.recorder.callback.RecorderCallback
import com.chenzb.recorder.data.enum.RecorderFormat
import com.chenzb.recorder.databinding.ActivityMainBinding
import com.chenzb.recorder.utils.TimeUtils
import java.io.File
import java.text.DateFormat
import java.text.Format
import java.time.format.DateTimeFormatter

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
        Toast.makeText(this, "开始录音.....", Toast.LENGTH_SHORT).show()
    }

    override fun onStopRecord(file: File?) {
        binding.recordTimeTv.text = TimeUtils.formatHourMinSec(0L)
        Toast.makeText(this, "录音结束，文件 -> " + file?.absolutePath, Toast.LENGTH_SHORT).show()
    }

    override fun onRecordingProgress(progress: Long, amplitude: Int) {
        runOnUiThread {
            binding.recordTimeTv.text = TimeUtils.formatHourMinSec(progress)
        }
    }

    private fun checkRecorderPermission(): Boolean {
        val noGranted = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        return noGranted.isEmpty()
    }
}