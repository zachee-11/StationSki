package fr.isen.zachee.ski_station.db_services

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.zachee.ski_station.dataclass.ChatMessage

class ChatService {
    private val dbRef = FirebaseDatabase.getInstance().getReference("chat")

    fun sendMessage(chatRoomId: String, senderEmail: String, messageText: String) {
        val message = ChatMessage(
            id = dbRef.child(chatRoomId).push().key ?: "",
            senderId = senderEmail,
            message = messageText,
            timestamp = System.currentTimeMillis()
        )
        dbRef.child(chatRoomId).child(message.id).setValue(message)
    }

    fun receiveMessages(chatRoomId: String, onMessageReceived: (ChatMessage) -> Unit) {
        dbRef.child(chatRoomId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { child ->
                        val message = child.getValue(ChatMessage::class.java)
                        message?.let { onMessageReceived(it) }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ChatService", "Failed to read messages", error.toException())
                }
            })
    }
}