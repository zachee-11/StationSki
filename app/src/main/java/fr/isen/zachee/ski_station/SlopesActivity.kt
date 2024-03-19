package fr.isen.zachee.ski_station

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
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.isen.zachee.ski_station.ui.theme.SkiStationTheme

class SlopesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkiStationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DisplaySlopes()
                }
            }
        }
        Log.d("lifeCycle", "Slope Activity - OnCreate")
    }

    override fun onPause() {
        Log.d("lifeCycle", "Slope Activity - OnPause")
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        Log.d("lifeCycle", "Slope Activity - OnResume")
    }

    override fun onDestroy() {
        Log.d("lifeCycle", "Slope Activity - onDestroy")
        super.onDestroy()
    }

}


fun toggleSlopeStatus(slopeName: String, newStatus: Boolean) {
    // Obtenir une référence à votre base de données Firebase
    val databaseReference = FirebaseDatabase.getInstance().getReference("slopes")

    // Mettre à jour le statut de la piste spécifique
    databaseReference.child(slopeName).child("status").setValue(newStatus)
        .addOnSuccessListener {
            // Gestion de succès
            Log.d("UpdateStatus", "Status updated successfully for slope: $slopeName")
        }
        .addOnFailureListener {
            // Gestion d'erreur
            Log.e("UpdateStatus", "Failed to update status for slope: $slopeName", it)
        }
}




@Composable
fun DisplaySlopes() {
    val slopes = remember { mutableStateListOf<Slope>() }
    GetSlopesData(slopes)

    // Définir l'ordre de tri pour les couleurs
    val colorOrder = mapOf("green" to 1, "blue" to 2, "red" to 3, "black" to 4)

    // Trier les pistes par couleur en utilisant l'ordre défini
    val sortedSlopes = slopes.sortedBy { colorOrder[it.color] ?: 5 }


    LazyColumn {
        //items(sortedSlopes) {slope ->
        items(items = slopes.sortedBy { slope ->
            mapOf("green" to 1, "blue" to 2, "red" to 3, "black" to 4)[slope.color] ?: 5
        }, key = { it.name }) { slope ->
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
                        text = slope.name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.weight(1f)) // This pushes the name to the left and status to the right
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(color = stringToColor(slope.color), shape = CircleShape)
                    )
                    Spacer(Modifier.weight(1f)) // This will keep the color in the middle
                    Text(
                        text = if (slope.status) "✅" else "❌",
                        color = if (slope.status) Color.Green else Color.Red,
                        modifier = Modifier
                            .clickable { toggleSlopeStatus(slope.name, !slope.status) }
                    )
                }
            }
        }
    }
}






fun stringToColor(colorString: String): Color {
    return when(colorString) {
        "red" -> Color.Red
        "blue" -> Color.Blue
        "green" -> Color.Green
        "black" -> Color.Black
        else -> Color.Gray // Couleur par défaut si la chaîne ne correspond pas
    }
}



@Composable
fun GetSlopesData(slopes: SnapshotStateList<Slope>) {
    SkiDatabase.database.getReference("slopes")
        .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val _slopes = snapshot.children.mapNotNull { it.getValue(Slope::class.java) }
                slopes.removeAll { true }
                slopes.addAll(_slopes)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("databaseError", error.toString())
            }
        })
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    SkiStationTheme {
        DisplaySlopes()
    }
}