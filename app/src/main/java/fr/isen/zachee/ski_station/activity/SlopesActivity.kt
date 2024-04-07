package fr.isen.zachee.ski_station.activity

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.zachee.ski_station.db_services.CommentServiceSlop
import fr.isen.zachee.ski_station.R
import fr.isen.zachee.ski_station.database.SkiDatabase
import fr.isen.zachee.ski_station.dataclass.CommentSlops
import fr.isen.zachee.ski_station.dataclass.Slope
import fr.isen.zachee.ski_station.ui.theme.SkiStationTheme
import java.util.Calendar
import java.util.Locale

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
    val commentService = CommentServiceSlop(database = FirebaseDatabase.getInstance())
    GetSlopesData(slopes)
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    var selectedSlopId by remember { mutableStateOf("") }

    val colorOrder = mapOf("green" to 1, "blue" to 2, "red" to 3, "black" to 4)
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
                    Spacer(Modifier.weight(1f))
                    Icon(
                        painter = painterResource(id = R.drawable.icon_comment0),
                        contentDescription = "Comment Icon",
                        modifier = Modifier
                            .weight(0.3f)
                            .clickable {
                                selectedSlopId = slope.name
                                setShowDialog(true)
                            }
                    )
                }
            }
        }
    }
    if (showDialog) {
        CommentDialogSlop(
            slopID = selectedSlopId,
            commentService = commentService,
            onDismiss = { setShowDialog(false) }
        )
    }
}

@Composable
fun CommentDialogSlop(slopID: String, onDismiss: () -> Unit, commentService: CommentServiceSlop) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) // Format de date et heure
    val currentDateAndTime = Calendar.getInstance().time
    val dateString = dateFormat.format(currentDateAndTime)
    val userEmailID = currentUser?.email ?: ""


    var showTextField by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    var textError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Commentaire de la montée $slopID") },
        text = {
            Column {
                if (showTextField) {
                    TextField(
                        value = commentText,
                        onValueChange = {
                            commentText = it
                            textError = it.isBlank() // Update error state based on text content
                        },
                        isError = textError,
                        label = { Text("Votre commentaire") }
                    )
                    if (textError) {
                        Text("Le champ de commentaire ne peut pas être vide.", color = Color.Red)
                    }
                    Button(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                // Logique pour envoyer le commentaire, par exemple :
                                commentService.addComment(
                                    CommentSlops(userID = userEmailID, slopID = slopID, date = dateString, text = commentText),
                                    onSuccess = {
                                        // Handle success, clear the text field and hide it
                                        showTextField = false
                                        commentText = ""
                                        onDismiss() // Optionally dismiss the dialog
                                    },
                                    onFailure = { error ->
                                        // Handle failure
                                    }
                                )
                            } else {
                                textError = true
                            }
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Envoyer")
                    }
                }else{
                    DisplayCommentSlop(slopID = slopID)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    showTextField = true // Show the text field when "+" is clicked
                }
            ) {
                Text("+")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Quitter")
            }
        }
    )
}

@Composable
fun DisplayCommentSlop(slopID: String) {
    val comments = remember { mutableStateListOf<CommentSlops>() }
    val context = LocalContext.current
    val commentService = remember { CommentServiceSlop(FirebaseDatabase.getInstance()) }
    LaunchedEffect(slopID) {
        commentService.getCommentsForSlop(slopID,
            onSuccess = { retrievedComments ->
                comments.clear()
                comments.addAll(retrievedComments)
            },
            onFailure = { error ->
                Toast.makeText(context, "Erreur: $error", Toast.LENGTH_LONG).show()
            }
        )
    }

    if (comments.isEmpty()) {
        Text("Aucun commentaire")
    } else {
        LazyColumn {
            items(comments) { comment ->
                CommentItemslop(comment = comment)
            }
        }
    }
}

@Composable
fun CommentItemslop(comment: CommentSlops) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = "Date: ${comment.date}", style = MaterialTheme.typography.bodySmall)
        Text(text = "Email: ${comment.userID}", style = MaterialTheme.typography.bodySmall)
        Text(text = "Commentaire: ${comment.text}", style = MaterialTheme.typography.bodySmall)
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