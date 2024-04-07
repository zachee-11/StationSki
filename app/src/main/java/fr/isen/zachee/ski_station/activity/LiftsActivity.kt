package fr.isen.zachee.ski_station.activity

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import fr.isen.zachee.ski_station.db_services.CommentService
import fr.isen.zachee.ski_station.R
import fr.isen.zachee.ski_station.dataclass.Lift
import fr.isen.zachee.ski_station.dataclass.NameSlopes
import fr.isen.zachee.ski_station.database.SkiDatabase
import fr.isen.zachee.ski_station.dataclass.Comments
import fr.isen.zachee.ski_station.ui.theme.SkiStationTheme
import java.util.Calendar
import java.util.Locale

class LiftsActivity : ComponentActivity() {
    val commentService = CommentService(FirebaseDatabase.getInstance())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkiStationTheme {
                val commentService = CommentService(FirebaseDatabase.getInstance())
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
        val commentService = CommentService(database = FirebaseDatabase.getInstance())

        GetLiftsData(lifts)

        // État pour contrôler l'affichage de la boîte de dialogue
        val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
        // État pour stocker l'ID de la remontée sélectionnée
        var selectedLiftId by remember { mutableStateOf("") }

        LazyColumn {
            items(items = lifts, key = { it.name }) { lift ->
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(context, DetailActivity::class.java)
                            intent.putExtra("selected_Lift", lift.name)
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
                        Spacer(Modifier.weight(1f))
                        Text(
                            if (lift.status ?: false) "✅" else "❌",
                            color = if (lift.status ?: false) Color.Green else Color.Red,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    toggleLiftStatus(lift.name, !lift.status)
                                }
                        )
                        Spacer(Modifier.weight(1f))
                        Icon(
                            painter = painterResource(id = R.drawable.icon_comment0),
                            contentDescription = "Comment Icon",
                            modifier = Modifier
                                .weight(0.3f)
                                .clickable {
                                    selectedLiftId = lift.name
                                    setShowDialog(true)
                                }
                        )
                    }
                }
            }
        }

        // Affiche la boîte de dialogue lorsque showDialog est vrai
        if (showDialog) {
            CommentDialog(
                liftId = selectedLiftId,
                commentService = commentService, // Passer l'instance de CommentService
                onDismiss = { setShowDialog(false) }
            )
        }
    }



    @Composable
    fun CommentDialog(liftId: String, onDismiss: () -> Unit, commentService: CommentService) {
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
            title = { Text(text = "Commentaire de la montée $liftId") },
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
                                        Comments(userID = userEmailID, liftID = liftId, date = dateString, text = commentText),
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
                        DisplayComments(liftId= liftId)
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
    fun DisplayComments(liftId: String) {
        val comments = remember { mutableStateListOf<Comments>() }
        val context = LocalContext.current
        val commentService = remember { CommentService(FirebaseDatabase.getInstance()) }
        LaunchedEffect(liftId) {
            commentService.getCommentsForLift(liftId,
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
                    CommentItem(comment = comment)
                }
            }
        }
    }

    @Composable
    fun CommentItem(comment: Comments) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Date: ${comment.date}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Email: ${comment.userID}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Commentaire: ${comment.text}", style = MaterialTheme.typography.bodySmall)
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