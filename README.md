## Android 录音工具
基于MediaRecorder封装的Android录音库，目前只有m4a录制格式，后续有时间会增加其他录制格式

## 引入依赖

在项目根目录的 build.gradle 添加仓库

```groovy
allprojects {
    repositories {
        // ...
        mavenCentral()
    }
}
```

在项目模块的 build.gradle 中的dependencies里添加依赖
```groovy
    // 必须
    implementation 'io.github.chenzb428:recorder-base:1.0.0'
    // 以下可以按需依赖
    implementation 'io.github.chenzb428:recorder-m4a:1.0.0'
```

## 权限声明

在 AndroidManifest.xml 中需要添加如下

```
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

## 使用说明

Kotlin:
```
private var recorderManager: RecorderManager? = null
    
recorderManager = RecorderManager.Builder()
    // 设置录音录制格式
    .setPresenter(M4aRecorderPresenter.create(
        folderPath = 自定义保存文件夹，默认保存在Music/recorder目录下
        fileName = 自定义保存文件名称，默认名称为record)
    )
        // 设置声道数
        .setAudioChannels(2)
        // 设置采样率
        .setAudioSamplingRate(48000)
        // 设置比特率（支持m4a）
        .setAudioEncodingBitRate(256000)
        // 设置录音回调
        .setRecorderCallback(this)
        .build()
            
// 控制 开始、暂停、继续、停止、取消录制
recorderManager?.startRecording()
recorderManager?.pauseRecording()
recorderManager?.resumeRecording()
recorderManager?.stopRecording()
recorderManager?.cancelRecording()

// 录音回调
override fun onStartRecord() {
    // 录音开始回调
}

override fun onPauseRecord() {
    // 录音暂停回调
}

override fun onResumeRecord() {
    // 录音继续回调
}

override fun onStopRecord(file: File?) {
    // 录音停止回调
}

override fun onCancelRecord() {
    // 录音取消回调
}

override fun onRecordingProgress(progress: Long, amplitude: Int) {
    // 录音进度、振幅回调
}
```



## 版本更新记录

### recorder-base
#### v1.0.0 (2022-12-17)
 - 首个版本发布


### recorder-m4a
#### v1.0.0 (2022-12-17)
 - 首个版本发布