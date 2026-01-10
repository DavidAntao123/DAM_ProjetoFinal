package pt.ipt.dam.projfinal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Button


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val btnGoToSecond = findViewById<Button>(R.id.btnGoToSecond)
        val btnteste = findViewById<Button>(R.id.btnTeste)

        btnGoToSecond.setOnClickListener {

            //intent para na class cam
            val intent = Intent(this, cam::class.java)

            // Começar a activity
            startActivity(intent)


        }
        btnteste.setOnClickListener {

            //intent para na class cam
            val intent = Intent(this, Horarios::class.java)

            // Começar a activity
            startActivity(intent)


        }
    }
}