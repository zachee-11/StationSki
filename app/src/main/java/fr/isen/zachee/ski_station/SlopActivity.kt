package fr.isen.zachee.ski_station

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.Modifier
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.isen.zachee.ski_station.dataClass.Slope
import fr.isen.zachee.ski_station.database.LinkApp


enum class SlopeColor {
    blue,
    red,
    green,
    black
}

class SlopActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
           /* val slopes = remember {
                mutableStateListOf<Slope>()
            }
            Greetingbutton(slopes)*/
           GreetingSlope()
        }
    }
}

@Composable
fun GetAllSlopes(slopes: SnapshotStateList<Slope>) {
    LinkApp.database.getReference("slopes")
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val _slopes = snapshot.children.mapNotNull { it.getValue(Slope::class.java) }
                Log.d("database", slopes.toString())
                slopes.addAll(_slopes)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("dataBase", error.toString())
            }
        })
}

@Composable
fun GreetingSlope() {
    val slopes = remember {
        mutableStateListOf<Slope>()
    }
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(stringResource(R.string.colonne_name_nom), modifier = Modifier.weight(1f))
            Text(stringResource(R.string.colonne_name_couleur), modifier = Modifier.weight(1f))
            Text(stringResource(R.string.colonne_name_etat), modifier = Modifier.weight(1f))
        }
        LazyColumn {
            items(slopes.toList()) { slope ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                color = when (slope.color) {
                                    SlopeColor.blue.toString() -> Color.Blue
                                    SlopeColor.red.toString() -> Color.Red
                                    SlopeColor.green.toString() -> Color.Green
                                    SlopeColor.black.toString() -> Color.Black
                                    else -> Color.Yellow
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(25.dp))
                    Text(slope.name ?: "", modifier = Modifier.weight(1f))
                    Text(slope.color ?: "", modifier = Modifier.weight(1f))
                    Text(
                        if (slope.status ?: false) "✅" else "❌",
                        color = if (slope.status ?: false) Color.Green else Color.Red,
                        modifier = Modifier.weight(1f)
                    )
                }

            }
        }
    }
    GetAllSlopes(slopes)

}



@Composable
fun Greetingbutton(slopes: SnapshotStateList<Slope>) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { slopes.clear(); slopes.addAll(slopes.filter { it.color == SlopeColor.blue.toString() }) }) {
                Text(text = "M_bleu")
            }
            Button(onClick = { slopes.clear(); slopes.addAll(slopes.filter { it.color == SlopeColor.red.toString() }) }) {
                Text(text = "M_rouge")
            }
            Button(onClick = { slopes.clear(); slopes.addAll(slopes.filter { it.color == SlopeColor.green.toString() }) }) {
                Text(text = "M_verte")
            }
            Button(onClick = { slopes.clear(); slopes.addAll(slopes.filter { it.color == SlopeColor.black.toString() }) }) {
                Text(text = "M_noir")
            }
        }
    }
}



