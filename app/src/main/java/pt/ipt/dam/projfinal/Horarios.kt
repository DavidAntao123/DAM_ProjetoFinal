package pt.ipt.dam.projfinal

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView

class Horarios : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        generateTable()
        setContentView(R.layout.activity_main)

    }

    private fun generateTable() {
        val tableLayout = findViewById<TableLayout>(R.id.tableLayout)

        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        var hour = 10
        var minute = 30

        // Header row
        val header = TableRow(this)
        header.addView(createCell("Time", "#C5CAE9"))
        for (day in days) {
            header.addView(createCell(day, "#C5CAE9"))
        }
        tableLayout.addView(header)

        // Time rows
        while (hour < 24) {

            val nextHour = if (minute == 30) hour + 1 else hour
            val nextMinute = if (minute == 30) 0 else 30

            val timeText = String.format(
                "%02d:%02d - %02d:%02d",
                hour, minute, nextHour, nextMinute
            )

            val row = TableRow(this)
            row.addView(createCell(timeText, "#E8EAF6"))

            for (day in days) {
                row.addView(createCell(""))
            }

            tableLayout.addView(row)

            minute += 30
            if (minute == 60) {
                minute = 0
                hour++
            }
        }
    }

    private fun createCell(text: String, bgColor: String = "#FFFFFF"): TextView {
        return TextView(this).apply {
            layoutParams = TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
            )
            this.text = text
            textSize = 12f
            gravity = Gravity.CENTER
            setPadding(10, 10, 10, 10)
            setBackgroundColor(Color.parseColor(bgColor))
        }
    }
}


