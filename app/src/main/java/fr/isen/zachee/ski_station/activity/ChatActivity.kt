package fr.isen.zachee.ski_station.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import fr.isen.zachee.ski_station.db_services.ChatService
import fr.isen.zachee.ski_station.dataclass.ChatMessage
import fr.isen.zachee.ski_station.ui.theme.ui.theme.SkiStationTheme

class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkiStationTheme {
                ChatScreenDialog(onNavigateBack = {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP // Assurez-vous de ne pas empiler les activités
                    startActivity(intent)
                    finish() // Terminez l'activité actuelle si vous ne voulez pas que l'utilisateur y retourne avec le bouton de retour
                })
            }
        }
    }
}
@Composable
fun ChatScreenDialog(onNavigateBack: () -> Unit) {
    val showDialog = remember { mutableStateOf(true) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "En Développement") },
            text = { Text(text = "Cette partie de l'application est encore en développement. Veuillez revenir plus tard.") },
            confirmButton = {
                Button(onClick = {
                    showDialog.value = false
                    onNavigateBack() // Invoquez le rappel ici
                }) {
                    Text("OK")
                }
            }
        )
    }
}


@Composable
fun ChatScreen(chatRoomId: String) {
    val user = FirebaseAuth.getInstance().currentUser
    val chatService = remember { ChatService() }
    val messages = remember { mutableStateListOf<ChatMessage>() }

    // Réception des messages
    LaunchedEffect(chatRoomId) {
        chatService.receiveMessages(chatRoomId) { message ->
            messages.add(message)
        }
    }

    var textState by remember { mutableStateOf("") }

    Column {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { message ->
                // Vérifiez si le message a été envoyé par l'utilisateur connecté
                MessageRow(message = message, isOwnMessage = message.senderId == user?.email)
            }
        }
        Row {
            TextField(
                value = textState,
                onValueChange = { textState = it },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                if (textState.isNotBlank()) {
                    user?.email?.let { email ->
                        chatService.sendMessage(chatRoomId, email, textState)
                        textState = ""
                    }
                }
            }) {
                Text("Envoyer")
            }
        }
    }
}


@Composable
fun MessageRow(message: ChatMessage, isOwnMessage: Boolean) {
    Row(
        horizontalArrangement = if (isOwnMessage) Arrangement.End else Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = if (isOwnMessage) Color.LightGray else Color.White),
            modifier = Modifier.padding(4.dp)
        ) {
            Text(text = message.message, modifier = Modifier.padding(8.dp))
        }
    }
}

