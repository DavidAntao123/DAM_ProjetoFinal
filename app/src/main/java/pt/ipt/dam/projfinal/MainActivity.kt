package pt.ipt.dam.projfinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

/**
 * MainActivity
 * Ecrã principal (Menu Inicial) da aplicação.
 * Permite navegar para:
 * - Leitura de QR Code (cam)
 * - Secção Sobre (Sobre)
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnQR = findViewById<Button>(R.id.btnQR)
        val btnSobre = findViewById<Button>(R.id.btnSobre)

        btnQR.setOnClickListener {
            val intent = Intent(this, cam::class.java)
            startActivity(intent)
        }

        btnSobre.setOnClickListener {
            val intent = Intent(this, Sobre::class.java)
            startActivity(intent)
        }
    }
}

