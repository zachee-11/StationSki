package fr.isen.zachee.ski_station

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.isen.zachee.ski_station.dataClass.Lift
import fr.isen.zachee.ski_station.database.LinkApp
import fr.isen.zachee.ski_station.ui.theme.SkiStationTheme

class LiftActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkiStationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }
}
@Composable
fun GetAllLift(lifts: SnapshotStateList<Lift>) {
    LinkApp.database.getReference("lifts")
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val _lifts = snapshot.children.mapNotNull { it.getValue(Lift::class.java) }
                Log.d("database", lifts.toString())
                lifts.addAll(_lifts)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("dataBase", error.toString())
            }

        })
}
@Composable
fun Greeting() {
    val context = LocalContext.current
    val lifts = remember {
        mutableStateListOf<Lift>()
    }
    LazyColumn {
        items(lifts.toList()) {
            Text(it.name)
        }
    }
    GetAllLift(lifts)

}