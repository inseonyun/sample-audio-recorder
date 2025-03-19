package com.sample.audio.recorder.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import com.sample.audio.recorder.model.STTState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class GroqSTT @Inject constructor(
    @ApplicationContext private val context: Context,
    state: Channel<STTState>,
    private val apiCallback: suspend (File) -> Unit
) : VoiceRecorder(state) {
    private var mediaRecorder: MediaRecorder? = null

    private var recordingJob: Job? = null
    private var silenceJob: Job? = null

    @SuppressLint("NewApi")
    override suspend fun create() {
        try {
            mediaRecorder = MediaRecorder(context).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(getOutputFile())
            }
        } catch (e: Exception) {
            Log.e("VoiceRecorder", "MediaRecorder create ERROR: ${e.message}")
            state.send(STTState.Error(e.message.toString()))
        }
    }

    private fun getOutputFile(): File = File.createTempFile(FILE_NAME, FILE_TYPE, context.cacheDir)

    override suspend fun start() {
        try {
            mediaRecorder?.let {
                it.prepare()
                it.start()
            }
            recordingJob = CoroutineScope(Dispatchers.IO).launch {
                delay(MAX_RECORDING_TIME) // Recording to MAX TIME
                end()
            }
            detectSilence()
        } catch (e: Exception) {
            Log.e("VoiceRecorder", "MediaRecorder Start ERROR: ${e.message}")
            state.send(STTState.Error(e.message.toString()))
        }
    }

    private fun detectSilence() {
        silenceJob = CoroutineScope(Dispatchers.IO).launch {
            var lastAmplitude = getAmplitude()
            while (isActive) {
                delay(500)
                val currentAmplitude = getAmplitude()
                if (currentAmplitude < 2000 && lastAmplitude < 2000) {
                    delay(1000)
                    if (getAmplitude() < 2000) {
                        end()
                        break
                    }
                }
                lastAmplitude = currentAmplitude
            }
        }
    }

    private fun getAmplitude(): Int = mediaRecorder?.maxAmplitude ?: 0

    override fun cancel() {
        TODO("Not yet implemented")
    }

    companion object {
        private const val FILE_NAME = "AUDIO_RECORD"
        private const val FILE_TYPE = ".wav"

        private const val MAX_RECORDING_TIME = 15000L
    }
}
