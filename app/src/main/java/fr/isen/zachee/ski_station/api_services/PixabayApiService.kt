package fr.isen.zachee.ski_station.api_services


import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class PixabayApiService(private val context: Context) {
    companion object {
        const val API_KEY = "42765549-14bb25b99e1c4484cd8caa190"
        const val BASE_URL = "https://pixabay.com/api/"
    }
    private val queue = Volley.newRequestQueue(context)

    fun fetchImages(query: String = "skieur", imageType: String = "all", callback: (List<String>) -> Unit) {
        val url = "$BASE_URL?key=$API_KEY&q=$query&image_type=$imageType"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                val jsonObject = JSONObject(response)
                val jsonArray = jsonObject.getJSONArray("hits")
                val imageUrls = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    val hit = jsonArray.getJSONObject(i)
                    imageUrls.add(hit.getString("webformatURL"))
                }
                callback(imageUrls)
            },
            { error -> error.printStackTrace() }
        )
        queue.add(stringRequest)
    }


    fun fetchVideos(query: String = "skieur", callback: (List<String>) -> Unit) {
        val url = "$BASE_URL/videos/?key=$API_KEY&q=$query"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                val jsonObject = JSONObject(response)
                val jsonArray = jsonObject.getJSONArray("hits")
                val videoUrls = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    val hit = jsonArray.getJSONObject(i)
                    videoUrls.add(hit.getJSONObject("videos").getJSONObject("large").getString("url"))
                }
                callback(videoUrls)
            },
            { error -> error.printStackTrace() }
        )
        queue.add(stringRequest)
    }

}