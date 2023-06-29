package com.example.gridentify

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.gridentify.ui.theme.GridentifyTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gridLayout: GridLayout = findViewById(R.id.gridLayout)

        // Set click listener for each cell in the grid
        for (row in 0 until gridLayout.rowCount) {
            for (column in 0 until gridLayout.columnCount) {
                val frameView = gridLayout.getChildAt(row * gridLayout.columnCount + column) as FrameLayout
                val cellView = frameView.getChildAt(0) as Button
                cellView.setOnClickListener { onCellClicked(cellView, row, column) }
            }
        }
    }

    fun onCellClicked(view: View, row: Int, column: Int) {
        val cellButton = view as Button
        cellButton.setBackgroundColor(Color.BLUE)

        println("Clicked cell at row: $row, column: $column")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GridentifyTheme {
        Greeting("Android")
    }
}