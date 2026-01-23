package pt.ipt.dam.projfinal

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import java.io.FileNotFoundException

class Horarios : AppCompatActivity() {

    private lateinit var tableLayout: TableLayout
    private lateinit var btnHorario1: Button
    private lateinit var btnHorario2: Button
    private lateinit var btnClear: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horarios)

        // Initialize views
        tableLayout = findViewById(R.id.tableLayout)
        btnHorario1 = findViewById(R.id.btnHorario1)
        btnHorario2 = findViewById(R.id.btnHorario2)
        btnClear = findViewById(R.id.btnClear)

        // Set button click listeners
        btnHorario1.setOnClickListener {
            loadSchedule("horario1.json")
        }

        btnHorario2.setOnClickListener {
            loadSchedule("horario2.json")
        }

        btnClear.setOnClickListener {
            clearTable()
        }

        // Generate empty table initially
        generateEmptyTable()
    }

    // Function to load schedule from JSON file
    private fun loadSchedule(filename: String) {
        try {
            val jsonString = assets.open(filename)
                .bufferedReader()
                .use { it.readText() }

            val gson = Gson()
            val scheduleData = gson.fromJson(jsonString, ScheduleResponse::class.java)

            // Generate table with the loaded schedule
            generateTableFromJson(scheduleData)

            // Show toast message
            val scheduleName = when (filename) {
                "horario1.json" -> "Horário 1"
                "horario2.json" -> "Horário 2"
                else -> "Schedule"
            }
            Toast.makeText(this, "Loaded: $scheduleName", Toast.LENGTH_SHORT).show()

        } catch (e: FileNotFoundException) {
            Toast.makeText(this, "File not found: $filename", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading schedule", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    // Function to generate table from JSON data
    private fun generateTableFromJson(scheduleData: ScheduleResponse) {
        tableLayout.removeAllViews()

        val days = scheduleData.schedule.days
        val timeSlots = scheduleData.schedule.timeSlots

        // Create header row
        val header = TableRow(this)
        header.addView(createCell("Time", "#C5CAE9", true))
        for (day in days) {
            header.addView(createCell(day, "#C5CAE9", true))
        }
        tableLayout.addView(header)

        // Create data rows from JSON
        for (timeSlot in timeSlots) {
            val row = TableRow(this)

            // Add time cell
            row.addView(createCell(timeSlot.time, "#E8EAF6", false))

            // Add day cells with subject names
            row.addView(createCell(timeSlot.Segunda, getCellColor(timeSlot.Segunda), false))
            row.addView(createCell(timeSlot.Terca, getCellColor(timeSlot.Terca), false))
            row.addView(createCell(timeSlot.Quarta, getCellColor(timeSlot.Quarta), false))
            row.addView(createCell(timeSlot.Quinta, getCellColor(timeSlot.Quinta), false))
            row.addView(createCell(timeSlot.Sexta, getCellColor(timeSlot.Sexta), false))
            row.addView(createCell(timeSlot.Sabado, getCellColor(timeSlot.Sabado), false))

            tableLayout.addView(row)
        }
    }

    // Function to generate empty table
    private fun generateEmptyTable() {
        tableLayout.removeAllViews()

        val days = listOf("Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sabado")

        // Header row
        val header = TableRow(this)
        header.addView(createCell("Time", "#C5CAE9", true))
        for (day in days) {
            header.addView(createCell(day, "#C5CAE9", true))
        }
        tableLayout.addView(header)

        Toast.makeText(this, "Empty table generated", Toast.LENGTH_SHORT).show()
    }

    // Function to clear table
    private fun clearTable() {
        tableLayout.removeAllViews()
        Toast.makeText(this, "Table cleared", Toast.LENGTH_SHORT).show()
    }

    // Helper function to create table cell
    private fun createCell(text: String, color: String, isHeader: Boolean): TextView {
        return TextView(this).apply {
            this.text = text
            setPadding(16, 16, 16, 16)
            gravity = Gravity.CENTER
            layoutParams = TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
            )

            // Set background color
            try {
                setBackgroundColor(Color.parseColor(color))
            } catch (e: Exception) {
                setBackgroundColor(Color.WHITE)
            }


        }
    }

    // Helper function to get cell color based on content
    private fun getCellColor(content: String): String {
        return if (content.isNotEmpty()) {
            // Assign different colors based on subject
            when {
                content.contains("Math", true) -> "#FF6B6B"
                content.contains("Physics", true) -> "#4ECDC4"
                content.contains("Chemistry", true) -> "#FFD166"
                content.contains("Biology", true) -> "#06D6A0"
                content.contains("History", true) -> "#118AB2"
                content.contains("Geography", true) -> "#9B5DE5"
                content.contains("Portuguese", true) -> "#00BBF9"
                content.contains("English", true) -> "#00F5D4"
                content.contains("Art", true) -> "#FF97B7"
                content.contains("Music", true) -> "#CA7DF9"
                content.contains("Programming", true) -> "#264653"
                content.contains("Algorithms", true) -> "#2A9D8F"
                content.contains("Databases", true) -> "#E9C46A"
                content.contains("Networks", true) -> "#F4A261"
                content.contains("Web Dev", true) -> "#E76F51"
                content.contains("Mobile Dev", true) -> "#9B5DE5"
                content.contains("AI", true) -> "#00BBF9"
                else -> "#9575CD" // Default color for other subjects
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
        val days: List<String>,
        val timeSlots: List<TimeSlot>
    )

    data class ScheduleResponse(
        val schedule: ScheduleData
    )

}


