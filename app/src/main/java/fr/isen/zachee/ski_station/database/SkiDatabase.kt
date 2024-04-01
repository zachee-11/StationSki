package fr.isen.zachee.ski_station.database

import com.google.firebase.Firebase
import com.google.firebase.database.database

class SkiDatabase {
    companion object {
        val database= Firebase.database("https://station-ski-b2c83-default-rtdb.europe-west1.firebasedatabase.app/")
    }
}