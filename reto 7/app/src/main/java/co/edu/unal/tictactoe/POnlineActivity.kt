package co.edu.unal.tictactoe

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlin.system.exitProcess

class POnlineActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    private lateinit var mGame: TicTacToeGameOnline


    //Button to go back to home screen
    private lateinit var backButton: ImageButton

    //Button to open the menu
    private lateinit var menuButton: ImageButton

    // Various text displayed
    private lateinit var mInfoTextView: TextView
    private lateinit var roomIdTextView: TextView

    private var roomId: Int = 0
    private lateinit var room: Room
    private var isHost: Boolean = false
    private var player: Int = 0

    //Game board view
    private lateinit var mBoardView: BoardViewOnline


    private inner class MTouchListener : View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            // Determine which cell was touched
            val col = event!!.x.toInt() / mBoardView.getBoardCellWidth()
            val row = event.y.toInt() / mBoardView.getBoardCellHeight()
            val pos = row * 3 + col

            println("TOUCHHHHH")

            if (room.players == 2 && (room.hostPlays == true && isHost || room.hostPlays == false && !isHost)) {
                if (room.gameOver == false && setMove(player, pos)) {
                    var winner: IntArray = mGame.checkForWinner()
                    checkWinner(winner, player)
                }
            }
            updateRoom()
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ponline)

        //Load database
        database = Firebase.database.reference
        //Listen for changes in Room
        database.addValueEventListener(postListener)

        //Create game
        mGame = TicTacToeGameOnline()

        mBoardView = findViewById(R.id.boardView)
        mBoardView.setGame(mGame)
        /* Listen for touches on the board */
        mBoardView.setOnTouchListener(MTouchListener())

        //Load Intent
        var intentRoomAct = intent
        roomId = intentRoomAct.getIntExtra("roomId", 0)
        isHost = intentRoomAct.getBooleanExtra("isHost", false)
        player = if (isHost) TicTacToeGameOnline.PLAYER_HOST else TicTacToeGameOnline.PLAYER_CLIENT
        getRoom()


        //TextViews
        mInfoTextView = findViewById<View>(R.id.information) as TextView




        roomIdTextView = findViewById(R.id.textID)
        roomIdTextView.text = roomId.toString()


        //Button to go back to home screen
        backButton = findViewById<View>(R.id.back_button) as ImageButton
        //Button to open the menu
        menuButton = findViewById<View>(R.id.menu_button) as ImageButton


        //Listener back button
        backButton.setOnClickListener {
            showFinishMatchDialog()
        }
        //Listener popup menu
        menuButton.setOnClickListener {
            showPopup(menuButton)
        }
    }
    //---------

    private fun setMove(player: Int, location: Int): Boolean {
        if (mGame.setMove(player, location)) {
            room.mBoard = (mGame.boardState).toList()
            room.hostPlays = !room.hostPlays!!
            mBoardView.invalidate()
            /*
            if (player == TicTacToeGameOnline.HUMAN_PLAYER) {
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

             */
            return true
        }
        return false
    }

    private fun checkWinner(winner: IntArray, player: Int) {
        when (winner[0]) {
            0 -> {

            }
            1 -> {
                mInfoTextView.setText(R.string.itsatie)
                room.gameOver = true
                /*
                //Play sound
                stopAllMPS()
                try {
                    mSoundsPlayer[5]?.start()
                } catch (e: IllegalStateException) {
                    println("Error al reproducir el sonido de TIE")
                }
                */
            }
            2 -> {
                if(isHost){
                    mInfoTextView.setText(R.string.youwon)

                }else{
                    mInfoTextView.setText("You loose!")
                }
                setWinner(winner)
                room.gameOver = true
                /*
                //Play sound
                stopAllMPS()
                try {
                    mSoundsPlayer[3]?.start()
                } catch (e: IllegalStateException) {
                    println("Error al reproducir el sonido de victoria")
                }
                 */
            }
            else -> {
                if(isHost){
                    mInfoTextView.setText("You lose!")

                }else{
                    mInfoTextView.setText(R.string.youwon)
                }
                setWinner(winner)
                room.gameOver = true

                /*

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
                 */

            }
        }
        updateRoom()
    }

    private fun setWinner(winner: IntArray) {
        mBoardView.setWinner(true)
        mBoardView.setWPos(winner)
        mBoardView.invalidate()
    }


    //------------

    override fun onBackPressed() {
        showFinishMatchDialog()
    }

    private fun getRoom() {
        //Get room from database
        println("ID: " + roomId.toString())
        database.child("rooms").child(roomId.toString()).get().addOnSuccessListener {
            room = it.getValue<Room>()!!
            if (isHost) {
                room.hostReady = true
                mInfoTextView.text = "Waiting for an opponent"
            } else {
                room.clientReady = true
                room.players = room.players?.plus(1)
            }
            updateRoom()
        }
    }

    val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            if (dataSnapshot.child("rooms").child(roomId.toString()).exists()) {
                room = dataSnapshot.child("rooms").child(roomId.toString()).getValue<Room>()!!
                mGame.setmBoard(room.mBoard?.toIntArray() ?: intArrayOf(0,0,0,0,0,0,0,0,0))

                mBoardView.invalidate()



                println("ROOM: " + room)
                if (room.clientReady == true && room.hostReady == true) {
                    println("ES TRUEEEEE")
                    if (room.hostPlays == true) {
                        if (isHost == true) {
                            mInfoTextView.text = "It's your turn"
                        } else {
                            mInfoTextView.text = "It's your opponent turn"
                        }
                    } else {
                        if (isHost == true) {
                            mInfoTextView.text = "It's your opponent turn"

                        } else {
                            mInfoTextView.text = "It's your turn"
                        }
                    }
                }

                var player2 = if (room.hostPlays == false) TicTacToeGameOnline.PLAYER_HOST else TicTacToeGameOnline.PLAYER_CLIENT

                var winner: IntArray = mGame.checkForWinner()
                checkWinner(winner, player2)

            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
        }
    }


    @SuppressLint("RestrictedApi", "DiscouragedPrivateApi")
    private fun showPopup(v: View) {
        val wrapper = ContextThemeWrapper(this, R.style.CustomPopupMenu)
        val popup = PopupMenu(wrapper, v, Gravity.END)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.options_menu_online, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.play_again -> {
                    //startNewGame()
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
        builder.setMessage(R.string.closeGameMatch)
        builder.setPositiveButton("Yes") { _, _ ->
            //Finish the match and close the game
            finishMatch()
            moveTaskToBack(true);
            exitProcess(-1)
        }
        builder.setNegativeButton("No") { _, _ ->
        }

        val mDialog = builder.create()

        mDialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#C1323232")));

        mDialog.show()
    }

    private fun showFinishMatchDialog() {
        val wrapper = ContextThemeWrapper(this, R.style.CustomAlertDialog)
        //Create dialog
        val builder = AlertDialog.Builder(wrapper)

        //Title
        val textView = TextView(this)
        textView.typeface = ResourcesCompat.getFont(this, R.font.minecraft_font2)
        textView.text = "Finish the match"
        textView.setPadding(0, 60, 0, 20)
        textView.textSize = 20f
        textView.gravity = Gravity.CENTER_HORIZONTAL
        textView.setTextColor(Color.WHITE)

        builder.setCustomTitle(textView)
        builder.setMessage(R.string.finishMatch)
        builder.setPositiveButton("Yes") { _, _ ->
            //Finish the match and go back
            finishMatch()
            finish()
        }
        builder.setNegativeButton("No") { _, _ ->
        }

        val mDialog = builder.create()

        mDialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#C1323232")));

        mDialog.show()
    }

    private fun updateRoom() {
        //Update values of match
        val childUpdates: HashMap<String, Any> = hashMapOf(
            "/rooms/$roomId" to room
        )
        database.updateChildren(childUpdates)
    }

    private fun finishMatch() {
        if (room != null) {
            if (isHost) {
                room.hostReady = false
                room.players = room.players?.minus(1)

            } else {
                room.clientReady = false
                room.players = room.players?.minus(1)
            }
            if (room.players == 0) {
                database.child("rooms").child(roomId.toString()).removeValue()
            } else {
                updateRoom()
            }
        }
    }


}