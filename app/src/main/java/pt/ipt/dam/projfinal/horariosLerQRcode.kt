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
import com.google.gson.Gson
import java.io.FileNotFoundException

class horariosLerQRcode : AppCompatActivity() {

    private lateinit var tableLayout: TableLayout

    private lateinit var btnClear: Button

    val memoriaCores = mutableMapOf(
        "Segunda" to "#FFFFFF",
        "Terça"   to "#FFFFFF",
        "Quarta"  to "#FFFFFF",
        "Quinta"  to "#FFFFFF",
        "Sexta"   to "#FFFFFF",
        "Sábado"  to "#FFFFFF"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horarios)

        // Initialize views
        tableLayout = findViewById(R.id.tableLayout)

        btnClear = findViewById(R.id.btnClear)

        loadSchedule("temphorario.json")

        btnClear.setOnClickListener {
            clearTable()
        }

        // Generate empty table initially
        generateEmptyTable()
    }

    // Carrega o horario do .json
    private fun loadSchedule(filename: String) {
        try {
            val jsonString = assets.open(filename)
                .bufferedReader()
                .use { it.readText() }

            val gson = Gson()
            val horarioData = gson.fromJson(jsonString, ScheduleResponse::class.java)

            // Tabela com o horario
            generateTableFromJson(horarioData)

            // Show toast message
            val horarioName = when (filename) {
                "horario1.json" -> "Horário 1"
                "horario2.json" -> "Horário 2"
                else -> "horario"
            }
            Toast.makeText(this, "Carregado: $horarioName", Toast.LENGTH_SHORT).show()

        } catch (e: FileNotFoundException) {
            Toast.makeText(this, "Ficheiro nao encontrado: $filename", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } catch (e: Exception) {
            Toast.makeText(this, "Erro a carregar horario", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    // Function to generate table from JSON data
    private fun generateTableFromJson(horarioData: ScheduleResponse) {
        tableLayout.removeAllViews()

        val dias = horarioData.horario.dias
        val timeSlots = horarioData.horario.timeSlots



        // Create header row
        val header = TableRow(this)
        header.addView(createCell("Time", "#C5CAE9", true))
        for (dia in dias) {
            header.addView(createCell(dia, "#C5CAE9", true))
        }
        tableLayout.addView(header)

        // Criar as rows com o json
        for (timeSlot in timeSlots) {

            val row = TableRow(this)

            // Add time cell
            row.addView(createCell(timeSlot.time, "#E8EAF6", false))
            // Add dia cells with subject names

            var corAtual = getCellColor(timeSlot.Segunda)

            if (timeSlot.Segunda.isEmpty()) {
                memoriaCores["Segunda"] = "#FFFFFF"
            } else {
                if (corAtual != "#9575CD" && corAtual != "#FFFFFF") {
                    memoriaCores["Segunda"] = corAtual
                }
            }
            var corParaPintar = memoriaCores["Segunda"] ?: "#FFFFFF"

            row.addView(createCell(timeSlot.Segunda, corParaPintar, false))

            //---------------------------
            //---------------------------
            //---------------------------

            corAtual = getCellColor(timeSlot.Terca)

            if (timeSlot.Terca.isEmpty()) {
                memoriaCores["Terca"] = "#FFFFFF"
            } else {
                if (corAtual != "#9575CD" && corAtual != "#FFFFFF") {
                    memoriaCores["Terca"] = corAtual
                }
            }
            corParaPintar = memoriaCores["Terca"] ?: "#FFFFFF"

            row.addView(createCell(timeSlot.Terca, corParaPintar, false))

            //---------------------------
            //---------------------------
            //---------------------------

            corAtual = getCellColor(timeSlot.Quarta)

            if (timeSlot.Quarta.isEmpty()) {
                memoriaCores["Quarta"] = "#FFFFFF"
            } else {
                if (corAtual != "#9575CD" && corAtual != "#FFFFFF") {
                    memoriaCores["Quarta"] = corAtual
                }
            }
            corParaPintar = memoriaCores["Quarta"] ?: "#FFFFFF"

            row.addView(createCell(timeSlot.Quarta, corParaPintar, false))

            //---------------------------
            //---------------------------
            //---------------------------
            corAtual = getCellColor(timeSlot.Quinta)

            if (timeSlot.Quinta.isEmpty()) {
                memoriaCores["Quinta"] = "#FFFFFF"
            } else {
                if (corAtual != "#9575CD" && corAtual != "#FFFFFF") {
                    memoriaCores["Quinta"] = corAtual
                }
            }
            corParaPintar = memoriaCores["Quinta"] ?: "#FFFFFF"
            row.addView(createCell(timeSlot.Quinta, corParaPintar, false))

            //---------------------------
            //---------------------------
            //---------------------------

            corAtual = getCellColor(timeSlot.Sexta)

            if (timeSlot.Sexta.isEmpty()) {
                memoriaCores["Sexta"] = "#FFFFFF"
            } else {
                if (corAtual != "#9575CD" && corAtual != "#FFFFFF") {
                    memoriaCores["Sexta"] = corAtual
                }
            }
            corParaPintar = memoriaCores["Sexta"] ?: "#FFFFFF"
            row.addView(createCell(timeSlot.Sexta, corParaPintar, false))

            //---------------------------
            //---------------------------
            //---------------------------

            corAtual = getCellColor(timeSlot.Sabado)


            if (timeSlot.Sabado.isEmpty()) {
                memoriaCores["Sabado"] = "#FFFFFF"
            } else {
                if (corAtual != "#9575CD" && corAtual != "#FFFFFF") {
                    memoriaCores["Sabado"] = corAtual
                }
            }
            corParaPintar = memoriaCores["Sabado"] ?: "#FFFFFF"
            row.addView(createCell(timeSlot.Sabado, corParaPintar, false))


            tableLayout.addView(row)
        }
    }

    // Funcao de gerar tabela limpa
    private fun generateEmptyTable() {
        tableLayout.removeAllViews()

        val dias = listOf("Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sabado")

        // Header
        val header = TableRow(this)
        header.addView(createCell("Horário", "#C5CAE9", true))
        for (dia in dias) {
            header.addView(createCell(dia, "#C5CAE9", true))
        }
        tableLayout.addView(header)

        Toast.makeText(this, "Empty table generated", Toast.LENGTH_SHORT).show()
    }

    // Funcao de clear
    private fun clearTable() {
        tableLayout.removeAllViews()
        Toast.makeText(this, "Table cleared", Toast.LENGTH_SHORT).show()
    }

    // Funcao de criar cell
    private fun createCell(text: String, color: String, isHeader: Boolean): TextView {
        return TextView(this).apply {

            this.text = text
            setPadding(32, 32, 32, 32)
            gravity = Gravity.CENTER
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )

            // Create a drawable programmatically to handle both Color and Border
            val shape = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE

                // 1. Set the background color (the 'solid' part)
                try {
                    setColor(Color.parseColor(color))
                } catch (e: Exception) {
                    setColor(Color.WHITE)
                }

                // 2. Set the border (the 'stroke' part)
                // Parameters: (width in pixels, color)
                setStroke(2, Color.BLACK)
            }

            // Apply the shape as the background
            background = shape
        }
    }

    // Helper function to get cell color based on content
    private fun getCellColor(content: String): String {
        return if (content.isNotEmpty()) {
            // Assign different colors based on subject
            when {
                content.contains("Álgebra (TP)", true) -> "#ff1f00"
                content.contains("Analise Matematica I", true) -> "#00ff92"
                content.contains("Sistemas Digitais", true) -> "#b300ff"
                content.contains("Introdução", true) -> "#ff6f00"
                content.contains("I.E.T", true) -> "#009eff"
                content.contains("I.P.R.P", true) -> "#9B5DE5"
                content.contains("I.R.L", true) -> "#ff1f00"
                content.contains("S.I", true) -> "#00ff92"
                content.contains("C.D", true) -> "#b300ff"
                content.contains("Eng. Software", true) -> "#ff6f00"
                content.contains("D.A.M", true) -> "#009eff"

                else -> "#9575CD" // Cor default
            }
        } else {
            "#FFFFFF" // White for empty cells
        }
    }


    // Data classes
    data class TimeSlot(
        val time: String,
        val Segunda: String,
        val Terca: String,
        val Quarta: String,
        val Quinta: String,
        val Sexta: String,
        val Sabado: String
    )

    data class ScheduleData(
        val dias: List<String>,
        val timeSlots: List<TimeSlot>
    )

    data class ScheduleResponse(
        val horario: ScheduleData
    )

}


