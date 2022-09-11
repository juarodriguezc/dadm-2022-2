package co.edu.unal.tictactoe

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    private lateinit var mGame: TicTacToeGame

    // Buttons making up the board
    private lateinit var restartButton: ImageButton

    // Various text displayed
    private lateinit var mInfoTextView: TextView
    private lateinit var playerCount: TextView
    private lateinit var tieCount: TextView
    private lateinit var androidCount: TextView
    private lateinit var difficultyText: TextView

    //Variable to control the player who starts
    private var userStarts: Boolean = true
    private var mGameOver = false
    private var userPlays: Boolean = false

    //Variable to store the gameBoard
    private lateinit var gameBoard: CharArray

    //Sounds for each player
    private var mHumanMediaPlayer: MediaPlayer? = null
    private var mComputerMediaPlayer: MediaPlayer? = null
    private var mLoseMediaPlayer: MediaPlayer? = null
    private var mWinMediaPlayer: MediaPlayer? = null

    //Game board view
    private lateinit var mBoardView: BoardView

    //Array to count wins
    /**
    Three games Positions
    0 - Games of player
    1 - Games of android
    2 - Ties
     **/
    private lateinit var nGames: IntArray



    private inner class MTouchListener : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            // Determine which cell was touched
            val col = event!!.x.toInt() / mBoardView.getBoardCellWidth()
            val row = event.y.toInt() / mBoardView.getBoardCellHeight()
            val pos = row * 3 + col
            println("USRPLAYS: "+userPlays)
            if(userPlays){
                if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos)){
                    var winner: IntArray = mGame.checkForWinner()
                    checkWinner(winner, TicTacToeGame.HUMAN_PLAYER)
                    if (winner[0] == 0) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            winner = androidPlays()
                            checkWinner(winner, TicTacToeGame.COMPUTER_PLAYER)
                        }, 2000)
                    }
                }
            }
            return false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mGame = TicTacToeGame()
        mBoardView = findViewById(R.id.boardView)
        mBoardView.setGame(mGame)
        // Listen for touches on the board
        mBoardView.setOnTouchListener(MTouchListener());
        nGames = intArrayOf(0, 0, 0)




        restartButton = findViewById<View>(R.id.restartButton) as ImageButton


        mInfoTextView = findViewById<View>(R.id.information) as TextView
        playerCount = findViewById<View>(R.id.text_player_count) as TextView
        tieCount = findViewById<View>(R.id.text_tie_count) as TextView
        androidCount = findViewById<View>(R.id.text_android_count) as TextView


        difficultyText = findViewById(R.id.difficulty_level)


        restartButton.setOnClickListener {
            startNewGame()
        }
        startNewGame()
    }

    override fun onResume() {
        super.onResume()
        mHumanMediaPlayer = MediaPlayer.create(applicationContext, R.raw.player)
        mComputerMediaPlayer = MediaPlayer.create(applicationContext, R.raw.computer)
        mLoseMediaPlayer = MediaPlayer.create(applicationContext, R.raw.lose)
        mWinMediaPlayer = MediaPlayer.create(applicationContext, R.raw.victory)
    }

    override fun onPause() {
        super.onPause()
        mHumanMediaPlayer?.release()
        mComputerMediaPlayer?.release()
        mLoseMediaPlayer?.release()
        mWinMediaPlayer?.release()
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
        mBuilder.setTitle(R.string.set_difficulty)
        mBuilder.setSingleChoiceItems(listItems, -1) { dialogInterface, i ->
            difficultyText.text = listItems[i]
            mGame.setmDifficultyLevel(listItems[i])
            startNewGame()
            dialogInterface.dismiss()
        }
        mBuilder.setNeutralButton("Cancel") { dialog, _ ->
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
        userPlays = userStarts
        mGameOver = false
        clearBoard()
        if (!userStarts) {
            mInfoTextView.setText(R.string.androidfirst)
            val winner: IntArray = androidPlays()
            checkWinner(winner, TicTacToeGame.COMPUTER_PLAYER)
        } else {
            mInfoTextView.setText(R.string.youfirst)
        }
        userStarts = !userStarts
    }

    private fun clearBoard() {
        // Reset all buttons
        mGame.clearBoard()
        mBoardView.invalidate()
        gameBoard = mGame.getmBoard()
        mBoardView.setWinner(false)
        mBoardView.setWPos(intArrayOf(-1,-1,-1,-1))
    }

    private fun androidPlays(): IntArray {
        mInfoTextView.setText(R.string.androidturn)
        val move: Int = mGame.computerMove
        setMove(TicTacToeGame.COMPUTER_PLAYER, move)
        return mGame.checkForWinner()
    }

    private fun checkWinner(winner: IntArray, player: Char) {
        when (winner[0]) {
            0 -> {
                if (player == TicTacToeGame.HUMAN_PLAYER) {
                    mInfoTextView.setText(R.string.androidturn)
                } else {
                    mInfoTextView.setText(R.string.youturn)
                }
            }
            1 -> {
                mInfoTextView.setText(R.string.itsatie)
                nGames[2]++
                tieCount.text = nGames[2].toString()
                mGameOver = true
            }
            2 -> {
                mInfoTextView.setText(R.string.youwon)
                nGames[0]++
                playerCount.text = nGames[0].toString()
                setWinner(winner)
                mGameOver = true
                //Play sound
                mWinMediaPlayer?.start()
            }
            else -> {
                mInfoTextView.setText(R.string.androidwon)
                nGames[1]++
                androidCount.text = nGames[1].toString()
                setWinner(winner)
                mGameOver = true
                //Play sound
                mLoseMediaPlayer?.start()
            }
        }
    }


    private fun setMove(player: Char, location: Int): Boolean {
        if(mGame.setMove(player, location)){
            userPlays = !userPlays
            mBoardView.invalidate()
            gameBoard = mGame.getmBoard()

            if (player == TicTacToeGame.HUMAN_PLAYER) {
                if (mComputerMediaPlayer?.isPlaying == true) {
                    mComputerMediaPlayer?.stop()
                    mComputerMediaPlayer?.prepareAsync();
                }
                mHumanMediaPlayer?.start()
                //Lock board to avoid double move
            } else {
                if (mHumanMediaPlayer?.isPlaying == true) {
                    mHumanMediaPlayer?.stop()
                    mHumanMediaPlayer?.prepareAsync();
                }
                mComputerMediaPlayer?.start()
                //Unlock board to avoid double move
            }
            return true
        }
        return false
    }

    private fun setWinner(winner: IntArray) {
        mBoardView.setWinner(true)
        mBoardView.setWPos(winner)
        mBoardView.invalidate()
    }
}