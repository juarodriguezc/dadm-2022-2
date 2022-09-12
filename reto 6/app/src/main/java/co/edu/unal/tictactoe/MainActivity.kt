package co.edu.unal.tictactoe

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
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


    //Sounds for the game
    private val NUMSOUND = 7
    private lateinit var mSoundsPlayer: Array<MediaPlayer?>

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

    //Image for god move

    private lateinit var godImage: ImageView

    private var handler: Handler = Handler(Looper.getMainLooper())


    private inner class MTouchListener : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            // Determine which cell was touched
            val col = event!!.x.toInt() / mBoardView.getBoardCellWidth()
            val row = event.y.toInt() / mBoardView.getBoardCellHeight()
            val pos = row * 3 + col
            if (userPlays) {
                if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos)) {
                    var winner: IntArray = mGame.checkForWinner()
                    checkWinner(winner, TicTacToeGame.HUMAN_PLAYER)
                    if (winner[0] == 0) {
                        handler.postDelayed({
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
        //Button to restart
        restartButton = findViewById<View>(R.id.restartButton) as ImageButton
        //TextViews
        mInfoTextView = findViewById<View>(R.id.information) as TextView
        playerCount = findViewById<View>(R.id.text_player_count) as TextView
        tieCount = findViewById<View>(R.id.text_tie_count) as TextView
        androidCount = findViewById<View>(R.id.text_android_count) as TextView
        difficultyText = findViewById(R.id.difficulty_level)
        //Create audio arrays
        mSoundsPlayer = arrayOfNulls(NUMSOUND)
        //Load image
        godImage = findViewById(R.id.troll_face) as ImageView
        //Listener for restart button
        restartButton.setOnClickListener {
            startNewGame()
        }
        startNewGame()
    }

    override fun onResume() {
        super.onResume()
        /**
        GAME SOUNDS
        0 - OST
        1 - PLAYER SOUND
        2 - COMPUTER SOUND
        3 - WIN SOUND
        4 - LOSE SOUND
        5 - TIE SOUND
        6 - GOD SOUND
         **/
        mSoundsPlayer[0] = MediaPlayer.create(applicationContext, R.raw.ost)
        mSoundsPlayer[0]?.isLooping = true
        mSoundsPlayer[0]?.setVolume(0.09F,0.09F)
        mSoundsPlayer[0]?.start()

        mSoundsPlayer[1] = MediaPlayer.create(applicationContext, R.raw.player)
        mSoundsPlayer[2] = MediaPlayer.create(applicationContext, R.raw.computer)
        mSoundsPlayer[3] = MediaPlayer.create(applicationContext, R.raw.victory)
        mSoundsPlayer[4] = MediaPlayer.create(applicationContext, R.raw.lose)
        mSoundsPlayer[5] = MediaPlayer.create(applicationContext, R.raw.tie)
        mSoundsPlayer[6] = MediaPlayer.create(applicationContext, R.raw.god2)

    }

    override fun onPause() {
        super.onPause()
        for (i in 0 until NUMSOUND) {
            mSoundsPlayer[i]?.release()
        }
    }

    private fun stopAllMPS() {
        for (i in 1 until NUMSOUND) {
            if (mSoundsPlayer[i]?.isPlaying == true) {
                mSoundsPlayer[i]?.stop()
                mSoundsPlayer[i]?.prepareAsync();
            }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (menu is MenuBuilder) {
            (menu).setOptionalIconsVisible(true)
        }
        menuInflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.restart_game -> {
                nGames = intArrayOf(0, 0, 0)
                userStarts = true
                startNewGame()
            }
            R.id.play_again -> {
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
        val listItems = arrayOf("Easy", "Harder", "Expert", "God")
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
        //Update wins
        updateWins()
        //Hide image
        godImage.visibility = View.INVISIBLE;
        //Delete handler Queue
        handler.removeCallbacksAndMessages(null);
        userPlays = userStarts
        mGameOver = false
        clearBoard()
        if (!userStarts) {
            mInfoTextView.setText(R.string.androidfirst)
            handler.postDelayed({
                val winner: IntArray = androidPlays()
                checkWinner(winner, TicTacToeGame.COMPUTER_PLAYER)
            }, 2000)
        } else {
            mInfoTextView.setText(R.string.youfirst)
        }
        userStarts = !userStarts
    }

    private fun clearBoard() {
        // Reset all buttons
        mGame.clearBoard()
        mBoardView.invalidate()
        mBoardView.setWinner(false)
        mBoardView.setWPos(intArrayOf(-1, -1, -1, -1))
    }

    private fun androidPlays(): IntArray {
        if (!userPlays) {
            val move: Int = mGame.computerMove
            setMove(TicTacToeGame.COMPUTER_PLAYER, move)
            mInfoTextView.setText(R.string.youturn)
        }
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
                updateWins()
                mGameOver = true
                //Play sound
                stopAllMPS()
                mSoundsPlayer[5]?.start()

            }
            2 -> {
                mInfoTextView.setText(R.string.youwon)
                nGames[0]++
                updateWins()
                setWinner(winner)
                mGameOver = true
                //Play sound
                stopAllMPS()
                mSoundsPlayer[3]?.start()
            }
            else -> {
                mInfoTextView.setText(R.string.androidwon)
                nGames[1]++
                updateWins()
                setWinner(winner)
                mGameOver = true
                //Play sound
                stopAllMPS()
                mSoundsPlayer[4]?.start()
                //Check if god move won
                println(winner)
                if(winner[0] == 4){
                    //Show image
                    godImage.visibility = View.VISIBLE;
                    //Play sound
                    stopAllMPS()
                    mSoundsPlayer[6]?.start()
                }

            }
        }
    }

    private fun updateWins() {
        playerCount.text = nGames[0].toString()
        androidCount.text = nGames[1].toString()
        tieCount.text = nGames[2].toString()
    }

    private fun setMove(player: Char, location: Int): Boolean {
        if (mGame.setMove(player, location)) {
            userPlays = !userPlays
            mBoardView.invalidate()
            if (player == TicTacToeGame.HUMAN_PLAYER) {
                stopAllMPS()
                mSoundsPlayer[1]?.start()
            } else {
                stopAllMPS()
                mSoundsPlayer[2]?.start()
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