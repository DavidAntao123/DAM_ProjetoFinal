package pt.ipt.dam.projfinal

// Imports para cores e bordas das células -> tabela
import android.graphics.Color
import android.graphics.drawable.GradientDrawable

import android.os.Bundle
import android.util.Log
import android.view.Gravity
// Imports de componentes
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
// Biblioteca usada para converter JSON -> objetos Kotlin
import com.google.gson.Gson
import java.io.FileNotFoundException

/**
 * Activity responsável por apresentar o horário correspondente a uma sala,
 * após a leitura do QR Code.
 * Recebe um nome de ficheiro -> o url , enviado por Intent
 * Carrega um ficheiro JSON da pasta assets
 * Mostra o horário numa tabela TableLayout
 * Pinta as células com cores diferentes consoante a disciplina
 * Possui botão Voltar para fechar este ecrã
 */
class lerqrcode : AppCompatActivity() {

    // Tabela onde o horário será desenhado
    private lateinit var tableLayout: TableLayout

    // Botão voltar -> id btnvoltarqrcode
    private lateinit var btnVoltarQrcode: Button


    /**
     * memoriaCores
     * Guarda a última cor utilizada em cada coluna -> dia,
     * para manter a continuacao visual quando existem aulas seguidas
     */
    val memoriaCores = mutableMapOf(
        "Segunda" to "#FFFFFF",
        "Terça" to "#FFFFFF",
        "Quarta" to "#FFFFFF",
        "Quinta" to "#FFFFFF",
        "Sexta" to "#FFFFFF",
        "Sábado" to "#FFFFFF"
    )

    /**
     * Método executado quando a Activity é criada.
     * Inicializa componentes e carrega o horário.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Liga esta activity ao XML activity_lerqrcode.xml
        setContentView(R.layout.activity_lerqrcode)

        //Inicializa os componentes do layout
        tableLayout = findViewById(R.id.tableLayout)
        btnVoltarQrcode = findViewById(R.id.btnvoltarqrcode)
        /**
         * Recebe o url enviado pelo ecrã anterior -> QR Code
         */
        val urlRecebida = intent.getStringExtra("url")

