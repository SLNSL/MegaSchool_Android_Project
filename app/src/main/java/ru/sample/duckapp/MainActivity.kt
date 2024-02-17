package ru.sample.duckapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import ru.sample.duckapp.infra.DuckClient

class MainActivity : AppCompatActivity() {

    private lateinit var buttonLoadDuck: Button
    private lateinit var imageViewDuck: ImageView
    private lateinit var editTextDuckId: EditText
    private var duckClient = DuckClient(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonLoadDuck = findViewById(R.id.buttonLoadDuck)
        imageViewDuck = findViewById(R.id.imageViewDuck)
        editTextDuckId = findViewById(R.id.editTextDuckId)

        buttonLoadDuck.setOnClickListener {

            var duckIdString = editTextDuckId.text.toString()
            if (duckClient.isRandomRequest(duckIdString)) {
                duckClient.loadRandomDuck(imageViewDuck)
                return@setOnClickListener
            }

            if (!duckClient.validateDuckId(duckIdString)) {
                return@setOnClickListener
            }

            val duckId = duckIdString.toInt()
            duckClient.loadDuckImageById(duckId, imageViewDuck)
        }
    }
}

