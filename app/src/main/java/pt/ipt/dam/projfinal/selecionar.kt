package pt.ipt.dam.projfinal

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pt.ipt.dam.projfinal.horarios.ScheduleData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable


/**
 * Activity responsável por permitir ao utilizador selecionar
 * o ano, curso ou sala antes de visualizar o horário.
 *
 * Comunica com a API através do Retrofit para:
 *  Obter lista de cursos
 *  Obter lista de salas
 *  Obter horários por turma ou por sala
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
        val btnSala = findViewById<Button>(R.id.btnIrSala)
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
        // Adapter com os anos disponíveis
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

        // Carrega cursos a partir da API
        loadCursos(txtCurso)
        // Força abertura do dropdown ao tocar
        txtCurso.setOnTouchListener { _, _ ->
            txtCurso.showDropDown()
            false
        }
        // Toast com curso selecionado
        txtCurso.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position).toString()
            Toast.makeText(this, getString(R.string.toast_curso, selected), Toast.LENGTH_SHORT).show()
        }


        /**
         * Campo AutoComplete da Sala
         */
        val txtSala = findViewById<AutoCompleteTextView>(R.id.txtSala)

        // Load cursos when activity starts
        loadCursos(txtCurso)
        // Carrega salas da API
        loadSalas(txtSala)

        txtCurso.setOnTouchListener { _, _ ->
            txtCurso.showDropDown()
            false
        }
        txtCurso.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position).toString()
            Toast.makeText(this, getString(R.string.toast_curso, selected), Toast.LENGTH_SHORT).show()
        }

        /**
         * Botão Ir para Horário (por curso + ano)
         */
        btnHorario.setOnClickListener {
            // Obtém os valores introduzidos pelo utilizador
            val anoSelecionado = txtAno.text.toString()
            val cursoSelecionado = txtCurso.text.toString()
            // Exemplo: LEI + 3
            val turma = cursoSelecionado + anoSelecionado
            fetchHorariobyTurma(turma)

        }
        /**
         * Botão Ir por Sala
         */
        btnSala.setOnClickListener {
            val sala = txtSala.text.toString()
            fetchHorariobySala(sala)

        }

    }
    /**
     * Obtém lista de cursos da API e coloca no AutoCompleteTextView
     */
    private fun loadCursos(txtCurso: AutoCompleteTextView) {

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

    private fun loadSalas(txtSala: AutoCompleteTextView) {
        // Use RetrofitClient instead of creating a new Retrofit instance
        RetrofitClient.instance.getSalas().enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val listaSalas = response.body()
                    if (listaSalas != null && listaSalas.isNotEmpty()) {
                        runOnUiThread {
                            val adapter = ArrayAdapter(
                                this@selecionar,
                                android.R.layout.simple_list_item_1,
                                listaSalas
                            )
                            txtSala.setAdapter(adapter)
                            println("DEBUG_API: Recebi ${listaSalas.size} cursos: $listaSalas")
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
    /**
     * Chama API usando Coroutine e abre Activity horarios
     */
    private fun fetchHorariobyTurma(turma: String) {
        lifecycleScope.launch {
            try {
                val horario = RetrofitClient.instance.getHorarioByTurma(turma)


                val intent = Intent(this@selecionar, horarios::class.java)
                // Envia dados pela Intent
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

    private fun fetchHorariobySala(sala: String) {
        lifecycleScope.launch {
            try {
                val horario = RetrofitClient.instance.getHorarioBySala(sala)


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
/**
 * HorarioResponse
 *
 * Classe principal da resposta da API.
 * Representa um horário completo devolvido pelo servidor Node.js.
 *
 * Contém:
 * horário (dias + blocos)
 * sala
 * turma
 * curso
 * ano
 *
 * Implementa Serializable para permitir envio entre Activities via Intent.
 */
data class HorarioResponse(
    val horario: ScheduleData,  // Estrutura principal do horário
    val sala: String,           // Sala (ex: I153)
    val turma: String,          // Turma (ex: LEI3)
    val curso: String,          // Curso (ex: LEI)
    val ano: String,            // Ano (ex: 3)
    val _id: String? = null,   // ID MongoDB
    val __v: Int? = null       // Versão MongoDB
) : Serializable

/**
 * ScheduleData
 *
 * Representa apenas a parte "horario" do JSON.
 * Contém uma lista de dias da semana
 */
data class ScheduleData(
    val dias: List<Dia>
) : Serializable

/**
 * Dia
 *
 * Representa um dia da semana (ex: Segunda).
 * Contém:
 * nome do dia
 * lista de blocos horários (timeSlots)
 */
data class Dia(
    val nome: String,        // Nome do dia (Segunda, Terça, etc.)
    val timeSlots: List<timeSlots>,  // Lista de aulas desse dia
    val _id: String? = null  // ID MongoDB
) : Serializable

/**
 * timeSlots
 *
 * Representa um bloco horário individual.
 *
 * Contém:
 * - hora (ex: 08:00-10:00)
 * - valor (nome da disciplina)
 * - cor (hexadecimal para pintar a célula)
 */
data class timeSlots(
    val time: String,   /// Hora do bloco
    val value: String,  // Nome da disciplina
    val cor: String,    // Cor enviada pela API
    val _id: String? = null  // ID MongoDB (opcional)
) : Serializable