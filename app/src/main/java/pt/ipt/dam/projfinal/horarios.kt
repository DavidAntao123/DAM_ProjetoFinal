package pt.ipt.dam.projfinal

// Imports para criar cores e bordas nas células da tabela
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
// Imports de componentes UI
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast

/**
 * Esta Activity é responsável por mostrar horários em formato de tabela
 * Recebe dados da API via Intent e os exibe em formato de tabela
 */
class horarios : AppCompatActivity() {

    // TableLayout onde o horário será desenhado
    private lateinit var tableLayout: TableLayout

    // Botões do ecrã
    private lateinit var btnClear: Button
    private lateinit var btnVoltarHorario: Button

    /**
     * Guarda a última cor usada em cada coluna para manter consistência
     */
    val memoriaCores = mutableMapOf(
        "Segunda" to "#FFFFFF",
        "Terca" to "#FFFFFF",
        "Quarta" to "#FFFFFF",
        "Quinta" to "#FFFFFF",
        "Sexta" to "#FFFFFF",
        "Sabado" to "#FFFFFF"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horarios)

        // Inicialização dos componentes
        tableLayout = findViewById(R.id.tableLayout)
        btnClear = findViewById(R.id.btnClear)
        btnVoltarHorario = findViewById(R.id.btnVoltarHorario)

        // Receber dados da Intent
        val horarioData = intent.getSerializableExtra("horario_data") as? ScheduleData
        val turma = intent.getStringExtra("turma") ?: ""
        val curso = intent.getStringExtra("curso") ?: ""
        val ano = intent.getStringExtra("ano") ?: ""
        val sala = intent.getStringExtra("sala") ?: ""

        if (horarioData != null) {
            // Criar objeto ScheduleResponse com os dados recebidos
            val fullResponse = ScheduleResponse(
                sala = sala,
                turma = turma,
                curso = curso,
                ano = ano,
                horario = horarioData
            )
            generateTableFromJson(fullResponse)

            // Mostrar título com informações da turma
            supportActionBar?.title = "$curso $ano - Turma: $turma - Sala: $sala"
        } else {
            // Sem dados, mostrar tabela vazia
            generateEmptyTable()
            Toast.makeText(this, "Nenhum dado de horário recebido", Toast.LENGTH_SHORT).show()
        }

        // Botão Limpar
        btnClear.setOnClickListener {
            clearTable()
        }

