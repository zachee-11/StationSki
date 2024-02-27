package fr.isen.zachee.ski_station

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.zachee.ski_station.SkiDatabase.Companion.database
import fr.isen.zachee.ski_station.ui.theme.SkiStationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        

        setContent {
            SkiStationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        DisplaySlopes()
                        DisplayLifts()
                    }
                }
            }
        }
    }
}

@Composable
fun GetSlopesData(slopes: SnapshotStateList<Slope>) {
    SkiDatabase.database.getReference("slopes")
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val _slopes = snapshot.children.mapNotNull { it.getValue(Slope::class.java) }
                slopes.addAll(_slopes)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("databaseError", error.toString())
            }
        })
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
fun DisplaySlopes() {
    val slopes = remember { mutableStateListOf<Slope>() }
    GetSlopesData(slopes)

    LazyColumn {
        items(slopes.toList()) {
            Text(it.name)
        }
    }
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
fun GreetingPreview() {
    SkiStationTheme {
        DisplaySlopes()
    }
}


