package pt.ipt.dam.projfinal

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Button
import kotlin.toString

class selecionar : AppCompatActivity() {

    private val anos = arrayOf("1", "2", "3")
    private val cursos = arrayOf("LEI", "LEEC", "DTAG")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selecionar)

        val btnSeguinte = findViewById<Button>(R.id.btnSeguinte)

        val txtAno = findViewById<AutoCompleteTextView>(R.id.txtAno)
        val adapterAno = ArrayAdapter(this, android.R.layout.simple_list_item_1, anos)
        txtAno.setAdapter(adapterAno)

        txtAno.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position).toString()
            Toast.makeText(this, "Ano: $selected", Toast.LENGTH_SHORT).show()
        }

        val txtCurso = findViewById<AutoCompleteTextView>(R.id.txtCurso)
        val adapterCursos = ArrayAdapter(this, android.R.layout.simple_list_item_1, cursos)
        txtCurso.setAdapter(adapterCursos)

        txtCurso.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position).toString()
            Toast.makeText(this, "Curso: $selected", Toast.LENGTH_SHORT).show()
        }
        btnSeguinte.setOnClickListener {
            val anoSelecionado = txtAno.text.toString()
            val cursoSelecionado = txtCurso.text.toString()

            if (anoSelecionado.isEmpty() || cursoSelecionado.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, horarios::class.java)

                intent.putExtra("ANO", anoSelecionado)
                intent.putExtra("CURSO", cursoSelecionado)

                startActivity(intent)
            }
        }

    }



}