        // Botão Voltar
        btnVoltarHorario.setOnClickListener {
            finish()
        }
    }

    /**
     * generateTableFromJson()
     * Cria a tabela do horário a partir dos dados recebidos
     */
    private fun generateTableFromJson(horarioData: ScheduleResponse) {
        // Limpa a tabela
        tableLayout.removeAllViews()

        val dias = horarioData.horario.dias  // List<Dia>

        // Lista de nomes dos dias para o cabeçalho
        val nomesDias = dias.map { it.nome }

        // Criar cabeçalho
        val header = TableRow(this)
        // Primeira célula - Hora
        header.addView(createCell(getString(R.string.horariosTitulo), "#C5CAE9", true))
        // Células para cada dia
        for (nomeDia in nomesDias) {
            header.addView(createCell(nomeDia, "#C5CAE9", true))
        }
        tableLayout.addView(header)

        // Verificar se há dias e timeSlots
        if (dias.isNotEmpty() && dias.first().timeSlots.isNotEmpty()) {

            // Pegar todos os timeSlots do primeiro dia (assumindo que todos os dias têm os mesmos horários)
            val todosTimeSlots = dias.first().timeSlots.map { it.time }

            // Para cada horário, criar uma linha
            todosTimeSlots.forEachIndexed { index, timeSlotTime ->

                val row = TableRow(this)

                // Coluna da hora
                row.addView(createCell(timeSlotTime, "#E8EAF6", false))

                // Para cada dia, buscar o valor correspondente neste horário
                dias.forEach { dia ->
                    // Encontrar o slot para este horário neste dia
                    val slot = dia.timeSlots.find { it.time == timeSlotTime }
                    val conteudo = slot?.value ?: ""

                    // Determinar cor
                    val corAtual = getCellColor(conteudo)

                    if (conteudo.isEmpty()) {
                        memoriaCores[dia.nome] = "#FFFFFF"
                    } else if (corAtual != "#9575CD" && corAtual != "#FFFFFF") {
                        memoriaCores[dia.nome] = corAtual
                    }

                    val corParaPintar = memoriaCores[dia.nome] ?: "#FFFFFF"
                    row.addView(createCell(conteudo, corParaPintar, false))
                }

                tableLayout.addView(row)
            }
        }
    }

    /**
     * generateEmptyTable()
     * Cria uma tabela vazia com cabeçalho
     */
    private fun generateEmptyTable() {
        tableLayout.removeAllViews()

        val dias = listOf("Segunda", "Terca", "Quarta", "Quinta", "Sexta", "Sabado")

        // Header
        val header = TableRow(this)
        header.addView(createCell(getString(R.string.horariosTitulo), "#C5CAE9", true))
        for (dia in dias) {
            header.addView(createCell(dia, "#C5CAE9", true))
        }
        tableLayout.addView(header)

        Toast.makeText(this, getString(R.string.tabela_vazia), Toast.LENGTH_SHORT).show()
    }

    /**
     * clearTable()
     * Remove todas as linhas da tabela
     */
    private fun clearTable() {
        tableLayout.removeAllViews()
        Toast.makeText(this, getString(R.string.tabela_limpa), Toast.LENGTH_SHORT).show()
    }

    /**
     * createCell()
     * Cria uma célula da tabela como TextView
     */
    private fun createCell(text: String, color: String, isHeader: Boolean): TextView {
        return TextView(this).apply {
            this.text = text
            setPadding(32, 32, 32, 32)
            this.setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )

            // Criar fundo com cor e borda
            val shape = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE

                try {
                    setColor(Color.parseColor(color))
                } catch (e: Exception) {
                    setColor(Color.WHITE)
                }

                setStroke(2, Color.BLACK)
            }

            background = shape
        }
    }

    /**
     * Define cor da célula conforme a cadeira
     */
    private fun getCellColor(content: String): String {
        return if (content.isNotEmpty()) {
            when {
                // == 1º TESP ==
                content.equals("A.R.S.I", true) -> "#a8326f"

                // == 1º ano ==
                content.contains("Algebra", true) -> "#FFECB3"
                content.contains("A. Matematica I", true) -> "#C8E6C9"
                content.contains("S.D", true) -> "#E1BEE7"
                content.contains("I.E.T", true) -> "#B3E5FC"
                content.contains("I.P.R.P", true) -> "#D1C4E9"

                // == 2º ano ==
                content.contains("Redes I", true) -> "#C5E1A5"
                content.contains("A.C", true) -> "#B2EBF2"
                content.contains("I.W", true) -> "#E1F5FE"

                // == 3º ano ==
                content.contains("I.R.L", true) -> "#FFCCBC"
                content.equals("S.I", true) -> "#B9F6CA"
                content.contains("C.D", true) -> "#D1C4E9"
                content.contains("Eng. Software", true) -> "#FFE0B2"
                content.contains("D.A.M", true) -> "#FCE4EC"

                // Cor default
                else -> "#9575CD"
            }
        } else {
            "#FFFFFF" // Branco para células vazias
        }
    }

    /**
     * DATA CLASSES - Atualizadas para corresponder à estrutura da API
     */

    // TimeSlot - representa um bloco de horário
    data class TimeSlot(
        val time: String,
        val value: String,
        val _id: String? = null
    ) : java.io.Serializable

    // Dia - representa um dia da semana com seus slots
    data class Dia(
        val nome: String,
        val timeSlots: List<TimeSlot>,
        val _id: String? = null
    ) : java.io.Serializable

    // ScheduleData - contém a lista de dias
    data class ScheduleData(
        val dias: List<Dia>
    ) : java.io.Serializable

    // ScheduleResponse - resposta completa da API
    data class ScheduleResponse(
        val sala: String,
        val turma: String,
        val curso: String,
        val ano: String,
        val horario: ScheduleData
    ) : java.io.Serializable
}