package fr.isen.zachee.ski_station.dataclass

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
