package pt.ipt.dam.projfinal

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Activity responsável por permitir ao utilizador
 * selecionar o ano e o curso antes de visualizar o horário pretendido
 */
class selecionar : AppCompatActivity() {

    // Lista de anos disponíveis
    private val anos = arrayOf("1", "2", "3")

    // Lista de cursos disponíveis
    private val cursos = arrayOf("LEI", "LEEC", "DTAG")

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

        // Adapter que fornece os valores do array "cursos" ao dropdown
        val adapterCursos = ArrayAdapter(this, android.R.layout.simple_list_item_1, cursos)
        txtCurso.setAdapter(adapterCursos)

        /**
         * Listener do Curso
         * Mostra um Toast com o curso selecionado
         */
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
}
