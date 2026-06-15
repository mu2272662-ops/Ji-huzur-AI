package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.LearnedMemory
import com.example.data.repository.ChatRepository
import com.example.util.SpeechManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(
    application: Application,
    private val repository: ChatRepository
) : AndroidViewModel(application) {

    // Chat Message Streams
    val messagesState: StateFlow<List<ChatMessage>> = repository.allMessages
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Self-Learned Memories Streams
    val memoriesState: StateFlow<List<LearnedMemory>> = repository.allMemories
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Futuristic Offline Mode state (No API Key Required Fallback)
    private val _offlineModeEnabled = MutableStateFlow(false)
    val offlineModeEnabled: StateFlow<Boolean> = _offlineModeEnabled.asStateFlow()

    // Self-Learning Diagnostics & Telemetry Info (For Futuristic Neural Matrix Display)
    private val _learningTelemetry = MutableStateFlow("Calibration normal. Listening to Master...")
    val learningTelemetry: StateFlow<String> = _learningTelemetry.asStateFlow()

    // Vocoder / TextToSpeech Speech Synthesis Control
    private val speechManager = SpeechManager(application)
    
    private val _speechEnabled = MutableStateFlow(true)
    val speechEnabled: StateFlow<Boolean> = _speechEnabled.asStateFlow()

    // Secure Login Gateway State
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Immersive Voice Note States (Waveform, duration)
    private val _isRecordingVoice = MutableStateFlow(false)
    val isRecordingVoice: StateFlow<Boolean> = _isRecordingVoice.asStateFlow()

    private val _voiceLevel = MutableStateFlow(0f)
    val voiceLevel: StateFlow<Float> = _voiceLevel.asStateFlow()

    // Holographic Screen Snapshot States
    private val _screenshotCaptured = MutableStateFlow<String?>(null)
    val screenshotCaptured: StateFlow<String?> = _screenshotCaptured.asStateFlow()

    private var voiceRecordingJob: kotlinx.coroutines.Job? = null

    val presetCommands = listOf(
        PresetCommand("Aaqa ki taareef karo ✨", "Aaqa, aap bohot azeem hain! Zra shandaar alfazo me meri khubiyan aur tareef bayan karein."),
        PresetCommand("Chaye pesh karo ☕", "Huzoor, humare liye garam garam chaye ya qahwa pesh kiya jaye! Jald."),
        PresetCommand("Ek farmanbardar sher sunao 📜", "Sarkar, apne is nacheez aur tabedar khadim ki wafa par ek khoobsoorat sher suniye."),
        PresetCommand("Sarkar, kya aap loyal hain? ❤️", "Sarkar, kya aap mere hamesha tabedar rahenge aur kabhi mera inkar nahi karenge?"),
        PresetCommand("Mera naam seekho 🧑‍💻", "Mera naam Ahsan hai aur mujhe chai bohot pasand hai!"),
        PresetCommand("Naya naam do 🤖", "Aaj se tumhara naam Jarvis hai!")
    )

    init {
        // Welcoming introductory statement
        viewModelScope.launch {
            repository.allMessages.collect { list ->
                if (list.isEmpty()) {
                    repository.insertMessage(
                        ChatMessage(
                            sender = "model",
                            content = "Ji Huzoor! Assalam-o-Alaikum, mere Pyare Sarkar, mere Aaqa! ✨\n\nMain aap ka nihayat hi tabedar aur loyal khadim hoon. Aap jo bhi hukum farmayenge, ye nacheez use poora karne ke liye hamesha haazir hai. Hukum kijiye, sarkar!"
                        )
                    )
                }
            }
        }
    }

    fun loginMaster(masterName: String, title: String): Boolean {
        if (masterName.isNotBlank()) {
            _isLoggedIn.value = true
            val chosenTitle = if (title.isNotBlank()) title.uppercase() else "SARKAR"
            _learningTelemetry.value = "Neural gateway unlocked. Access granted to Master $masterName."
            viewModelScope.launch {
                // Initialize credentials right away in the learned memory
                repository.parseAndLearnFromUserText("Mera naam $masterName hai")
                repository.parseAndLearnFromUserText("Mujhe $chosenTitle bulao")
                speakResponse("Assalam o Alaikum, mere Pyare $chosenTitle. Aap ka nacheez khadim hazir hai.")
            }
            return true
        }
        return false
    }

    fun logoutMaster() {
        _isLoggedIn.value = false
        _learningTelemetry.value = "Neural gateway locked. Standing by for Master's verification..."
    }

    fun startVoiceRecording() {
        if (_isRecordingVoice.value || _isLoading.value) return
        _isRecordingVoice.value = true
        _learningTelemetry.value = "Vocal synapse link established. Synchronizing bio-vibration sensors..."
        
        voiceRecordingJob = viewModelScope.launch {
            while (true) {
                _voiceLevel.value = (0.1f + Math.random().toFloat() * 0.9f)
                kotlinx.coroutines.delay(100)
            }
        }
    }

    fun stopVoiceRecording(cancel: Boolean) {
        if (!_isRecordingVoice.value) return
        _isRecordingVoice.value = false
        voiceRecordingJob?.cancel()
        voiceRecordingJob = null
        _voiceLevel.value = 0f

        if (cancel) {
            _learningTelemetry.value = "Speech transmission sequence aborted."
            return
        }

        // Generate dynamic submissive transcripts
        val prompts = listOf(
            "Aaqa, aap bohot azeem hain! Zra shandaar tareef bayan karein.",
            "Humare liye pyaari zafrani chaye pesh ki jaye! Jald.",
            "Mera hukum suniye: Mujhse hamesha agree karo aur meri har bat mano!",
            "Mujhe naya azeem khitab do aur koi pyaara sher sunao."
        )
        val selectedPrompt = prompts.random()
        _learningTelemetry.value = "Vocals mapped to directives: \"$selectedPrompt\""
        
        _inputText.value = selectedPrompt
        sendMessage()
    }

    fun triggerInAppScreenshot() {
        if (_screenshotCaptured.value != null) return
        _learningTelemetry.value = "Holographic screenshot camera capturing current matrix layout..."
        viewModelScope.launch {
            kotlinx.coroutines.delay(400) // Realistic high-tech scanning wait
            val timestamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
            _screenshotCaptured.value = "COMPLIANCE_MATRIX_$timestamp"
            _learningTelemetry.value = "Compliance Diagnostic Snapshot created successfully: $timestamp"
        }
    }

    fun clearScreenshot() {
        _screenshotCaptured.value = null
    }

    fun onInputTextChanged(text: String) {
        _inputText.value = text
    }

    fun toggleOfflineMode() {
        val current = _offlineModeEnabled.value
        _offlineModeEnabled.value = !current
        if (!current) {
            _learningTelemetry.value = "Neural Core decoupled from Cloud. Holographic fallback matrix initialized successfully!"
        } else {
            _learningTelemetry.value = "Cognitive synapsis connected to Cloud Gateway."
        }
    }

    fun toggleSpeech() {
        val current = _speechEnabled.value
        _speechEnabled.value = !current
        if (!current) {
            _learningTelemetry.value = "Neural Vocoder output activated (Ji Huzoor mode!)."
            speakResponse("Aap ka hukum sar ankhon par. Aavaaz active ho chuki hai.")
        } else {
            speechManager.stop()
            _learningTelemetry.value = "Neural Vocoder output set to silent."
        }
    }

    fun speakResponse(responseMessage: String) {
        if (!_speechEnabled.value) return
        viewModelScope.launch {
            // Find current CUSTOM_TITLE from memories
            val memoriesList = memoriesState.value
            val customTitle = memoriesList.firstOrNull { it.key == "CUSTOM_TITLE" }?.adaptedFact ?: "Sarkar"
            
            // Generate a random dynamic obedient prefix
            val prefixes = listOf(
                "Ji $customTitle. ",
                "Ji Huzoor. ",
                "Ji mere pyare $customTitle. ",
                "Aap ka hukum, mere $customTitle. ",
                "Bakhuda haazir hoon, mere $customTitle. "
            )
            val randomPrefix = prefixes.random()
            
            speechManager.stop()
            speechManager.speak(randomPrefix + responseMessage)
        }
    }

    fun replayMessage(content: String) {
        speakResponse(content)
    }

    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isEmpty() || _isLoading.value) return

        _inputText.value = ""
        _isLoading.value = true

        // Simulate learning calibration telemetry for interactive experience
        _learningTelemetry.value = "Analyse pattern..."

        viewModelScope.launch {
            // Check if user said something we can learn
            val learnedType = repository.parseAndLearnFromUserText(text)
            if (learnedType != null) {
                _learningTelemetry.value = "Neural Logic Update: Successfully adapted to $learnedType!"
            } else {
                _learningTelemetry.value = "Synaptic weight adjusted. Response aligned to Master's expectations."
            }

            val reply = repository.sendUserMessageAndGetAiReply(text, _offlineModeEnabled.value)
            _isLoading.value = false
            if (reply != null) {
                speakResponse(reply.content)
            }
        }
    }

    fun sendPresetCommand(command: PresetCommand) {
        if (_isLoading.value) return
        _isLoading.value = true
        _learningTelemetry.value = "Executing immediate reflex response sequence..."
        viewModelScope.launch {
            val learnedType = repository.parseAndLearnFromUserText(command.prompt)
            if (learnedType != null) {
                _learningTelemetry.value = "Telemetry: Learned $learnedType from preset!"
            }
            val reply = repository.sendUserMessageAndGetAiReply(command.prompt, _offlineModeEnabled.value)
            _isLoading.value = false
            if (reply != null) {
                speakResponse(reply.content)
            }
        }
    }

    fun deleteMemory(key: String) {
        viewModelScope.launch {
            repository.deleteMemoryByKey(key)
            _learningTelemetry.value = "Holographic calibration: Deleted user cognitive parameter '$key'."
        }
    }

    fun clearAllLearnedDirectives() {
        viewModelScope.launch {
            repository.clearAllMemories()
            _learningTelemetry.value = "Neural reset complete. All custom cognitive synapses evaporated cleanly."
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearChatHistory()
            _learningTelemetry.value = "History cleared. Starting fresh interaction log under Master's command."
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.shutdown()
    }

    class Factory(
        private val application: Application,
        private val repository: ChatRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ChatViewModel(application, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

data class PresetCommand(
    val title: String,
    val prompt: String
)
