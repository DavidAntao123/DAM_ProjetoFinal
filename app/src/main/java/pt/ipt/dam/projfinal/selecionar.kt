package pt.ipt.dam.projfinal

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ipt.dam.projfinal.horarios.ScheduleData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable


/**
 * Activity responsável por permitir ao utilizador
 * selecionar o ano e o curso antes de visualizar o horário pretendido
 */
class selecionar : AppCompatActivity() {

    // Lista de anos disponíveis
    private val anos = arrayOf("1", "2", "3")

    /**
     * Método executado quando a Activity é criada.
     * Inicializa os campos, adapters e listeners dos botões.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Liga esta Activity ao respetivo layout XML
        setContentView(R.layout.activity_selecionar)

        // Referência aos botões
        val btnHorario = findViewById<Button>(R.id.btnIrHorario)
        val btnVoltar = findViewById<Button>(R.id.btnVoltar)

        /**
         * Botão Voltar
         * Fecha esta Activity e regressa ao ecrã anterior
         */
        btnVoltar.setOnClickListener {
            finish()
        }

        // Campo AutoComplete do Ano
        val txtAno = findViewById<AutoCompleteTextView>(R.id.txtAno)

        // Adapter que fornece os valores do array "anos" ao dropdown
        val adapterAno = ArrayAdapter(this, android.R.layout.simple_list_item_1, anos)
        txtAno.setAdapter(adapterAno)

        /**
         * Listener do Ano
         * Mostra um Toast com o valor selecionado
         */
        txtAno.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position).toString()
            Toast.makeText(this, getString(R.string.toast_ano, selected), Toast.LENGTH_SHORT).show()
        }

        // Campo AutoComplete do Curso
        val txtCurso = findViewById<AutoCompleteTextView>(R.id.txtCurso)

        // Load cursos when activity starts
        loadCursos(txtCurso)

        txtCurso.setOnTouchListener { _, _ ->
            txtCurso.showDropDown()
            false
        }

        txtCurso.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position).toString()
            Toast.makeText(this, getString(R.string.toast_curso, selected), Toast.LENGTH_SHORT).show()
        }

        /**
         * Botão Ir para Horário
         * Valida os campos e abre a Activity de horários
         */
        btnHorario.setOnClickListener {
            // Obtém os valores introduzidos pelo utilizador
            val anoSelecionado = txtAno.text.toString()
            val cursoSelecionado = txtCurso.text.toString()

            val turma = cursoSelecionado + anoSelecionado
            fetchHorario(turma)

        }
    }

    private fun loadCursos(txtCurso: AutoCompleteTextView) {
        // Use RetrofitClient instead of creating a new Retrofit instance
        RetrofitClient.instance.getCursos().enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val listaCursos = response.body()
                    if (listaCursos != null && listaCursos.isNotEmpty()) {
                        runOnUiThread {
                            val adapter = ArrayAdapter(
                                this@selecionar,
                                android.R.layout.simple_list_item_1,
                                listaCursos
                            )
                            txtCurso.setAdapter(adapter)
                            println("DEBUG_API: Recebi ${listaCursos.size} cursos: $listaCursos")
                        }
                    } else {
                        println("DEBUG_API: A lista veio vazia!")
                        runOnUiThread {
                            Toast.makeText(
                                this@selecionar,
                                "Nenhum curso encontrado",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    println("DEBUG_API: Erro na resposta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                println("DEBUG_API: Falha na chamada: ${t.message}")
                runOnUiThread {
                    Toast.makeText(
                        this@selecionar,
                        "Erro de conexão: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
    private fun fetchHorario(turma: String) {
        lifecycleScope.launch {
            try {
                val horario = RetrofitClient.instance.getHorario(turma)

                // --- DEBUG CONSOLE ---
                println("--------------------------------------------------")
                println("DEBUG_API_RESULT (Objeto Inteiro): $horario")
                println("DEBUG_API_RESULT (Sala): ${horario.sala}")
                println("DEBUG_API_RESULT (Nº de Dias): ${horario.horario.dias.size}")

                println("--------------------------------------------------")
                // ---------------------

                val intent = Intent(this@selecionar, horarios::class.java)
                intent.putExtra("horario_data", horario.horario)
                intent.putExtra("turma", horario.turma)
                intent.putExtra("curso", horario.curso)
                intent.putExtra("ano", horario.ano)
                intent.putExtra("sala", horario.sala)

                startActivity(intent)

            } catch (e: Exception) {
                println("DEBUG_API_ERROR: ${e.message}")
                Toast.makeText(
                    this@selecionar,
                    "Erro ao carregar horário: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        }
    }

}

data class HorarioResponse(
    val horario: ScheduleData,
    val sala: String,
    val turma: String,
    val curso: String,
    val ano: String,
    val _id: String? = null,  // You might want to add this
    val __v: Int? = null       // And this
) : Serializable

data class ScheduleData(
    val dias: List<Dia>  // Matches JSON "dias"
) : Serializable

data class Dia(
    val nome: String,        // Matches JSON "nome"
    val timeSlots: List<timeSlots>,  // Matches JSON "timeSlots"
    val _id: String? = null  // Optional, since it's in the JSON
) : Serializable

data class timeSlots(
    val time: String,   // Matches JSON "time"
    val value: String,  // Matches JSON "value"
    val _id: String? = null  // Optional
) : Serializable