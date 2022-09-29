package co.edu.unal.tictactoe

import com.google.firebase.database.IgnoreExtraProperties

val OPEN_SPOT: Int = 0

@IgnoreExtraProperties
data class Room(
    var players: Int? = 1,
    var privateRoom: Boolean? = false,
    var hostReady: Boolean? = false,
    var clientReady: Boolean? = false,
    var hostStarts: Boolean? = true,
    var hostPlays: Boolean? = true,
    var gameOver: Boolean? = false,
    var mBoard: List<Int>? = List(9) { OPEN_SPOT }
) {

}
