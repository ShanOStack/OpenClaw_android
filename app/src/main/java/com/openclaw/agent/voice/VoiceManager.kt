package com.openclaw.agent.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class VoiceManager(
    private val context: Context,
    private val onVoiceResult: (String) -> Unit,
    private val onStateChanged: (VoiceState) -> Unit
) : TextToSpeech.OnInitListener {

    enum class VoiceState {
        IDLE, LISTENING, PROCESSING, SPEAKING
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    var isTtsReady: Boolean = false
        private set

    // Customizable voice attributes
    var currentPitch: Float = 1.0f
        set(value) {
            field = value
            textToSpeech?.setPitch(value)
        }

    var currentSpeed: Float = 1.0f
        set(value) {
            field = value
            textToSpeech?.setSpeechRate(value)
        }

    init {
        initTts()
        initRecognizer()
    }

    private fun initTts() {
        textToSpeech = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(Locale("hi", "IN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                textToSpeech?.setLanguage(Locale.getDefault())
            }
            textToSpeech?.setPitch(currentPitch)
            textToSpeech?.setSpeechRate(currentSpeed)
            isTtsReady = true
        }
    }

    private fun initRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        onStateChanged(VoiceState.LISTENING)
                    }

                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {
                        onStateChanged(VoiceState.PROCESSING)
                    }

                    override fun onError(error: Int) {
                        Log.w("VoiceManager", "Speech error code: $error")
                        onStateChanged(VoiceState.IDLE)
                    }

                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val spokenText = matches?.firstOrNull() ?: ""
                        onStateChanged(VoiceState.IDLE)
                        if (spokenText.isNotBlank()) {
                            onVoiceResult(spokenText)
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        }
    }

    fun startListening() {
        if (speechRecognizer == null) {
            initRecognizer()
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            // Offline recognition preference tag
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
        }
        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e("VoiceManager", "Error starting listening", e)
            onStateChanged(VoiceState.IDLE)
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        onStateChanged(VoiceState.IDLE)
    }

    fun speak(text: String, onDone: (() -> Unit)? = null) {
        if (isTtsReady && textToSpeech != null) {
            onStateChanged(VoiceState.SPEAKING)
            val utteranceId = "OpenClawTTS_${System.currentTimeMillis()}"
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            // Switch state back after short duration
            onDone?.invoke()
        }
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
    }
}
