package ru.sample.duckapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.sample.duckapp.data.StatusCode
import ru.sample.duckapp.domain.Duck
import ru.sample.duckapp.infra.Api.ducksApi

class MainActivity : AppCompatActivity() {

    private lateinit var buttonLoadDuck: Button
    private lateinit var imageViewDuck: ImageView
    private lateinit var editTextDuckId: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonLoadDuck = findViewById(R.id.buttonLoadDuck)
        imageViewDuck = findViewById(R.id.imageViewDuck)
        editTextDuckId = findViewById(R.id.editTextDuckId)

        buttonLoadDuck.setOnClickListener {

            if (isRandomRequest()) {
                loadRandomDuck()
                return@setOnClickListener
            }

            if (!validateDuckId()) {
                return@setOnClickListener
            }

            val duckId = editTextDuckId.text.toString().toInt()
            loadDuckImageById(duckId)
        }
    }

    private fun validateDuckId(): Boolean {
        val duckIdString = editTextDuckId.text.toString()
        val duckId = duckIdString.toIntOrNull()

        val httpCodes = StatusCode.values().map { it.code }
        return if (httpCodes.contains(duckId)) {
                true
            } else {
                Toast.makeText(this, "Введеный код должен являться http кодом", Toast.LENGTH_SHORT).show()
                false
            }
    }

    private fun isRandomRequest(): Boolean {
        val duckIdString = editTextDuckId.text.toString()
        if (duckIdString.isEmpty() || duckIdString.isBlank()) return true
        return false
    }

    private fun loadDuckImageById(duckId: Int) {


        ducksApi.getDuckById(duckId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val imageStream = response.body()?.byteStream()
                    if (imageStream != null) {
                        val bitmap = BitmapFactory.decodeStream(imageStream)
                        imageViewDuck.setImageBitmap(bitmap)
                    } else {
                        Toast.makeText(this@MainActivity, "Пустой ответ", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Ошибка при загрузке утки: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Ошибка при загрузке утки: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadRandomDuck() {
        ducksApi.getRandomDuck().enqueue(object : Callback<Duck> {
            override fun onResponse(call: Call<Duck>, response: Response<Duck>) {
                if (response.isSuccessful) {
                    val duck = response.body()
                    duck?.let {
                        Picasso.get().load(it.url).into(imageViewDuck)
                    } ?: run {
                        Toast.makeText(this@MainActivity, "Пустой ответ", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Ошибка при загрузке утки: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Duck>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity,
                    "Ошибка при загрузке утки: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }
}

