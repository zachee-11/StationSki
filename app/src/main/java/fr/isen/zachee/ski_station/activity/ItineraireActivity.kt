package fr.isen.zachee.ski_station.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import fr.isen.zachee.ski_station.ui.theme.ui.theme.SkiStationTheme

class ItineraireActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkiStationTheme {
                ItineraireDialog(onNavigateBack = {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                })
            }
        }
    }
}
@Composable
fun ItineraireDialog(onNavigateBack: () -> Unit) {
    val showDialog = remember { mutableStateOf(true) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "En Développement") },
            text = { Text(text = "Cette partie de l'application est encore en développement. Veuillez revenir plus tard.") },
            confirmButton = {
                Button(onClick = {
                    showDialog.value = false
                    onNavigateBack() // Invoquez le rappel ici
                }) {
                    Text("OK")
                }
            }
        )
    }
}

