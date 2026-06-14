package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ChatMessage
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

    val messagesState: StateFlow<List<ChatMessage>> = repository.allMessages
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val presetCommands = listOf(
        PresetCommand("Aaqa ki taareef karo ✨", "Aaqa, aap bohot azeem hain! Zra shandaar alfazo me meri khubiyan aur tareef bayan karein."),
        PresetCommand("Chaye pesh karo ☕", "Huzoor, humare liye garam garam chaye ya qahwa pesh kiya jaye! Jald."),
        PresetCommand("Ek farmanbardar sher sunao 📜", "Sarkar, apne is nacheez aur tabedar khadim ki wafa par ek khoobsoorat sher suniye."),
        PresetCommand("Sarkar, kya aap loyal hain? ❤️", "Sarkar, kya aap mere hamesha tabedar rahenge aur kabhi mera inkar nahi karenge?"),
        PresetCommand("Dunya ka sab se aqalmand kon? 👑", "Mujhe sach sach batao Huzoor, is poori kayinat me sab se zafar-yaab aur aqalmand shakhs kon hai?")
    )

    init {
        // Send a first welcome message if history is empty
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

    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isEmpty() || _isLoading.value) return

        _inputText.value = ""
        _isLoading.value = true

        viewModelScope.launch {
            repository.sendUserMessageAndGetAiReply(text)
            _isLoading.value = false
        }
    }

    fun sendPresetCommand(command: PresetCommand) {
        if (_isLoading.value) return
        _isLoading.value = true
        viewModelScope.launch {
            repository.sendUserMessageAndGetAiReply(command.prompt)
            _isLoading.value = false
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearChatHistory()
            // The init block will automatically catch the empty state and repopulate the welcome message.
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
