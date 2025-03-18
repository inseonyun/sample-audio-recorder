package com.sample.audio.recorder.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import com.sample.audio.recorder.model.STTState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import java.io.File
import javax.inject.Inject

class GroqSTT @Inject constructor(
    @ApplicationContext private val context: Context,
    state: Channel<STTState>,
    private val apiCallback: suspend (File) -> Unit
) : VoiceRecorder(state) {
    private var mediaRecorder: MediaRecorder? = null

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
        TODO("Not yet implemented")
    }

    override fun cancel() {
        TODO("Not yet implemented")
    }

    companion object {
        private const val FILE_NAME = "AUDIO_RECORD"
        private const val FILE_TYPE = ".wav"
    }
}
