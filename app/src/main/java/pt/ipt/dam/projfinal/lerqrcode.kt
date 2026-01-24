package pt.ipt.dam.projfinal

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import java.io.FileNotFoundException


class lerqrcode : AppCompatActivity() {

    private lateinit var tableLayout: TableLayout

    private lateinit var btnClear: Button
    private lateinit var btnVoltarQrcode: Button


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
        setContentView(R.layout.activity_lerqrcode)

        tableLayout = findViewById(R.id.tableLayout)
        btnVoltarQrcode = findViewById(R.id.btnvoltarqrcode)


        val urlRecebida = intent.getStringExtra("url")

        if (!urlRecebida.isNullOrEmpty()) {
            loadSchedule(urlRecebida)
        } else {
            generateEmptyTable()
        }

        btnVoltarQrcode.setOnClickListener {
            finish()
        }
    }
    // Carrega o horario do .json
    private fun loadSchedule(filename: String) {
        try {
            val finalFilename = if (filename.endsWith(".json")) filename else "$filename.json"

            val jsonString = assets.open(finalFilename)
                .bufferedReader()
                .use { it.readText() }

            val gson = Gson()
            val horarioData = gson.fromJson(jsonString, ScheduleResponse::class.java)

            tableLayout.removeAllViews()
            generateTableFromJson(horarioData)

            Toast.makeText(this, "Carregado: $finalFilename", Toast.LENGTH_SHORT).show()

        } catch (e: FileNotFoundException) {
            Toast.makeText(this, "Ficheiro não encontrado: $filename", Toast.LENGTH_SHORT).show()
            generateEmptyTable() // Mostra algo se falhar
        } catch (e: Exception) {
            Log.e("ERRO_JSON", "Erro: ${e.message}")
            generateEmptyTable()
        }
    }
    // Function to generate table from JSON data
    private fun generateTableFromJson(horarioData: ScheduleResponse) {
        tableLayout.removeAllViews()

        val dias = horarioData.horario.dias
        val timeSlots = horarioData.horario.timeSlots



        // Create header row
        val header = TableRow(this)
        header.addView(createCell("Horario", "#C5CAE9", true))
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
    // Funcao de criar cell
    private fun createCell(text: String, color: String, isHeader: Boolean): TextView {
        return TextView(this).apply {

            this.text = text
            this.setTextColor(Color.BLACK)
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

    // Funcao de clear
    private fun clearTable() {
        tableLayout.removeAllViews()
        Toast.makeText(this, "Table cleared", Toast.LENGTH_SHORT).show()
    }


    // Helper function to get cell color based on content
// Helper function to get cell color based on content
    private fun getCellColor(content: String): String {
        return if (content.isNotEmpty()) {
            // Assign different colors based on subject (Pastel tones for black text readability)
            when {
                // Engenharia / Tecnologia (Tons Vermelhos e Rosas claros)
                content.equals("Tecnologia", true) -> "#FFCDD2"
                content.equals("E.P", true) -> "#F8BBD0"
                content.equals("R.M", true) -> "#FFEBEE"
                content.equals("A.M", true) -> "#C8E6C9"
                content.equals("S.E.T.R", true) -> "#E1BEE7"
                content.equals("M.E.I", true) -> "#BBDEFB"
                content.equals("Auto. II", true) -> "#D1C4E9"
                content.equals("R.I", true) -> "#F48FB1"
                content.equals("C.A.T", true) -> "#FFAB91"
                content.equals("S.T", true) -> "#F06292"
                content.equals("S.S", true) -> "#B39DDB"
                content.equals("A.Circ.", true) -> "#B2DFDB"
                content.equals("Controlo D.", true) -> "#E0F2F1"
                content.equals("S.D.C", true) -> "#DCEDC8"
                content.equals("R.H.C", true) -> "#FFCCBC"
                content.equals("F.S", true) -> "#EF9A9A"
                content.equals("R.E.I", true) -> "#9FA8DA"
                content.equals("C.E", true) -> "#C5E1A5"
                content.equals("S.A.I", true) -> "#FFCC80"
                content.equals("E.E", true) -> "#FFE082"
                content.equals("MicroC.", true) -> "#CE93D8"
                content.equals("Eletronica II.", true) -> "#80CBC4"
                content.equals("T.S.A", true) -> "#FFD54F"
                content.equals("Mecatronica", true) -> "#BCAAA4"
                content.contains("Eletronica II", true) -> "#80DEEA"
                content.contains("I.R", true) -> "#FFCCBC"

                // Projetos e Outros (Tons Laranja e Amarelo claros)
                content.equals("D.P.C", true) -> "#E8F5E9"
                content.contains("M.E", true) -> "#D1C4E9"
                content.contains("M.I.T.E.E.R", true) -> "#FFE0B2"
                content.contains("I.E.I", true) -> "#FCE4EC"
                content.contains("Projeto", true) -> "#FFF9C4"
                content.contains("O.T.E", true) -> "#DCEDC8"

                // Matemática e Teoria (Tons Roxos e Azuis claros)
                content.contains("Matematica", true) -> "#E1BEE7"
                content.contains("I.E.T", true) -> "#B3E5FC"
                content.contains("I.P.R.P", true) -> "#D1C4E9"
                content.contains("H.S.T", true) -> "#C8E6C9"
                content.contains("E.S", true) -> "#B2EBF2"

                // TESP (Tons Terra e Cinzas suaves)
                content.equals("A.R.S.I", true) -> "#D7CCC8"
                content.equals("P.I 2", true) -> "#F5F5F5"
                content.equals("P.M", true) -> "#FFF59D"
                content.equals("S.E.G.P", true) -> "#CFD8DC"
                content.equals("G.S.I", true) -> "#F8BBD0"

                // 1º Ano
                content.contains("Álgebra (TP)", true) -> "#FFECB3"
                content.contains("Analise Matematica I", true) -> "#C8E6C9"
                content.contains("S.D", true) -> "#E1BEE7"
                content.contains("I.E.T", true) -> "#B3E5FC"
                content.contains("I.P.R.P", true) -> "#D1C4E9"

                // 2º Ano
                content.contains("Redes I", true) -> "#C5E1A5"
                content.contains("A.C", true) -> "#B2EBF2"
                content.contains("I.W", true) -> "#E1F5FE"

                // 3º Ano
                content.contains("I.R.L", true) -> "#FFCCBC"
                content.equals("S.I", true) -> "#B9F6CA"
                content.contains("C.D", true) -> "#D1C4E9"
                content.contains("Eng. Software", true) -> "#FFE0B2"
                content.contains("D.A.M", true) -> "#FCE4EC"

                else -> "#F3E5F5" // Cor default suave (Lavanda)
            }
        } else {
            "#FFFFFF" // Branco para células vazias
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


