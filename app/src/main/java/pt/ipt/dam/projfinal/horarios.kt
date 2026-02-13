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
// Biblioteca Gson para ler JSON
import com.google.gson.Gson
import java.io.FileNotFoundException

/**

 * Esta Activity é responsável por mostrar horários em formato de tabela
 * Botão "Horário 1" -> carrega horario1.json
 * Botão "Horário 2" -> carrega horario2.json
 * Botão "Limpar" -> remove tudo da tabela
 * Tabela com cabeçalho e as células sao coloridas por disciplina
 */
class horarios : AppCompatActivity() {

    // TableLayout onde o horário será desenhado -> linha a linha
    private lateinit var tableLayout: TableLayout

    // Botões do ecrã
    private lateinit var btnHorario1: Button
    private lateinit var btnHorario2: Button
    private lateinit var btnClear: Button
    private lateinit var btnVoltarHorario: Button

    /**
     * Guarda a última cor usada em cada coluna -> dia da semana,
     * para manter consistência quando a aula se repete em horários seguidos.
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
     * Inicializa componentes e cria tabela vazia inicial.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Liga esta Activity ao layout XML -> activity_horarios.xml
        setContentView(R.layout.activity_horarios)

        // Inicialização dos componentes do layout através dos IDs
        tableLayout = findViewById(R.id.tableLayout)
        btnHorario1 = findViewById(R.id.btnHorario1)
        btnHorario2 = findViewById(R.id.btnHorario2)
        btnClear = findViewById(R.id.btnClear)
        btnVoltarHorario = findViewById(R.id.btnVoltarHorario)

        /**
         * Botão Horário 1
         * Carrega horario1.json da pasta assets
         */
        btnHorario1.setOnClickListener {
            loadSchedule("horario1.json")
        }
        /**
         * Botão Horário 2
         * Carrega horario2.json da pasta assets
         */
        btnHorario2.setOnClickListener {
            loadSchedule("horario2.json")
        }


        /**
         * Botão Limpar
         * Remove todas as linhas da tabela
         */
        btnClear.setOnClickListener {
            clearTable()
        }
        /**
         * Botão Voltar
         * Fecha esta Activity
         */
        btnVoltarHorario.setOnClickListener {
            finish()
        }

        // No início, cria a tabela vazia com cabeçalho -> antes de carregar horários
        generateEmptyTable()
    }

    /**
     * loadSchedule()
     * Lê um ficheiro JSON a partir da pasta assets,
     * converte para objetos Kotlin com Gson,
     * e desenha a tabela com os dados
     */
    private fun loadSchedule(filename: String) {
        try {
            // Le o ficheiro JSON (assets/filename)
            val jsonString = assets.open(filename)
                .bufferedReader()
                .use { it.readText() }

            // Converter JSON em objetos Kotlin usando Gson
            val gson = Gson()
            val horarioData = gson.fromJson(jsonString, ScheduleResponse::class.java)

            // Tabela com o horario
            generateTableFromJson(horarioData)

            // Mostra a mensagem no ecrã com o nome do horário que é carregado
            val horarioName = when (filename) {
                "horario1.json" -> getString(R.string.horario_1ano)
                "horario2.json" -> getString(R.string.horario_3ano)
                else -> getString(R.string.horariosTitulo)
            }

            Toast.makeText(
                this,
                getString(R.string.horario_carregado, horarioName),
                Toast.LENGTH_SHORT
            ).show()

        } catch (e: FileNotFoundException) {
            Toast.makeText(
                this,
                getString(R.string.ficheiro_nao_encontrado, filename),
                Toast.LENGTH_SHORT
            ).show()

        } catch (e: Exception) {

            Toast.makeText(this, getString(R.string.erro_carregar_horario), Toast.LENGTH_SHORT)
                .show()
        }
    }


    /**
     * generateTableFromJson()
     *  Cria a tabela do horário a partir dos dados JSON.
     * Recebe os dados já convertidos do JSON e depois
     * Cria o cabeçalho ->dias da semana
     * Cria as linhas de horários -> timeSlots
     * Pinta depois cada célula com base na disciplina
     */

    private fun generateTableFromJson(horarioData: ScheduleResponse) {
        // Limpa o conteúdo antigo da tabela
        tableLayout.removeAllViews()

        // Lista de dias e lista de blocos horários -> time slots
        val dias = horarioData.horario.dias
        val timeSlots = horarioData.horario.timeSlots


        // Criar cabeçalho
        val header = TableRow(this)
        // Primeira célula do cabeçalho -> no nosso caso a hora
        header.addView(createCell(getString(R.string.horariosTitulo), "#C5CAE9", true))
        // Células do cabeçalho para cada dia
        for (dia in dias) {
            header.addView(createCell(dia, "#C5CAE9", true))
        }
        // Adicionar cabeçalho à tabela
        tableLayout.addView(header)

        // Cria as linhas da tabela
        for (timeSlot in timeSlots) {

            val row = TableRow(this)

            // Coluna da hora
            row.addView(createCell(timeSlot.time, "#E8EAF6", false))

            /**
             * Percorre todos os dias da semana e, para cada célula:
             * obtém o conteúdo (disciplina)
             * calcula a cor correspondente
             * guarda a cor para manter continuidade visual
             * cria a célula e adiciona à linha da tabela
             *
             * se a célula estiver vazia → branco
             * se for uma cadeira → guardar cor
             */
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
     * Cria uma tabela vazia com cabeçalho -> dias da semana,
     * usada quando ainda não foi carregado nenhum horário
     */
    private fun generateEmptyTable() {
        tableLayout.removeAllViews()

        val dias = listOf("Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sabado")

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
     * texto centrado
     * padding para melhorar a leitura
     * o fundo colorido
     * a borda preta para estilo da grelha
     */
    private fun createCell(text: String, color: String, isHeader: Boolean): TextView {
        return TextView(this).apply {
            // Texto da célula
            this.text = text
            // Espaçamento interno
            setPadding(32, 32, 32, 32)
            //Cor das letras
            this.setTextColor(Color.BLACK)
            // Texto centrado dentro da célula
            gravity = Gravity.CENTER
            // Tamanho das células -> WRAP_CONTENT para se ajustar
            layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )

            // Criar o fundo da célula com cor e borda
            val shape = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE

                /// Cor de fundo
                try {
                    setColor(Color.parseColor(color))
                } catch (e: Exception) {
                    setColor(Color.WHITE)
                }

                // Borda da célula
                setStroke(2, Color.BLACK)
            }

            // Aplicar fundo na célula
            background = shape
        }
    }


    /**
     * Define cor da célula conforme a cadeira
     */
    private fun getCellColor(content: String): String {
        return if (content.isNotEmpty()) {
            when {
                //Outro

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
            // Branco para células vazias
            "#FFFFFF"
        }
    }


    /**
     * TimeSlot
     * Representa uma linha do horário -> hora e conteudo de cada dia
     * MODELOS JSON
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
     * Guarda a lista de dias e lista de timeSlots -> linhas
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
