package fr.isen.zachee.ski_station

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.isen.zachee.ski_station.ui.theme.SkiStationTheme

class LiftsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkiStationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DisplayLifts()
                }
            }
        }
        Log.d("lifeCycle", "Lift Activity - OnCreate")
    }

    override fun onPause() {
        Log.d("lifeCycle", "Lift Activity - OnPause")
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        Log.d("lifeCycle", "Lift Activity - OnResume")
    }

    override fun onDestroy() {
        Log.d("lifeCycle", "Lift Activity - onDestroy")
        super.onDestroy()
    }

}

@Composable
fun GetLiftsData(lifts: SnapshotStateList<Lift>) {
    SkiDatabase.database.getReference("lifts")
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val _lifts = snapshot.children.mapNotNull { it.getValue(Lift::class.java) }
                lifts.addAll(_lifts)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("databaseError", error.toString())
            }
        })
}

@Composable
fun DisplayLifts() {
    val lifts = remember { mutableStateListOf<Lift>() }
    GetLiftsData(lifts)

    LazyColumn {
        items(lifts.toList()) {
            Text(it.name)
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    SkiStationTheme {
        DisplayLifts()
    }
}