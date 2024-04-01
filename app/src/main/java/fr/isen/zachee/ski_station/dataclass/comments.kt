package fr.isen.zachee.ski_station.dataclass

import android.icu.text.SimpleDateFormat
import com.google.firebase.auth.FirebaseUser

data class Comments(
    val userID: String? = "",
    val date: String? = "",
    val liftID: String? = "",
    val text: String? =""
)
