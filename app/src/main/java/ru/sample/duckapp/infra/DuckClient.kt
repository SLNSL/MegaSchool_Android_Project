package ru.sample.duckapp.infra

import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.Toast
import com.squareup.picasso.Picasso
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.sample.duckapp.MainActivity
import ru.sample.duckapp.R
import ru.sample.duckapp.data.StatusCode
import ru.sample.duckapp.domain.Duck
import ru.sample.duckapp.infra.Api.ducksApi

class DuckClient(private val activity: MainActivity) {
    fun validateDuckId(duckIdString: String): Boolean {
        val duckId = duckIdString.toIntOrNull()

        val httpCodes = StatusCode.values().map { it.code }
        return if (httpCodes.contains(duckId)) {
            true
        } else {
            Toast.makeText(activity, "Введеный код должен являться http кодом", Toast.LENGTH_SHORT)
                .show()
            false
        }
    }

    fun isRandomRequest(duckIdString: String): Boolean {
        if (duckIdString.isEmpty() || duckIdString.isBlank()) return true
        return false
    }

    fun loadDuckImageById(duckId: Int, imageView: ImageView) {


        ducksApi.getDuckById(duckId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val imageStream = response.body()?.byteStream()
                    if (imageStream != null) {
                        val bitmap = BitmapFactory.decodeStream(imageStream)
                        imageView.setImageBitmap(bitmap)
                    } else {
                        Toast.makeText(activity, "Пустой ответ", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Picasso.get().load(R.drawable.oops).into(imageView)
                    Toast.makeText(
                        activity,
                        "Ошибка при загрузке утки: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                    activity,
                    "Ошибка при загрузке утки: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    fun loadRandomDuck(imageViewDuck: ImageView) {
        ducksApi.getRandomDuck().enqueue(object : Callback<Duck> {
            override fun onResponse(call: Call<Duck>, response: Response<Duck>) {
                if (response.isSuccessful) {
                    val duck = response.body()
                    duck?.let {
                        Picasso.get().load(it.url).into(imageViewDuck)
                    } ?: run {
                        Toast.makeText(activity, "Пустой ответ", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Picasso.get().load(R.drawable.oops).into(imageViewDuck)
                    Toast.makeText(
                        activity,
                        "Ошибка при загрузке утки: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Duck>, t: Throwable) {
                Toast.makeText(
                    activity,
                    "Ошибка при загрузке утки: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }
}
