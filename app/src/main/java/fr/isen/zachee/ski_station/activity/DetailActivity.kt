package fr.isen.zachee.ski_station.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.isen.zachee.ski_station.dataclass.NameSlopes
import fr.isen.zachee.ski_station.database.SkiDatabase
import fr.isen.zachee.ski_station.ui.theme.SkiStationTheme

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val selectedLift = intent.getStringExtra("selected_Lift")

        setContent {
            SkiStationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (selectedLift != null) {
                        //Greeting(selectedLift)
                        DisplayLifts2(selectedLift)
                    }

                }
            }
        }
        Log.d("lifeCycle", "Detail Activity - OnCreate")
    }

    override fun onPause() {
        Log.d("lifeCycle", "Detail Activity - OnPause")
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        Log.d("lifeCycle", "Detail Activity - OnResume")
    }

    override fun onDestroy() {
        Log.d("lifeCycle", "Detail Activity - onDestroy")
        super.onDestroy()
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


@Composable
fun GetSlopesForSelectedLift(selectedLift: String, slopesDetails: SnapshotStateList<NameSlopes>) {
    SkiDatabase.database.getReference("lifts/$selectedLift/theSlopes")
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val slopesNames = snapshot.children.mapNotNull { child ->
                    child.getValue(NameSlopes::class.java)
                }
                slopesDetails.clear()
                slopesDetails.addAll(slopesNames)

               /* slopesNames.forEach { slopeName ->
                    SkiDatabase.database.getReference("slopes/$slopeName")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(slopeSnapshot: DataSnapshot) {
                                val status = slopeSnapshot.child("status").getValue(Boolean::class.java) ?: false
                                val slopeDetail = NameSlopes(slopeName.name, status)
                                slopesDetails.add(slopeDetail)

                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("databaseError", error.toString())
                            }

                        })
                }*/


            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("databaseError", error.toString())
            }
        })
}


@Composable
fun DisplayLifts2(selectedLiftName: String) {
    val context = LocalContext.current
    val slopes = remember { mutableStateListOf<NameSlopes>() }
    GetSlopesForSelectedLift(selectedLiftName, slopes )

    LazyColumn {
        items(items = slopes, key = { it.name }) { slopes ->
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()

            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = slopes.name,
                       // text = "${slopes.name} - Status: ${if (slopes.status) "Open" else "Closed"}" ,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )

                }
            }
        }
    }
}





