package com.sample.audio.recorder.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import com.sample.audio.recorder.data.api.STTApi
import com.sample.audio.recorder.model.STTState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class GroqSTT @Inject constructor(
    @ApplicationContext private val context: Context,
    state: Channel<STTState>,
    private val sttApi: STTApi,
) : VoiceRecorder(state) {
    private var mediaRecorder: MediaRecorder? = null

    private var recordingJob: Job? = null
    private var silenceJob: Job? = null

    private var audioFile: File? = null

    @SuppressLint("NewApi")
    override suspend fun create() {
        try {
            audioFile = getOutputFile()

            mediaRecorder = MediaRecorder().apply {
                setAudioEncodingBitRate(AUDIO_BIT_RATE)
                setAudioSamplingRate(AUDIO_SAMPLE_RATE)
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFile?.absolutePath)
            }
            Log.i("VoiceRecorder", "MediaRecorder is Created")
        } catch (e: Exception) {
            Log.e("VoiceRecorder", "MediaRecorder create ERROR: ${e.message}")
            state.send(STTState.Error(e.message.toString()))
        }
    }

    private fun getOutputFile(): File = File(context.cacheDir, FILE_NAME + FILE_TYPE).apply {
        if (exists()) removeFile(this)
        createNewFile()
    }

    private fun removeFile(file: File? = audioFile) {
        file?.delete()
    }

    override suspend fun start() {
        try {
            mediaRecorder?.let {
                it.prepare()
                it.start()
                Log.i("VoiceRecorder", "MediaRecorder is Started")
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
        silenceJob?.cancel()

        silenceJob = CoroutineScope(Dispatchers.IO).launch {
            var lastAmplitude = getAmplitude()
            while (isActive) {
                delay(500L)
                val currentAmplitude = getAmplitude()
                if (currentAmplitude < SILENCE_AMPLITUDE_CONDITION && lastAmplitude < SILENCE_AMPLITUDE_CONDITION) {
                    var isSilence = true
                    for (i in 1..10) {
                        delay(TIME_AMPLITUDE_MEASUREMENT)

                        if (getAmplitude() > SILENCE_AMPLITUDE_CONDITION) {
                            isSilence = false
                            break
                        }
                    }

                    if (isSilence) {
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
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        recordingJob?.cancel()
        silenceJob?.cancel()
    }

    override suspend fun end() {
        mediaRecorder?.apply {
            stop()
            reset()
            release()
        }
        mediaRecorder = null
        recordingJob?.cancel()
        silenceJob?.cancel()

        Log.i("VoiceRecorder", "MediaRecorder is Ended")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sttResponse = sttApi.getGroqSTT(getMultipartBody(), MODEL_MULTIPART)

                if (sttResponse.text.isBlank()) {
                    state.send(STTState.TimeOut)
                } else {
                    state.send(STTState.Success(sttResponse.text))
                }
            } catch (e: Exception) {
                Log.e("VoiceRecorder", "STT ERROR: ${e.message}")
                state.send(STTState.Error(e.message.toString()))
            }
        }
    }

    private fun getMultipartBody(): MultipartBody.Part {
        val requestFile = audioFile!!.asRequestBody(FILE_MEDIA_TYPE)
        return MultipartBody.Part.createFormData(FILE_MULTIPART_NAME, audioFile!!.name, requestFile)
    }

    companion object {
        private const val AUDIO_SAMPLE_RATE = 16000
        private const val AUDIO_BIT_RATE = 12800

        private const val TIME_AMPLITUDE_MEASUREMENT = 100L
        private const val SILENCE_AMPLITUDE_CONDITION = 2000

        private const val FILE_NAME = "AUDIO_RECORD"
        private const val FILE_TYPE = ".wav"
        private val FILE_MEDIA_TYPE = "audio/wav".toMediaTypeOrNull()
        private const val FILE_MULTIPART_NAME = "file"

        private val MODEL_MULTIPART = MultipartBody.Part.createFormData("model", "whisper-large-v3-turbo")

        private const val MAX_RECORDING_TIME = 15000L
    }
}
