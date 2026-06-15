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

            repository.sendUserMessageAndGetAiReply(text, _offlineModeEnabled.value)
            _isLoading.value = false
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
            repository.sendUserMessageAndGetAiReply(command.prompt, _offlineModeEnabled.value)
            _isLoading.value = false
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
