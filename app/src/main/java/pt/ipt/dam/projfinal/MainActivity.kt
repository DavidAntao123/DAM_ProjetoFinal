package pt.ipt.dam.projfinal
// Imports necessários para navegar entre Activities
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

/**
 * MainActivity
 * Ecrã principal -> Menu inicial da aplicação
 * Abrir o ecrã de leitura do QR Code (cam)
 * Abrir o ecrã "Sobre a Aplicação" (sobre)
 * Abrir o ecrã de horários
 */
class MainActivity : AppCompatActivity() {
    /**
     * Método chamado quando a Activity é criada
     * Aqui ligamos o layout e criamos os cliques dos botões
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Liga esta Activity ao layout XML activity_main.xml
        setContentView(R.layout.activity_main)
        // Procurar os botões do layout através do ID
        // botão "Ler QR Code"
        val btnQR = findViewById<Button>(R.id.btnQR)
        // botão "Sobre a App"
        val btnSobre = findViewById<Button>(R.id.btnSobre)
        // botão "Horários"
        val btnIrHorario = findViewById<Button>(R.id.btnHorario)
        /**
         * Clique no botão QR
         * abre a Activity cam, responsável pela câmara e leitura do QR Code
         */
        btnQR.setOnClickListener {
            val intent = Intent(this, cam::class.java)
            startActivity(intent)
        }
        /**
         * Clique no botão Sobre
         * abre a Activity sobre, que contém as informações do nosso projeto
         */
        btnSobre.setOnClickListener {
            val intent = Intent(this, sobre::class.java)
            startActivity(intent)
        }
        /**
         * Clique no botão Horários
         * abre a Activity horarios, onde é mostrado o horário em formato de tabela
         */
        btnIrHorario.setOnClickListener {
            val intent = Intent(this, horarios::class.java)
            startActivity(intent)
        }
    }
}

