package co.edu.unal.tictactoe

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class RoomAdapter(val roomList: List<Int>) : RecyclerView.Adapter<RoomAdapter.RoomHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return RoomHolder(layoutInflater.inflate(R.layout.item_room, parent, false))
    }

    override fun onBindViewHolder(holder: RoomHolder, position: Int) {
        holder.render(roomList[position])
    }

    override fun getItemCount(): Int {
        return roomList.size
    }

    class RoomHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun render(room: Int) {
            val idRoom : TextView = view.findViewById(R.id.room_id_val)
            val buttonJoin : ImageButton = view.findViewById(R.id.join_room)
            idRoom.text = room.toString()


            buttonJoin.setOnClickListener {
                var context = view.getContext();

                val intent = Intent(context, POnlineActivity::class.java).apply {
                    putExtra("roomId", room)
                    putExtra("isHost", false)
                }
                context.startActivity(intent)

            }
        }
    }

}