        // Se receber um nome válido -> carrega o horário
        // Senão -> mostra tabela vazia
        if (!urlRecebida.isNullOrEmpty()) {
            loadSchedule(urlRecebida)
        } else {
            generateEmptyTable()
        }
        // Botão voltar: fecha este ecrã e volta ao anterior
        btnVoltarQrcode.setOnClickListener {
            finish()
        }
    }

    /**
     * loadSchedule()
     * Lê um ficheiro JSON na pasta assets e gera assim a tabela do horário
     *
     * O filename pode vir com ou sem ".json".
     */
    private fun loadSchedule(filename: String) {
        try {
            // Garantir que o nome do ficheiro termina em .json
            val finalFilename = if (filename.endsWith(".json")) filename else "$filename.json"
            // Le ficheiro JSON dos assets
            val jsonString = assets.open(finalFilename)
                .bufferedReader()
                .use { it.readText() }

            // Converte JSON -> objeto Kotlin com Gson
            val gson = Gson()
            val horarioData = gson.fromJson(jsonString, ScheduleResponse::class.java)
            // Limpa tabela antiga e desenha nova tabela
            tableLayout.removeAllViews()
            generateTableFromJson(horarioData)
            // Mensagem de sucesso
            Toast.makeText(this, "Carregado: $finalFilename", Toast.LENGTH_SHORT).show()

        } catch (e: FileNotFoundException) {
            // Caso o ficheiro não exista
            Toast.makeText(this, "Ficheiro não encontrado: $filename", Toast.LENGTH_SHORT).show()
            generateEmptyTable() // Mostra algo se falhar
        } catch (e: Exception) {
            // Outros erros possíveis de acontecer
            Log.e("ERRO_JSON", "Erro: ${e.message}")
            generateEmptyTable()
        }
    }


    /**
     * Cria a tabela do horário a partir do JSON.
     */

    private fun generateTableFromJson(horarioData: ScheduleResponse) {
        // Limpa a tabela antes de desenhar
        tableLayout.removeAllViews()
        // Dias e blocos horários vindos do JSON
        val dias = horarioData.horario.dias
        val timeSlots = horarioData.horario.timeSlots
        // Criar cabeçalho
        val header = TableRow(this)
        // Primeira coluna: "Time"
        header.addView(createCell("Time", "#C5CAE9", true))
        // Colunas seguintes->dias da semana
        for (dia in dias) {
            header.addView(createCell(dia, "#C5CAE9", true))
        }
        tableLayout.addView(header)

        // Criar linhas do horário
        for (timeSlot in timeSlots) {

            val row = TableRow(this)

            // Coluna da hora
            row.addView(createCell(timeSlot.time, "#E8EAF6", false))


            listOf(
                "Segunda" to timeSlot.Segunda,
                "Terca" to timeSlot.Terca,
                "Quarta" to timeSlot.Quarta,
                "Quinta" to timeSlot.Quinta,
                "Sexta" to timeSlot.Sexta,
                "Sabado" to timeSlot.Sabado
            ).forEach { (dia, conteudo) ->

                val corAtual = getCellColor(conteudo)

                if (conteudo.isEmpty()) {
                    memoriaCores[dia] = "#FFFFFF"
                } else if (corAtual != "#9575CD" && corAtual != "#FFFFFF") {
                    memoriaCores[dia] = corAtual
                }

                val corParaPintar = memoriaCores[dia] ?: "#FFFFFF"
                row.addView(createCell(conteudo, corParaPintar, false))
            }

            tableLayout.addView(row)
        }
    }


    /**
     * generateEmptyTable()
     * Cria uma tabela vazia apenas com o cabeçalho,
     * usada quando não foi possível carregar um JSON.
     * Gera tabela vazia em caso de erro
     */
    private fun generateEmptyTable() {

        tableLayout.removeAllViews()

        val dias = listOf("Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sabado")

        // cabecalho
        val header = TableRow(this)
        header.addView(createCell("Horário", "#C5CAE9", true))
        for (dia in dias) {
            header.addView(createCell(dia, "#C5CAE9", true))
        }
        tableLayout.addView(header)

        Toast.makeText(this, getString(R.string.tabela_vazia), Toast.LENGTH_SHORT).show()
    }

    /**
     * createCell()
     * Cria uma célula da tabela usando um TextView
     *Texto centrado
     *Padding
     *Fundo colorido
     * Borda preta
     */
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

            // Fundo com cor + borda
            val shape = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE

                // Cor de fundo
                try {
                    setColor(Color.parseColor(color))
                } catch (e: Exception) {
                    setColor(Color.WHITE)
                }

                //Borda da célula
                setStroke(2, Color.BLACK)
            }

            //aplica o fundo
            background = shape
        }
    }


    /**
     * Limpa todas as linhas da tabela
     */
    private fun clearTable() {
        tableLayout.removeAllViews()
        Toast.makeText(this, getString(R.string.tabela_limpa), Toast.LENGTH_SHORT).show()
    }


    /**
     * getCellColor()
     * Define a cor da célula conforme o nome da disciplina
     * Se a disciplina não for reconhecida usa cor default
     */
    private fun getCellColor(content: String): String {
        return if (content.isNotEmpty()) {
            // Diferentes cores para cada cadeira
            when {
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

                content.equals("D.P.C", true) -> "#E8F5E9"
                content.contains("M.E", true) -> "#D1C4E9"
                content.contains("M.I.T.E.E.R", true) -> "#FFE0B2"
                content.contains("I.E.I", true) -> "#FCE4EC"
                content.contains("Projeto", true) -> "#FFF9C4"
                content.contains("O.T.E", true) -> "#DCEDC8"

                content.contains("Matematica", true) -> "#E1BEE7"
                content.contains("I.E.T", true) -> "#B3E5FC"
                content.contains("I.P.R.P", true) -> "#D1C4E9"
                content.contains("H.S.T", true) -> "#C8E6C9"
                content.contains("E.S", true) -> "#B2EBF2"

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

                else -> "#F3E5F5" // Cor default
            }
        } else {
            "#FFFFFF"
        }
    }

    // DATA CLASSES ->modelo do JSON
    /**
     * TimeSlot
     * Representa uma linha do horário -> hora e aulas/dados para cada dia
     */
    data class TimeSlot(
        val time: String,
        val Segunda: String,
        val Terca: String,
        val Quarta: String,
        val Quinta: String,
        val Sexta: String,
        val Sabado: String
    )

    /**
     * ScheduleData
     * Guarda a lista de dias e lista das linhas ->TimeSlots
     */
    data class ScheduleData(
        val dias: List<String>,
        val timeSlots: List<TimeSlot>
    )

    /**
     * ScheduleResponse
     * Estrutura principal do JSON horario -> ScheduleData
     */
    data class ScheduleResponse(
        val horario: ScheduleData
    )

}





