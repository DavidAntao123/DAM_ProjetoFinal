package pt.ipt.dam.projfinal

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
//import pt.ipt.dam.projfinal.HorarioApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
/**
 * Activity responsável por permitir ao utilizador
 * selecionar o ano e o curso antes de visualizar o horário pretendido
 */
class selecionar : AppCompatActivity() {

    data class Horario(
        val turma: String,
        val curso: String,
        val ano: String,
        val horario: String
    )
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
            val btnHorario = findViewById<Button>(R.id.btnHorario)
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
            getCursos { lista ->
                if (lista.isNotEmpty()) {
                    // Usar o contexto da Activity (this@NomeDaSuaActivity)
                    val adapter = ArrayAdapter(this@selecionar, android.R.layout.simple_list_item_1, lista)
                    txtCurso.setAdapter(adapter)

                    // Log para conferir no Logcat se os dados chegaram
                    println("DEBUG_API: Recebi ${lista.size} cursos")
                } else {
                    println("DEBUG_API: A lista veio vazia!")
                }
            }


            txtCurso.setOnTouchListener { _, _ ->
                txtCurso.showDropDown()
                false
            }
            txtCurso.setOnItemClickListener { parent, _, position, _ ->
                val selected = parent.getItemAtPosition(position).toString()
                Toast.makeText(this, getString(R.string.toast_curso, selected), Toast.LENGTH_SHORT)
                    .show()
            }

            /**
             * Botão Ir para Horário
             * Valida os campos e abre a Activity de horários
             */
            btnHorario.setOnClickListener {

                // Obtém os valores introduzidos pelo utilizador
                val anoSelecionado = txtAno.text.toString()
                val cursoSelecionado = txtCurso.text.toString()

                // Validação dos campos
                if (anoSelecionado.isEmpty() || cursoSelecionado.isEmpty()) {

                    // Mensagem de erro se algum campo estiver vazio
                    Toast.makeText(this, getString(R.string.erro_campos), Toast.LENGTH_SHORT).show()

                } else {

                    // Cria Intent para abrir a Activity horarios
                    val intent = Intent(this, horarios::class.java)

                    // Envia os dados selecionados para a próxima Activity
                    intent.putExtra("ANO", anoSelecionado)
                    intent.putExtra("CURSO", cursoSelecionado)

                    // Abre o ecrã de horários
                    startActivity(intent)
                }
            }
    }
    private fun getCursos(onResult: (List<String>) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(HorarioApiService::class.java)

        service.getCursos().enqueue(object : retrofit2.Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val listaCursos = response.body() ?: emptyList()
                    onResult(listaCursos)
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Toast.makeText(this@selecionar, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
                onResult(emptyList()) // Devolve lista vazia em caso de erro
            }
        })
    }}
