package pt.ipt.dam.projfinal

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
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

    private lateinit var tableLayout: TableLayout
    private lateinit var btnClear: Button
    private lateinit var btnVoltarHorario: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horarios)

        // Inicialização dos componentes
        tableLayout = findViewById(R.id.tableLayout)
        btnVoltarHorario = findViewById(R.id.btnVoltarHorario)

        // Receber dados da Intent
        val horarioData = intent.getSerializableExtra("horario_data") as? ScheduleData
        val turma = intent.getStringExtra("turma") ?: ""
        val curso = intent.getStringExtra("curso") ?: ""
        val ano = intent.getStringExtra("ano") ?: ""
        val sala = intent.getStringExtra("sala") ?: ""

        if (horarioData != null) {
            val fullResponse = ScheduleResponse(
                sala = sala,
                turma = turma,
                curso = curso,
                ano = ano,
                horario = horarioData
            )
            generateTableFromJson(fullResponse)

        } else {
            generateEmptyTable()
            Toast.makeText(this, getString(R.string.horarios_ErroHorario), Toast.LENGTH_SHORT).show()
        }

        btnVoltarHorario.setOnClickListener { finish() }
    }

    /**
     * Cria a tabela do horário a partir dos dados recebidos,
     * usando a cor definida em cada slot da API.
     */
    private fun generateTableFromJson(horarioData: ScheduleResponse) {
        tableLayout.removeAllViews()

        val dias = horarioData.horario.dias

        // Criar cabeçalho
        val header = TableRow(this)
        header.addView(createCell(getString(R.string.horariosTitulo), "#C5CAE9", true))
        for (dia in dias) {
            header.addView(createCell(dia.nome, "#C5CAE9", true))
        }
        tableLayout.addView(header)

        if (dias.isNotEmpty() && dias.first().timeSlots.isNotEmpty()) {

            // Lista de todos os horários (usando o primeiro dia como referência)
            val todosTimeSlots = dias.first().timeSlots.map { it.time }

            todosTimeSlots.forEach { timeSlotTime ->
                val row = TableRow(this)

                // Primeira coluna: A Hora (cinza claro)
                row.addView(createCell(timeSlotTime, "#E8EAF6", false))

                // Colunas dos dias
                dias.forEach { dia ->
                    val slot = dia.timeSlots.find { it.time == timeSlotTime }
                    val conteudo = slot?.value ?: ""

                    // BUSCA A COR DIRETAMENTE DO OBJETO TIMESLOT
                    // Se não houver cor ou slot, assume Branco (#FFFFFF)
                    val corParaPintar = if (slot?.cor != null && slot.cor.isNotEmpty()) {
                        slot.cor
                    } else {
                        "#FFFFFF"
                    }

                    row.addView(createCell(conteudo, corParaPintar, false))
                }
                tableLayout.addView(row)
            }
        }
    }

    private fun generateEmptyTable() {
        tableLayout.removeAllViews()
        val diasPadrao = listOf("Segunda", "Terca", "Quarta", "Quinta", "Sexta", "Sabado")
        val header = TableRow(this)
        header.addView(createCell(getString(R.string.horariosTitulo), "#C5CAE9", true))
        for (dia in diasPadrao) {
            header.addView(createCell(dia, "#C5CAE9", true))
        }
        tableLayout.addView(header)
    }


    private fun createCell(text: String, color: String, isHeader: Boolean): TextView {
        return TextView(this).apply {
            this.text = text
            setPadding(32, 32, 32, 32)
            this.setTextColor(Color.BLACK)
            gravity = Gravity.CENTER

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
     * DATA CLASSES - Mapeamento exato da estrutura da API
     */

    data class TimeSlot(
        val time: String,
        val value: String,
        val cor: String? = null, // Campo de cor da API
        val _id: Any? = null
    ) : java.io.Serializable

    data class Dia(
        val nome: String,
        val timeSlots: List<TimeSlot>,
        val _id: Any? = null
    ) : java.io.Serializable

    data class ScheduleData(
        val dias: List<Dia>
    ) : java.io.Serializable

    data class ScheduleResponse(
        val sala: String,
        val turma: String,
        val curso: String,
        val ano: String,
        val horario: ScheduleData
    ) : java.io.Serializable
}