package pt.ipt.dam.projfinal

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class sobre : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sobre)

        val btnVoltar = findViewById<Button>(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            finish()
        }
    }
}
