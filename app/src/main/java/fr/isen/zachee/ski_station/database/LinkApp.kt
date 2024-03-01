package fr.isen.zachee.ski_station.database

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LinkApp {
    companion object{
        val database = Firebase.database("https://station-ski-b2c83-default-rtdb.europe-west1.firebasedatabase.app/")
    }
}
