package fr.isen.zachee.ski_station

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.zachee.ski_station.ui.theme.SkiStationTheme
import java.io.Serializable

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
        .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val _lifts = snapshot.children.mapNotNull {
                    val lift = it.getValue(Lift::class.java)
                    val slopes = it.child("theSlopes").children.mapNotNull { it.getValue(NameSlopes::class.java) }
                    lift?.listslopes = slopes

                    return@mapNotNull lift
                }
                lifts.removeAll { true }
                lifts.addAll(_lifts)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("databaseError", error.toString())
            }
        })
}

@Composable
fun DisplayLifts() {
    val context = LocalContext.current
    val lifts = remember { mutableStateListOf<Lift>() }
    GetLiftsData(lifts)

    LazyColumn {
        items(items = lifts, key = { it.name }) { lift ->
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(context,DetailActivity::class.java)
                        intent.putExtra("selected_Lift", lift.name )
                        context.startActivity(intent)
                    }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = lift.name,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.weight(1f)) // This will push the name to the left and status to the right
                    Text(
                        if (lift.status ?: false) "✅" else "❌",
                        color = if (lift.status ?: false) Color.Green else Color.Red,
                        modifier = Modifier
                            .weight(1f)
                            .clickable{
                                toggleLiftStatus(lift.name, !lift.status)
                            }

                    )
                }
            }
        }
    }
}







fun toggleLiftStatus(liftName: String, newStatus: Boolean) {
    // Obtenir une référence à votre base de données Firebase
    val databaseReference = FirebaseDatabase.getInstance().getReference("lifts")

    // Mettre à jour le statut de la piste spécifique
    databaseReference.child(liftName).child("status").setValue(newStatus)
        .addOnSuccessListener {
            // Gestion de succès
            Log.d("UpdateStatus", "Status updated successfully for slope: $liftName")
        }
        .addOnFailureListener {
            // Gestion d'erreur
            Log.e("UpdateStatus", "Failed to update status for slope: $liftName", it)
        }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    SkiStationTheme {
        DisplayLifts()
    }
}