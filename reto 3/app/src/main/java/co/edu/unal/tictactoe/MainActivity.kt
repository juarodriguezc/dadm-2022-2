package co.edu.unal.tictactoe

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var mGame: TicTacToeGame

    // Buttons making up the board
    private lateinit var mBoardButtons : Array<ImageButton?>
    // Various text displayed
    private lateinit var mInfoTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBoardButtons = arrayOfNulls(TicTacToeGame().BOARD_SIZE)
        mBoardButtons[0] = findViewById<View>(R.id.one) as ImageButton
        mBoardButtons[1] = findViewById<View>(R.id.two) as ImageButton
        mBoardButtons[2] = findViewById<View>(R.id.three) as ImageButton
        mBoardButtons[3] = findViewById<View>(R.id.four) as ImageButton
        mBoardButtons[4] = findViewById<View>(R.id.five) as ImageButton
        mBoardButtons[5] = findViewById<View>(R.id.six) as ImageButton
        mBoardButtons[6] = findViewById<View>(R.id.seven) as ImageButton
        mBoardButtons[7] = findViewById<View>(R.id.eight) as ImageButton
        mBoardButtons[8] = findViewById<View>(R.id.nine) as ImageButton

        mInfoTextView = findViewById<View>(R.id.information) as TextView
        mGame = TicTacToeGame()

        startNewGame()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menu.add("New Game")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startNewGame()
        return true
    }

    // Set up the game board.
    private fun  startNewGame() {
        mGame.clearBoard()

        // Reset all buttons
        for (i in mBoardButtons.indices) {
            mBoardButtons[i]!!.setImageResource(R.drawable.transparent)
            mBoardButtons[i]!!.isEnabled = true
            mBoardButtons[i]!!.setOnClickListener {
                if (mBoardButtons[i]!!.isEnabled) {

                    setMove(TicTacToeGame.HUMAN_PLAYER, i)
                    println("Movement: "+i+" type: "+ TicTacToeGame.HUMAN_PLAYER);
                    for(j in 0 until  9){
                        print(mGame.mBoard[j]+" ");
                    }
                    println(" ")
                    // If no winner yet, let the computer make a move
                    var winner: Int = mGame.checkForWinner()
                    if (winner == 0) {
                        mInfoTextView.text = "It's Android's turn."
                        val move: Int = mGame.getComputerMove()
                        setMove(TicTacToeGame.COMPUTER_PLAYER, move)
                        println("Movement: "+move+" type: "+ TicTacToeGame.COMPUTER_PLAYER);
                        for(j in 0 until  9){
                            print(mGame.mBoard[j]+" ");
                        }
                        println(" ")
                        winner = mGame.checkForWinner()
                    }
                    when (winner) {
                        0 -> mInfoTextView.text =
                            "It's your turn."
                        1 -> mInfoTextView.text =
                            "It's a tie!"
                        2 -> mInfoTextView.text =
                            "You won!"
                        else -> mInfoTextView.text =
                            "Android won!"
                    }
                }
            }
        }
        // Human goes first
        mInfoTextView.text = "You go first."
    }

    private fun setMove(player: Char, location: Int) {
        mGame.setMove(player, location)
        mBoardButtons[location]!!.isEnabled = false

        //mBoardButtons[location]!!.text = player.toString()

        if (player == TicTacToeGame.HUMAN_PLAYER) mBoardButtons[location]?.setImageResource(R.drawable.x_symbol)
        else mBoardButtons[location]?.setImageResource(R.drawable.o_symbol)


    }
}