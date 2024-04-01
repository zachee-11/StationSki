package fr.isen.zachee.ski_station.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import androidx.activity.compose.setContent
import com.google.firebase.ktx.Firebase
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import android.util.Log
import android.widget.Toast
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import fr.isen.zachee.ski_station.MainActivity
import fr.isen.zachee.ski_station.R

@OptIn(ExperimentalMaterial3Api::class)
class SignUpActivity : ComponentActivity() {
    private  lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        setContent {
            var email by remember { mutableStateOf("") }
            remember { mutableStateOf(TextFieldValue("")) }
            var password by remember { mutableStateOf("") }
            val emty by remember { mutableStateOf("") }
            var emailError by remember { mutableStateOf(false) }
            var errorP by remember { mutableStateOf(false) }
            var passwordVisibility by remember { mutableStateOf(false) }
            var plength by remember { mutableStateOf(false) }

            val context = LocalContext.current

            Image(
                painter = painterResource(id = R.drawable.rd),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                Modifier
                    .padding(start = 4.dp, top = 16.dp)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.stationski),
                    contentDescription = null,
                    Modifier.size(250.dp)
                )
                Text(
                    text = "Inscription",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp
                )
                Spacer(modifier = Modifier.height(40.dp))
                if (emailError) {
                    Text(
                        text = " Entrez votre email *",
                        color = Color.Red,
                        modifier = Modifier.padding(end = 60.dp)
                    )
                }
                TextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = it.isEmpty()
                    },
                    label = {
                        Text(text = "Email *")
                    },


                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_person_24),
                            contentDescription = null
                        )
                    },
                    trailingIcon = {    if (email.isNotEmpty()) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_close_24),
                            contentDescription = null,
                            Modifier.clickable { email = emty }
                        )
                    }
                    },

                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    ),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .width(300.dp)
                        .height(60.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        containerColor = Color(0x30FFFFFF),
                        focusedLeadingIconColor = Color.White,
                        unfocusedLeadingIconColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        cursorColor = Color.Red,
                        focusedTrailingIconColor = Color.White,
                        unfocusedTrailingIconColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(30.dp))
                if (errorP) {
                    Text(
                        text = "Entrez votre mot de passe *",
                        color = Color.Red,
                        modifier = Modifier.padding(end = 100.dp)
                    )
                }
                if (plength) {
                    Text(
                        text = "Mot de passe: aumoins 6 caractères",
                        color = Color.Red,
                        modifier = Modifier.padding(end = 100.dp)
                    )
                }
                TextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorP = it.isEmpty()
                        plength = it.length < 6
                    },
                    label = {
                        Text(text = "Password *")
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_lock_24),
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (password.isNotEmpty()) {
                            val visibilityIcon = if (passwordVisibility) {
                                painterResource(id = R.drawable.baseline_visibility_24)
                            } else {
                                painterResource(id = R.drawable.baseline_visibility_off_24)
                            }
                            Icon(
                                painter = visibilityIcon,
                                contentDescription = if (passwordVisibility) "Hide Password" else "Show Password",
                                Modifier.clickable {
                                    passwordVisibility = !passwordVisibility
                                }
                            )
                        }
                    },
                    visualTransformation = if (passwordVisibility) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Password
                    ),
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .width(300.dp)
                        .height(60.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        containerColor = Color(0x30FFFFFF),
                        focusedLeadingIconColor = Color.White,
                        unfocusedLeadingIconColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        cursorColor = Color.Red,
                        focusedTrailingIconColor = Color.White,
                        unfocusedTrailingIconColor = Color.White
                    )
                )
                OutlinedButton(onClick = {
                    if (email.isEmpty() || password.isEmpty()) {
                        emailError = email.isEmpty()
                        errorP = password.isEmpty()
                        return@OutlinedButton
                    }
                    register(email, password) }) {
                    Text("S'inscrire")
                }
                TextButton(onClick = {
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                }) {
                    Text("Connexion")
                }

            }
        }
    }

    fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    goToSlopes()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("auth", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "email ou mot de passe incorrect.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }
    fun goToSlopes() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}