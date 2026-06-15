package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class SpeechManager(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Try to set Indian Hindi/Urdu pronunciation locale for Roman Urdu compatibility.
            // Falls back to US English if unavailable.
            val result = tts?.setLanguage(Locale("hi", "IN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.US)
            }
            isInitialized = true
        } else {
            Log.e("SpeechManager", "TTS Initialization failed!")
        }
    }

    fun speak(text: String) {
        if (!isInitialized) {
            Log.e("SpeechManager", "TTS not fully initialized yet.")
            return
        }
        val cleanText = cleanEmojisAndFormatting(text)
        tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "HuzoorSpeechId")
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }

    /**
     * Filters emojis and formatting characters so the speech synthesis sounds completely polished.
     */
    private fun cleanEmojisAndFormatting(text: String): String {
        // Regex to remove common emojis and special characters
        val emojiRegex = "[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+|[\\u2600-\\u27BF]".toRegex()
        var cleaned = text.replace(emojiRegex, "")
        
        // Remove markdown formatting like bold signs (**) and code blocks (```)
        cleaned = cleaned.replace("**", "")
        cleaned = cleaned.replace("`", "")
        
        // Trim double spaces or newlines
        return cleaned.trim()
    }
}
