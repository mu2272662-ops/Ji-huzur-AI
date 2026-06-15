package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.data.local.AppDatabase
import com.example.data.repository.ChatRepository
import com.example.ui.ChatScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ChatViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize local database and repository
        val database = AppDatabase.getDatabase(this)
        val repository = ChatRepository(database.chatMessageDao(), database.learnedMemoryDao())
        
        // Instantiate ViewModel
        val viewModel: ChatViewModel by viewModels {
            ChatViewModel.Factory(application, repository)
        }

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ChatScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

