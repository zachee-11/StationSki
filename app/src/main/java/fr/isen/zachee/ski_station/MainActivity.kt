package fr.isen.zachee.ski_station

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.zachee.ski_station.SkiDatabase.Companion.database
import fr.isen.zachee.ski_station.ui.theme.SkiStationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.AccessController
import kotlin.time.Duration.Companion.milliseconds


enum class DisplayMode {
    Slopes, Lifts
}
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkiStationTheme {

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
        Log.d("lifeCycle", "Home Activity - OnCreate")

    }
    override fun onPause() {
        Log.d("lifeCycle", "Home Activity - OnPause")
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        Log.d("lifeCycle", "Home Activity - OnResume")
    }

    override fun onDestroy() {
        Log.d("lifeCycle", "Home Activity - onDestroy")
        super.onDestroy()
    }

}

@Composable
fun MainScreen(){
    val context = LocalContext.current
    Box (
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(16.dp))
    {


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Button(
                onClick = {
                    context.startActivity(Intent(context, SlopesActivity::class.java))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
            ) {
                Text("Slopes")
            }

            Button(
                onClick = {
                    context.startActivity(Intent(context, LiftsActivity::class.java))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
            ) {
                Text("Lifts")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SkiStationTheme {
        MainScreen()
    }
}




@Composable
fun fetchAndDisplayImages(context: Context) {
    val pixabayApiService = PixabayApiService(context)
    val imageUrls = remember { mutableStateOf(listOf<String>()) }
    val scrollState = rememberLazyListState()

    LaunchedEffect(key1 = Unit) {
        pixabayApiService.fetchImages { urls ->
            imageUrls.value = urls
        }
    }

    // Défilement automatique avec rebouclage
    LaunchedEffect(key1 = imageUrls.value) {
        while (true) { // Boucle infinie pour le rebouclage
            delay(3000.milliseconds) // Attendre 3 secondes avant de commencer à défiler
            imageUrls.value.indices.forEach { index ->
                scrollState.animateScrollToItem(index)
                delay(2000.milliseconds) // Attendre 2 secondes avant de passer à l'image suivante
            }
            // Après avoir atteint la dernière image, la boucle va reboucler et recommencer depuis la première image
        }
    }

    LazyRow(state = scrollState) {
        items(imageUrls.value) { imageUrl ->
            PixabayImageComponent(imageUrl = imageUrl, modifier = Modifier
                .fillMaxSize()
                .size(400.dp, 160.dp))
            Spacer(modifier = Modifier.width(0.dp)) // Ajustez la largeur pour contrôler l'espacement
        }
    }
}

@Composable
fun PixabayImageComponent(imageUrl: String, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = rememberImagePainter(data = imageUrl),
            contentDescription = null,
            modifier = Modifier
                .size(400.dp, 250.dp)
            //.aspectRatio(2f)
        )
    }
}

@Composable
fun VideoPlayerComponent(videoUrl: String, modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val exoPlayer = remember(context) {
        SimpleExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM // Adaptez cela selon vos besoins
            }
        },
        modifier = modifier
            .size(300.dp, 100.dp)
            .offset(x = 5.dp, y = -5.dp)
    )

}

@Composable
fun fetchAndDisplayVideos(context: Context) {
    val pixabayApiService = PixabayApiService(context)
    val videoUrls = remember { mutableStateOf(listOf<String>()) }
    val scrollState = rememberLazyListState()

    LaunchedEffect(Unit) {
        pixabayApiService.fetchVideos { urls ->
            videoUrls.value = urls
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val speed = 1f // Vitesse de défilement, ajustez selon les besoins
    // Capture de la densité courante
    val density = LocalDensity.current

    DisposableEffect(key1 = true) {
        val job = coroutineScope.launch {
            while (isActive) {
                delay(50) // Ajustez cette valeur pour contrôler la vitesse de défilement
                withContext(Dispatchers.Main) {
                    val scrollAmount = with(density) { speed.dp.toPx() }
                    val shouldReboucle = scrollState.firstVisibleItemIndex + 1 >= videoUrls.value.size &&
                            scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.offset ?: 0 > scrollAmount
                    if (shouldReboucle) {
                        scrollState.scrollToItem(0)
                    } else {
                        scrollState.scrollBy(scrollAmount)
                    }
                }
            }
        }
        onDispose {
            job.cancel()
        }
    }


    LazyRow(state = scrollState) {
        items(videoUrls.value) { videoUrl ->
            VideoPlayerComponent(videoUrl = videoUrl, modifier = Modifier.size(400.dp, 180.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}



