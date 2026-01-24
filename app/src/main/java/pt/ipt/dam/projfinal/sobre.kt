package pt.ipt.dam.projfinal

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
/**
 * Activity responsável por mostrar as informações sobre a aplicação
 * - Curso / UC / Ano letivo
 * - Autores do trabalho
 * - Nome da aplicação
 *
 * Possui um botão Voltar que fecha este ecrã.
 */
class sobre : AppCompatActivity() {
    /**
     * Método chamado quando a Activity é criada
     * Aqui ligamos o layout XML e definimos o clique do botão voltar
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Liga a Activity ao layout activity_sobre.xml
        setContentView(R.layout.activity_sobre)
        // Botão Voltar -> fecha este ecrã e regressa ao anterior
        val btnVoltar = findViewById<Button>(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            finish()
        }
    }
}
