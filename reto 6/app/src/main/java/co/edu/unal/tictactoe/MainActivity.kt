package co.edu.unal.tictactoe

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    private lateinit var mGame: TicTacToeGame

    // Buttons making up the board
    private lateinit var restartButton: ImageButton

    //Button to open the menu
    private lateinit var menuButton: ImageButton

    //Button for music
    private lateinit var musicButton: ImageButton

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

    //Variable to control the difficulty

    private val difficultyLevel: Array<String> = arrayOf("Easy", "Harder", "Expert", "God")

    //Set default difficulty to expert
    private var mDifLevel: Int = 2

    //Sounds for the game
    private val NUMSOUND = 7
    private lateinit var mSoundsPlayer: Array<MediaPlayer?>
    private var musicSounds = true
    private var musicPos = 0

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


    //Keep persistent scores

    private lateinit var mPrefs: SharedPreferences

    private inner class MTouchListener : View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharArray("board", mGame.boardState)
        outState.putBoolean("mGameOver", mGameOver)
        outState.putCharSequence("info", mInfoTextView.text)
        outState.putBoolean("userStarts", userStarts)
        outState.putBoolean("userPlays", userPlays)
        //Save song position
        outState.putInt("musicPos", musicPos)
        //outState.putInt("musicPos", mSoundsPlayer[0].currentPosition)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Restore the game's state
        mGame.boardState = savedInstanceState.getCharArray("board")
        mGameOver = savedInstanceState.getBoolean("mGameOver")
        mInfoTextView.text = savedInstanceState.getCharSequence("info")
        userStarts = savedInstanceState.getBoolean("userStarts")
        userPlays = savedInstanceState.getBoolean("userPlays", userPlays)
        musicPos = savedInstanceState.getInt("musicPos", 0)
        //When the game is reloaded play if is android turn
        if (!userPlays && !mGameOver) {
            handler.postDelayed({
                val winner: IntArray = androidPlays()
                checkWinner(winner, TicTacToeGame.COMPUTER_PLAYER)
            }, 2000)
        }
        if (mGameOver) {
            val winner: IntArray = mGame.checkForWinner()
            val plays = if (userPlays) TicTacToeGame.HUMAN_PLAYER else TicTacToeGame.COMPUTER_PLAYER
            checkWinner(winner, plays)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Create game
        mGame = TicTacToeGame()
        mBoardView = findViewById(R.id.boardView)
        mBoardView.setGame(mGame)
        /* Listen for touches on the board */
        mBoardView.setOnTouchListener(MTouchListener())
        nGames = intArrayOf(0, 0, 0)
        //Button to restart
        restartButton = findViewById<View>(R.id.restartButton) as ImageButton
        //Button to open the menu
        menuButton = findViewById<View>(R.id.menu_button) as ImageButton
        //Button to mute music
        musicButton = findViewById<View>(R.id.musicButton) as ImageButton
        //TextViews
        mInfoTextView = findViewById<View>(R.id.information) as TextView
        playerCount = findViewById<View>(R.id.text_player_count) as TextView
        tieCount = findViewById<View>(R.id.text_tie_count) as TextView
        androidCount = findViewById<View>(R.id.text_android_count) as TextView
        difficultyText = findViewById(R.id.difficulty_level)
        //Create audio arrays
        mSoundsPlayer = arrayOfNulls(NUMSOUND)
        //Load image
        godImage = findViewById(R.id.troll_face)

        //Data persistence
        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE)
        //Restore the scores
        nGames[0] = mPrefs.getInt("wins", 0)
        nGames[1] = mPrefs.getInt("loses", 0)
        nGames[2] = mPrefs.getInt("ties", 0)

        mDifLevel = mPrefs.getInt("mDifLevel", 2)
        updateDifficulty()

        //Store if music is muted
        musicSounds = mPrefs.getBoolean("musicSounds", true)

        //Update icon of music sound
        if (!musicSounds)
            musicButton.setImageResource(R.drawable.mute)
        else
            musicButton.setImageResource(R.drawable.sound)


        //Listener for restart button
        restartButton.setOnClickListener {
            startNewGame()
        }

        menuButton.setOnClickListener {
            showPopup(menuButton)
        }

        musicButton.setOnClickListener {
            musicUNMute()
        }

        //Load previous state
        if (savedInstanceState == null) {
            startNewGame()
        }
        updateWins()
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
        try {
            mSoundsPlayer[0]?.isLooping = true
            mSoundsPlayer[0]?.seekTo(musicPos)
            mSoundsPlayer[0]?.setVolume(0.09F, 0.09F)
            if (musicSounds) {
                mSoundsPlayer[0]?.start()
            }
        } catch (e: IllegalStateException) {
            println("Error al reproducir el OST")
        }
        mSoundsPlayer[1] = MediaPlayer.create(applicationContext, R.raw.player)
        mSoundsPlayer[2] = MediaPlayer.create(applicationContext, R.raw.computer)
        mSoundsPlayer[3] = MediaPlayer.create(applicationContext, R.raw.victory)
        mSoundsPlayer[4] = MediaPlayer.create(applicationContext, R.raw.lose)
        mSoundsPlayer[5] = MediaPlayer.create(applicationContext, R.raw.tie)
        mSoundsPlayer[6] = MediaPlayer.create(applicationContext, R.raw.god2)

    }

    override fun onPause() {
        super.onPause()
        //Get sound position onPause event
        try {
            if (mSoundsPlayer[0] != null) {
                musicPos = mSoundsPlayer[0]?.currentPosition!!
            }
        } catch (e: IllegalStateException) {
            println("Error al reproducir el OST")
        }
        //Release all the sounds
        for (i in 0 until NUMSOUND) {
            mSoundsPlayer[i]?.release()
        }
    }

    override fun onStop() {
        super.onStop()
        // Save the current scores
        val ed = mPrefs.edit()
        //Wins
        ed.putInt("wins", nGames[0])
        //Lose
        ed.putInt("loses", nGames[1])
        //Tie
        ed.putInt("ties", nGames[2])
        //Difficulty
        ed.putInt("mDifLevel", mDifLevel)
        //Music stops
        ed.putBoolean("musicSounds", musicSounds)
        ed.apply()
    }

    @SuppressLint("RestrictedApi", "DiscouragedPrivateApi")
    private fun showPopup(v: View) {
        val wrapper = ContextThemeWrapper(this, R.style.CustomPopupMenu)
        val popup = PopupMenu(wrapper, v, Gravity.END)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.options_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
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
            true
        }

        //Try showing icons on menu
        try {
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(popup)
            mPopup.javaClass
                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(mPopup, true)
        } catch (e: Exception) {
            Log.e("Main", "Error showing menu icons.", e)
        } finally {
            popup.show()
        }
    }

    private fun musicUNMute() {


        musicSounds = !musicSounds
        if (musicSounds) {
            musicButton.setImageResource(R.drawable.sound)
            try {
                mSoundsPlayer[0]?.start()
            } catch (e: IllegalStateException) {
                println("Error al reproducir el OST")
            }

        } else {
            musicButton.setImageResource(R.drawable.mute)
            try {
                mSoundsPlayer[0]?.pause()
            } catch (e: IllegalStateException) {
                println("Error al pausar el OST")
            }
        }
    }

    private fun stopAllMPS() {
        for (i in 1 until NUMSOUND) {
            try {
                if (mSoundsPlayer[i]?.isPlaying == true) {
                    mSoundsPlayer[i]?.stop()
                    mSoundsPlayer[i]?.prepareAsync()
                }
            } catch (e: IllegalStateException) {
                println("Error al pausar los sonidos")
            }

        }
    }

    private fun showDifficultyDialog() {
        val wrapper = ContextThemeWrapper(this, R.style.CustomAlertDialog)
        val mBuilder = AlertDialog.Builder(wrapper)
        //Textview
        val textView = TextView(this)
        textView.typeface = ResourcesCompat.getFont(this, R.font.minecraft_font2)
        textView.text = getString(R.string.set_difficulty)
        textView.setPadding(0, 60, 0, 20)
        textView.textSize = 25f
        textView.gravity = Gravity.CENTER_HORIZONTAL
        textView.setTextColor(Color.WHITE)

        mBuilder.setCustomTitle(textView)
        mBuilder.setSingleChoiceItems(difficultyLevel, mDifLevel) { dialogInterface, i ->
            mDifLevel = i
            updateDifficulty()
            startNewGame()
            dialogInterface.dismiss()
        }
        mBuilder.setNeutralButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        val mDialog = mBuilder.create()

        mDialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#C1323232")));


        mDialog.show()
    }

    private fun updateDifficulty() {
        difficultyText.text = difficultyLevel[mDifLevel]
        mGame.setmDifficultyLevel(difficultyLevel[mDifLevel])

    }

    private fun showExitDialog() {
        val wrapper = ContextThemeWrapper(this, R.style.CustomAlertDialog)
        //Create dialog
        val builder = AlertDialog.Builder(wrapper)

        //Title
        val textView = TextView(this)
        textView.typeface = ResourcesCompat.getFont(this, R.font.minecraft_font2)
        textView.text = "Close the game"
        textView.setPadding(0, 60, 0, 20)
        textView.textSize = 20f
        textView.gravity = Gravity.CENTER_HORIZONTAL
        textView.setTextColor(Color.WHITE)

        builder.setCustomTitle(textView)
        builder.setMessage("Do you want to close the game?")
        builder.setPositiveButton("Yes") { _, _ ->
            exitProcess(0)
        }
        builder.setNegativeButton("No") { _, _ ->
        }

        val mDialog = builder.create()

        mDialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#C1323232")));

        mDialog.show()
    }

    // Set up the game board.
    private fun startNewGame() {
        //Update wins
        updateWins()
        //Hide image
        godImage.visibility = View.INVISIBLE
        //Delete handler Queue
        handler.removeCallbacksAndMessages(null)
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
                try {
                    mSoundsPlayer[5]?.start()
                } catch (e: IllegalStateException) {
                    println("Error al reproducir el sonido de TIE")
                }
            }
            2 -> {
                mInfoTextView.setText(R.string.youwon)
                nGames[0]++
                updateWins()
                setWinner(winner)
                mGameOver = true
                //Play sound
                stopAllMPS()
                try {
                    mSoundsPlayer[3]?.start()
                } catch (e: IllegalStateException) {
                    println("Error al reproducir el sonido de victoria")
                }
            }
            else -> {
                mInfoTextView.setText(R.string.androidwon)
                nGames[1]++
                updateWins()
                setWinner(winner)
                mGameOver = true


                //Play sound
                stopAllMPS()
                try {
                    mSoundsPlayer[4]?.start()
                } catch (e: IllegalStateException) {
                    println("Error al reproducir el sonido de perdida")
                }
                //Check if god move won
                if (winner[0] == 4) {
                    //Show image
                    godImage.visibility = View.VISIBLE
                    //Play sound
                    stopAllMPS()
                    try {
                        mSoundsPlayer[6]?.start()
                    } catch (e: IllegalStateException) {
                        println("Error al reproducir el sonido de GOD")
                    }
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

                try {
                    mSoundsPlayer[1]?.start()
                } catch (e: IllegalStateException) {
                    println("ERRor al reproducir el sonido del jugador")
                }

            } else {
                stopAllMPS()

                try {
                    mSoundsPlayer[2]?.start()
                } catch (e: IllegalStateException) {
                    println("Error al reproducir el sonido del COmputer")
                }
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