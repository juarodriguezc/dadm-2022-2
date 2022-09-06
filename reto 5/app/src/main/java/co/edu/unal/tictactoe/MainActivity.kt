package co.edu.unal.tictactoe

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    private lateinit var mGame: TicTacToeGame

    // Buttons making up the board
    private lateinit var mBoardButtons: Array<ImageButton?>
    private lateinit var restartButton: ImageButton

    // Various text displayed
    private lateinit var mInfoTextView: TextView

    private lateinit var playerCount: TextView
    private lateinit var tieCount: TextView
    private lateinit var androidCount: TextView

    private lateinit var difficultyText: TextView


    private var userStarts: Boolean = false

    var mHumanMediaPlayer: MediaPlayer? = null
    var mComputerMediaPlayer: MediaPlayer? = null


    /**
    Three games Positions
    0 - Games of player
    1 - Games of android
    2 - Ties
     **/
    private lateinit var nGames: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBoardButtons = arrayOfNulls(TicTacToeGame().boarD_SIZE)

        nGames = intArrayOf(0, 0, 0)

        mBoardButtons[0] = findViewById<View>(R.id.one) as ImageButton
        mBoardButtons[1] = findViewById<View>(R.id.two) as ImageButton
        mBoardButtons[2] = findViewById<View>(R.id.three) as ImageButton
        mBoardButtons[3] = findViewById<View>(R.id.four) as ImageButton
        mBoardButtons[5] = findViewById<View>(R.id.six) as ImageButton
        mBoardButtons[4] = findViewById<View>(R.id.five) as ImageButton
        mBoardButtons[6] = findViewById<View>(R.id.seven) as ImageButton
        mBoardButtons[7] = findViewById<View>(R.id.eight) as ImageButton
        mBoardButtons[8] = findViewById<View>(R.id.nine) as ImageButton

        restartButton = findViewById<View>(R.id.restartButton) as ImageButton



        mInfoTextView = findViewById<View>(R.id.information) as TextView


        playerCount = findViewById<View>(R.id.text_player_count) as TextView
        tieCount = findViewById<View>(R.id.text_tie_count) as TextView
        androidCount = findViewById<View>(R.id.text_android_count) as TextView

        difficultyText = findViewById(R.id.difficulty_level)


        restartButton.setOnClickListener {
            startNewGame()
        }



        mGame = TicTacToeGame()

        startNewGame()
    }

    override fun onResume() {
        super.onResume()
        mHumanMediaPlayer = MediaPlayer.create(applicationContext, R.raw.player)
        mComputerMediaPlayer = MediaPlayer.create(applicationContext, R.raw.computer)
    }

    override fun onPause() {
        super.onPause()
        mHumanMediaPlayer?.release()
        mComputerMediaPlayer?.release()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.new_game -> {
                startNewGame()
            }
            R.id.ai_difficulty -> {
                showDifficultyDialog()
            }
            R.id.quit -> {
                showExitDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showDifficultyDialog() {
        val listItems = arrayOf("Easy", "Harder", "Expert")
        val mBuilder = AlertDialog.Builder(this@MainActivity)
        mBuilder.setTitle("Set the difficulty")
        mBuilder.setSingleChoiceItems(listItems, -1) { dialogInterface, i ->
            //TODO - Fix order

            difficultyText.text = listItems[i]
            mGame.setmDifficultyLevel(listItems[i])
            startNewGame()


            dialogInterface.dismiss()
        }
        // Set the neutral/cancel button click listener
        mBuilder.setNeutralButton("Cancel") { dialog, _ ->
            // Do something when click the neutral button
            dialog.cancel()
        }

        val mDialog = mBuilder.create()
        mDialog.show()

    }

    private fun showExitDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Close the game")
        builder.setMessage("Do you want to close the game?")
        builder.setPositiveButton("Yes") { _, _ ->
            exitProcess(0)
        }
        builder.setNegativeButton("No") { _, _ ->
        }
        builder.show()
    }

    // Set up the game board.
    private fun startNewGame() {
        mGame.clearBoard()

        userStarts = !userStarts

        // Reset all buttons
        for (i in mBoardButtons.indices) {
            mBoardButtons[i]!!.setImageResource(0)
            mBoardButtons[i]!!.clearColorFilter()
            mBoardButtons[i]!!.isEnabled = true
            mBoardButtons[i]!!.setOnClickListener {
                if (mBoardButtons[i]!!.isEnabled) {

                    setMove(TicTacToeGame.HUMAN_PLAYER, i)
                    // If no winner yet, let the computer make a move
                    var winner: IntArray? = mGame.checkForWinner()
                    if (winner?.get(0) == 0) {
                        mInfoTextView.setText(R.string.androidturn)
                        val move: Int = mGame.computerMove
                        setMove(TicTacToeGame.COMPUTER_PLAYER, move)
                        winner = mGame.checkForWinner()
                    }
                    when (winner?.get(0)) {
                        0 -> mInfoTextView.setText(R.string.youturn)
                        1 -> {
                            mInfoTextView.setText(R.string.itsatie)
                            nGames[2]++
                            tieCount.text = nGames[2].toString()

                        }
                        2 -> {
                            mInfoTextView.setText(R.string.youwon)
                            nGames[0]++
                            playerCount.text = nGames[0].toString()
                            setWinner(winner)
                        }
                        else -> {
                            mInfoTextView.setText(R.string.androidwon)
                            if (winner != null) {
                                nGames[1]++
                                androidCount.text = nGames[1].toString()
                                setWinner(winner)
                            }
                        }
                    }
                }
            }
        }
        if(userStarts){
            // Human goes first
            mInfoTextView.setText(R.string.youfirst)
        }else{
            mInfoTextView.setText(R.string.androidfirst)
            val move: Int = mGame.computerMove
            setMove(TicTacToeGame.COMPUTER_PLAYER, move)
            val winner = mGame.checkForWinner()
            when (winner?.get(0)) {
                0 -> mInfoTextView.setText(R.string.youturn)
                1 -> {
                    mInfoTextView.setText(R.string.itsatie)
                    nGames[2]++
                    tieCount.text = nGames[2].toString()

                }
                2 -> {
                    mInfoTextView.setText(R.string.youwon)
                    nGames[0]++
                    playerCount.text = nGames[0].toString()
                    setWinner(winner)
                }
                else -> {
                    mInfoTextView.setText(R.string.androidwon)
                    if (winner != null) {
                        nGames[1]++
                        androidCount.text = nGames[1].toString()
                        setWinner(winner)
                    }
                }
            }
        }

    }

    private fun setMove(player: Char, location: Int) {
        mGame.setMove(player, location)
        mBoardButtons[location]!!.isEnabled = false

        //mBoardButtons[location]!!.text = player.toString()

        if (player == TicTacToeGame.HUMAN_PLAYER){
            mHumanMediaPlayer?.start()
            mBoardButtons[location]?.setImageResource(R.drawable.x_symbol_m)
        }
        else {
            mComputerMediaPlayer?.start()
            mBoardButtons[location]?.setImageResource(R.drawable.o_symbol_m)
        }


    }

    private fun setWinner(winner: IntArray) {
        //Limpiar los resultados
        for (i in mBoardButtons.indices)
            mBoardButtons[i]!!.clearColorFilter()
        //Pintar el ganador
        mBoardButtons[winner[1]]!!.setColorFilter(Color.argb(255, 206, 147, 216))
        mBoardButtons[winner[2]]!!.setColorFilter(Color.argb(255, 206, 147, 216))
        mBoardButtons[winner[3]]!!.setColorFilter(Color.argb(255, 206, 147, 216))
        //Bloquear las casillas al terminar la partida
        for (i in mBoardButtons.indices)
            mBoardButtons[i]!!.isEnabled = false


    }


}