package fr.isen.zachee.ski_station.activity


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ResetPasswordScreen()
        }
    }
    @Composable
    fun ResetPasswordScreen() {
        val context = LocalContext.current
        var email by remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = {
                    sendPasswordResetEmail(email)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reset Password")
            }
            OutlinedButton(
                onClick = {
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Connexion")
            }
        }
    }
    fun sendPasswordResetEmail(email: String) {
        val auth = FirebaseAuth.getInstance()
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
// Toast.makeText(context, "Email de réinitialisation de mot de passe envoyé", Toast.LENGTH_SHORT).show()
                } else {
                  val errorMessage = task.exception?.message
//Toast.makeText(context, "Erreur : $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
    }
}