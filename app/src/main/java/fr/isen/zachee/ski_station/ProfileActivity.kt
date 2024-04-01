package fr.isen.zachee.ski_station

import androidx.activity.ComponentActivity
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.activity.compose.setContent
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle


class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = intent.getSerializableExtra("user") as? User ?: User("", "")

        setContent {
            ProfileScreen(user)
        }
    }
}
data class User(val name: String, val email: String)

@Composable
fun ProfileScreen(user: User) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Profile Information",
            color = Color.Black,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        ProfileItem("Name", user.name)
        ProfileItem("Email", user.email)
        // Ajoutez d'autres informations de profil si nécessaire
    }
}

@Composable
fun ProfileItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 16.sp
        )
        BasicTextField(
            value = value,
            onValueChange = {},
            textStyle = TextStyle(color = Color.Black, fontSize = 18.sp),
            modifier = Modifier.fillMaxWidth(),
            readOnly = true
        )
    }
}
