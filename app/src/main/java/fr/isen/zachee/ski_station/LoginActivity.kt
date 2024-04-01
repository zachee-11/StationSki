package fr.isen.zachee.ski_station

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text

import dagger.hilt.android.AndroidEntryPoint

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import androidx.activity.compose.setContent
import com.google.firebase.ktx.Firebase
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.google.android.gms.common.api.ApiException
import androidx.compose.runtime.MutableState


@OptIn(ExperimentalMaterial3Api::class)
class LoginActivity : ComponentActivity() {
    private  lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val currentUser = auth.currentUser
        if (currentUser != null) {

        }
        setContent {

            var email by remember { mutableStateOf("") }
            var emailError by remember { mutableStateOf(false) }
            val passwordValue = remember { mutableStateOf(TextFieldValue("")) }
            var password by remember { mutableStateOf("") }
            val emty by remember { mutableStateOf("") }
            var errorE by remember { mutableStateOf(false) }
            var errorP by remember { mutableStateOf(false) }
            var passwordVisibility by remember { mutableStateOf(false) }
            var cpasswordVisibility by remember { mutableStateOf(false) }

            var plength by remember { mutableStateOf(false) }
            val context = LocalContext.current
            val googleSignInLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        val account = task.getResult(ApiException::class.java)
                        firebaseAuthWithGoogle(account.idToken!!)
                    } catch (e: ApiException) {
                        Log.w("LoginActivity", "Google sign in failed", e)
                    }
                }
            }


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
                    Modifier.size(180.dp)
                )
                Text(
                    text = "Log In",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
               /* if (errorMessageState != null) {
                    // Afficher le message d'erreur en bas de l'écran en rouge
                   Text(
                        text = errorMessageState!!,
                        color = Color.Red,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }*/
                if (emailError) { // Afficher le message d'erreur uniquement si le champ email est vide
                    Text(
                        text = " Enter Email.",
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
                        Text(text = "Email")
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
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Email,
                        autoCorrect = false,
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            if (!email.endsWith(".com")) {
                                email += ".com"
                            }
                        }
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
                        text = "Enter Password",
                        color = Color.Red,
                        modifier = Modifier.padding(end = 60.dp)
                    )
                }
                if (plength) {
                    Text(
                        text = "Password must be 6 characters",
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
                        Text(text = "Password")
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
                Text(
                    "Forgot your password?",
                    style = TextStyle(color = Color.Yellow),
                    modifier = Modifier.clickable {
                        val intent = Intent(context, ResetPasswordActivity::class.java)
                        startActivity(intent)
                    }
                )
                OutlinedButton(onClick = {
                    if (email.isEmpty() || password.isEmpty()) {
                        // Mettre à jour l'état d'erreur du champ password si nécessaire
                        emailError = email.isEmpty()
                        errorP = password.isEmpty()
                        return@OutlinedButton
                    }
                    connection(
                        email,
                        password
                    )
                },
                    modifier = Modifier.padding(8.dp)

                ) {
                    Text("Connexion",style = TextStyle(fontSize = 20.sp,fontWeight = FontWeight.Bold ))
                }
                TextButton(onClick = {
                    val intent = Intent(context, SignUpActivity::class.java)
                    startActivity(intent)
                }) {
                    Text("Register",style = TextStyle(fontSize = 16.sp,fontWeight = FontWeight.Bold ))
                }
                TextButton(
                    onClick = {

                        val signInIntent = googleSignInClient.signInIntent
                        googleSignInLauncher.launch(signInIntent)
                    },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = "Sign in with Google",
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
        }
    }


    fun connection(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    goToSlopes()
                } else {
                    /*val errorMessage = task.exception?.message ?: "Unknown error"

                    // Déterminer le type d'erreur (erreur d'email ou erreur de mot de passe)
                    val errorType = if (errorMessage.contains("password")) {
                        "password"
                    } else if (errorMessage.contains("email")) {
                        "email"
                    } else {
                        "unknown"
                    }
                //    errorMessageState = errorMessage*/
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()

                    Log.w("auth", "signInWithEmail:failure", task.exception)
                }
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    goToSlopes()
                } else {
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
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
