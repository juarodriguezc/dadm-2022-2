package co.edu.unal.tictactoe

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.analytics.FirebaseAnalytics

class HomeScreenActivity : AppCompatActivity() {

    private lateinit var buttonSinglePlayer: Button
    private lateinit var buttonMLocal: Button
    private lateinit var buttonMOnline: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        buttonSinglePlayer = findViewById<View>(R.id.pAndroidButton) as Button
        buttonMLocal = findViewById<View>(R.id.pMLocalButton) as Button
        buttonMOnline = findViewById<View>(R.id.pMOnlineButton) as Button

        //Listener for singleplayer button
        buttonSinglePlayer.setOnClickListener {
            playAndroidA.launch(Intent(this, PAndroidActivity::class.java))
        }
        //Listener for multiplayer local button
        buttonMLocal.setOnClickListener {
            var url = "https://www.youtube.com/watch?v=QDia3e12czc&ab_channel=Everythingistroll"
            val intentApp = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intentApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intentApp.setPackage("com.google.android.youtube")
            val intentBrowser = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            try {
                this.startActivity(intentApp)
            } catch (ex: ActivityNotFoundException) {
                this.startActivity(intentBrowser)
            }
        }
        //Listener for multiplayer online button
        buttonMOnline.setOnClickListener {
            playAndroidRA.launch(Intent(this, OnlineRoomActivity::class.java))
        }

    }

    private val playAndroidA = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
        }
    }

    private val playAndroidRA = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
        }
    }

}