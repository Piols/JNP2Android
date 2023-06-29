package com.example.gridentify

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.ColorUtils
import com.example.gridentify.ui.theme.GridentifyTheme
import java.lang.Math.log
import java.util.Stack
import kotlin.random.Random.Default.nextInt


class MainActivity : ComponentActivity() {
    lateinit var cells : Array<Array<GridCell>>
    var stack = Stack<Pair<Int, Int>>()
    var selected_number = 0
    var score = 0
    var game_over = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val grid: GridLayout = findViewById(R.id.gridLayout)

        cells = Array<Array<GridCell>>(5) { i ->
            Array(5) { j -> createCell(grid, i, j) }
        }

        for (i in 0 until 5) {
            for (j in 0 until 5) {
                cells[i][j].button.setOnClickListener {(onCellClicked(i, j))}
            }
        }
    }

    fun newGame() {
        score = 0
        game_over = false
        val scoreView = findViewById<TextView>(R.id.textScore)
        scoreView.setText("New game!\nClick neighboring cells with same value and merge them!")
        val button = findViewById<Button>(R.id.buttonAction)
        button.setText("Merge")
        stack.clear();
        for (i in 0 until 5) {
            for (j in 0 until 5) {
                cells[i][j].deselect()
                cells[i][j].generateValue()
            }
        }
    }

    fun onCellClicked(row: Int, column: Int) {
        if (game_over) return
        val cell = cells[row][column]
        if (cell.state == 1) {
            return
        }
        if (cell.state == 2) {
            cell.deselect()
            stack.pop()
            if (!stack.empty()) {
                cells[stack.peek().first][stack.peek().second].highlight()
            }
            return
        }
        if (stack.empty()) {
            stack.push(Pair<Int, Int>(row, column))
            cell.highlight()
            selected_number = cell.value
            return
        }
        if (checkNeighboringCellsForSelection(row, column)) {
            if (cell.value != selected_number) {
                return
            }
            cells[stack.peek().first][stack.peek().second].select()
            stack.push(Pair<Int, Int>(row, column))
            cell.highlight()
            return
        }
    }

    fun onMergeClicked(view: View) {
        if (game_over) {
            newGame()
            return
        }
        if (stack.empty()) {
            return
        }
        val scoreGained = stack.size * selected_number
        if (stack.size > 1) {
            score += scoreGained
            val scoreView = findViewById<TextView>(R.id.textScore)
            scoreView.setText("Current score: ".plus(score.toString()))
        }
        for (i in 0 until 5) {
            for (j in 0 until 5) {
                val cell = cells[i][j]
                if (cell.state == 1) {
                    cell.generateValue()
                }
                if (cell.state == 2) {
                    cell.changeValue(scoreGained)
                }
                cell.deselect()
            }
        }
        stack.clear()
        var ok = false
        for (i in 0 until 5) {
            for (j in 0 until 5) {
                ok = ok.or(checkNeighboringCellsForSameValue(i, j))
            }
        }
        if (!ok) {
            gameOver()
        }
    }

    fun gameOver() {
        game_over = true
        val scoreView = findViewById<TextView>(R.id.textScore)
        scoreView.setText("Game Over!\nFinal score: ".plus(score.toString()))
        val button = findViewById<Button>(R.id.buttonAction)
        button.setText("New game")

    }
    fun checkNeighboringCellsForSameValue(row: Int, column: Int): Boolean {
        val value = cells[row][column].value
        return cellValue(row - 1, column) == value || cellValue(row, column - 1) == value || cellValue(row + 1, column) == value || cellValue(row, column + 1) == value
    }

    fun cellValue(row: Int, column: Int): Int {
        if (row < 0 || row >= 5 || column < 0 || column >= 5) {
            return -1;
        }
        return cells[row][column].value
    }

    fun checkNeighboringCellsForSelection(row: Int, column: Int): Boolean {
        return cellState(row - 1, column) == 2 || cellState(row, column - 1) == 2 || cellState(row + 1, column) == 2 || cellState(row, column + 1) == 2
    }
    fun cellState(row: Int, column: Int): Int {
        if (row < 0 || row >= 5 || column < 0 || column >= 5) {
            return -1;
        }
        return cells[row][column].state
    }
}

fun createCell(grid: GridLayout, row: Int, column: Int): GridCell {
    val frame = grid.getChildAt(row * grid.columnCount + column) as FrameLayout
    val button = frame.getChildAt(0) as Button
    return GridCell(frame, button, row, column)
}

class GridCell(val frame: FrameLayout, val button: Button, val row: Int, val column: Int) {
    var value : Int = 0
    var state: Int = 0
    init {
        generateValue()
    }
    fun changeValue(newValue: Int) {
        value = newValue
        changeColor()
        button.setText(value.toString())
    }
    fun deselect() {
        state = 0
        changeFrameColor(Color.BLACK)
    }
    fun select() {
        state = 1
        changeFrameColor(Color.GREEN)
    }
    fun highlight() {
        state = 2
        changeFrameColor(Color.RED)
    }
    fun changeColor() {
        button.setBackgroundColor(ColorUtils.HSLToColor(floatArrayOf((200 + log(value.toDouble()) * 40).mod(360.0).toFloat(), 0.6F, (0.5 + (log(value.toDouble()).div(90.0))).mod(1.0).toFloat())))
    }
    fun changeFrameColor(color: Int) {
        val drawable = frame.background as? GradientDrawable
        drawable?.setColor(color)
    }


    fun generateValue() {
        val newValue : Int = nextInt(3) + 1
        changeValue(newValue)
    }
}