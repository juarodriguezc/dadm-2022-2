package co.edu.unal.tictactoe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlin.random.Random


class OnlineRoomActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    //Button to go back to home screen
    private lateinit var backButton: ImageButton

    //Button to go back to create a room
    private lateinit var createRoomButton: ConstraintLayout
    private lateinit var rvRoomList: RecyclerView



    //Hashmap with the rooms
    private var hashRooms: HashMap<String, Room> = HashMap()
    private var listRooms: MutableList<Int> = mutableListOf()

    //Max value for rooms
    private val RMINROOMS = 1000
    private val RMAXNROOMS = 10000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_room)


        //Load database
        database = Firebase.database.reference

        //Get rooms on load
        getRooms()

        //Button to go back to home screen
        backButton = findViewById<View>(R.id.back_button) as ImageButton

        rvRoomList = findViewById<RecyclerView>(R.id.rvRoomList)

        //Button to create a room
        createRoomButton = findViewById(R.id.createRoomConstraint)

        //Listener back button
        backButton.setOnClickListener {
            onBackPressed()
        }

        //listener create room button
        createRoomButton.setOnClickListener {
            createRoom()
        }
    }

    private fun getRooms() {
        //Get rooms from database
        database.child("rooms").get().addOnSuccessListener {
            //Clear the hash to get new values
            hashRooms.clear()
            it.children.forEach { it2 ->
                val room = it2.getValue(Room::class.java)
                if (room != null) {
                    if (room.players == 1) {
                        hashRooms[it2.key.toString()] = room
                        listRooms.add(it2.key.toString().toInt())
                    }
                }
            }
            //Update menu with rooms
            updateMenu()

        }.addOnFailureListener {
            println("firebase" + "Error getting data")
        }
    }

    private fun updateMenu() {
        rvRoomList.layoutManager = LinearLayoutManager(this)
        val adapter = RoomAdapter(listRooms)
        rvRoomList.adapter = adapter
    }

    private fun createRoom() {
        //Create empty room
        val room = Room(privateRoom = false)

        //Run transaction
        database.child("rooms").runTransaction(object : Transaction.Handler {
            var roomId = RMAXNROOMS
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                var keySet = mutableSetOf<Int>()
                currentData.children.forEach {
                    it.key?.let { it1 ->
                        keySet.add(it1.toInt())
                    }
                }
                roomId = (RMINROOMS until RMAXNROOMS).random()
                var nTry = 0
                while (keySet.contains(roomId)) {
                    if (nTry > RMAXNROOMS) {
                        roomId = RMAXNROOMS
                        println("ERROR, número de partidas alcanzadas")
                        break
                    }
                    roomId = (RMINROOMS until RMAXNROOMS).random()
                    nTry++
                }

                currentData.child(roomId.toString()).value = room
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                goToRoom(roomId, true)
            }

        })
    }

    private fun goToRoom(roomId: Int, isHost: Boolean) {
        var intentHostAct = Intent(this, POnlineActivity::class.java)
        intentHostAct.putExtra("roomId", roomId)
        intentHostAct.putExtra("isHost", isHost)
        playOnlineA.launch(intentHostAct)
    }
    private val playOnlineA =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
            }
        }
}