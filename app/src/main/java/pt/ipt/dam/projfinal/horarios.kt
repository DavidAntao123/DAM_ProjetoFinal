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
 * Horarios
 *
 * Activity responsável por apresentar o horário em formato de tabela.
 *
 * Recebe os dados enviados pela Activity Cam através de Intent
 * (resulta da leitura do QR Code e da consulta à API)
 *
 * A tabela é construída dinamicamente com base nos dados recebidos.
 */
class horarios : AppCompatActivity() {
    // Tabela onde o horário será desenhado
    private lateinit var tableLayout: TableLayout
    private lateinit var btnVoltarHorario: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horarios)

        // Inicialização dos componentes do layout
        tableLayout = findViewById(R.id.tableLayout)
        btnVoltarHorario = findViewById(R.id.btnVoltarHorario)

        /**
         * Recebe dados enviados pela Activity anterior (Cam)
         */
        val horarioData = intent.getSerializableExtra("horario_data") as? ScheduleData
        val turma = intent.getStringExtra("turma") ?: ""
        val curso = intent.getStringExtra("curso") ?: ""
        val ano = intent.getStringExtra("ano") ?: ""
        val sala = intent.getStringExtra("sala") ?: ""

        // Se existirem dados válidos, cria a tabela
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
            // Caso falhe a receção dos dados
            generateEmptyTable()
            Toast.makeText(this, getString(R.string.horarios_ErroHorario), Toast.LENGTH_SHORT).show()
        }
        // Botão Voltar fecha esta Activity
        btnVoltarHorario.setOnClickListener { finish() }
    }

    /**
     * Gera a tabela completa do horário a partir dos dados recebidos da API
     *
     * Cada célula recebe a cor diretamente do servidor.
     */
    private fun generateTableFromJson(horarioData: ScheduleResponse) {
        // Limpa tabela anterior
        tableLayout.removeAllViews()

        val dias = horarioData.horario.dias

        // Criação do cabeçalho da tabela
        val header = TableRow(this)
        header.addView(createCell(getString(R.string.horariosTitulo), getString(R.string.Lavender_Blue), true))
        for (dia in dias) {
            header.addView(createCell(dia.nome, getString(R.string.Lavender_Blue), true))
        }
        tableLayout.addView(header)

        // Usa o primeiro dia como referência para obter todos os horários
        if (dias.isNotEmpty() && dias.first().timeSlots.isNotEmpty()) {

            // Lista de todos os horários (usando o primeiro dia como referência)
            val todosTimeSlots = dias.first().timeSlots.map { it.time }

            todosTimeSlots.forEach { timeSlotTime ->
                val row = TableRow(this)

                // Primeira coluna: A Hora
                row.addView(createCell(timeSlotTime, getString(R.color.branco_cinzento), false))

                // Preenche cada dia
                dias.forEach { dia ->
                    val slot = dia.timeSlots.find { it.time == timeSlotTime }
                    val conteudo = slot?.value ?: ""

                    // Usa a cor enviada pela API (ou branco por defeito)
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

    /**
     * Cria tabela vazia caso não existam dados
     */

    private fun generateEmptyTable() {
        tableLayout.removeAllViews()
        val diasPadrao = listOf("Segunda", "Terca", "Quarta", "Quinta", "Sexta", "Sabado")
        val header = TableRow(this)
        header.addView(createCell(getString(R.string.horariosTitulo),getString(R.string.Lavender_Blue) , true))
        for (dia in diasPadrao) {
            header.addView(createCell(dia, getString(R.string.Lavender_Blue), true))
        }
        tableLayout.addView(header)
    }

    /**
     * Cria uma célula da tabela com texto, cor de fundo e borda
     */
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

    // ---------------- MODELOS DE DADOS ----------------
    // Representam exatamente a estrutura devolvida pela API

    /**
     * Representa um bloco horário
     */

    data class TimeSlot(
        val time: String,
        val value: String,
        val cor: String? = null,
        val _id: Any? = null
    ) : java.io.Serializable

    /**
     * Representa um dia da semana
     */
    data class Dia(
        val nome: String,
        val timeSlots: List<TimeSlot>,
        val _id: Any? = null
    ) : java.io.Serializable

    /**
     * Contém todos os dias do horário
     */
    data class ScheduleData(
        val dias: List<Dia>
    ) : java.io.Serializable

    /**
     * Estrutura completa recebida da API
     */
    data class ScheduleResponse(
        val sala: String,
        val turma: String,
        val curso: String,
        val ano: String,
        val horario: ScheduleData
    ) : java.io.Serializable
}