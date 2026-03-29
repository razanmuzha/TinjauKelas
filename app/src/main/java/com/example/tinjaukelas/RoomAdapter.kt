package com.example.tinjaukelas

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.tinjaukelas.R
import com.example.tinjaukelas.Room

class RoomAdapter(
    private var rooms: List<Room>,
    private val onRoomClick: (Room) -> Unit
) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    private var selectedRoomId: Int = -1

    inner class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView      = itemView.findViewById(R.id.cardView)
        val tvNamaKelas: TextView   = itemView.findViewById(R.id.tvNamaKelas)
        val tvKapasitas: TextView   = itemView.findViewById(R.id.tvKapasitas)
        val tvStatusBadge: TextView = itemView.findViewById(R.id.tvStatusBadge)
        val imgStatus: ImageView    = itemView.findViewById(R.id.imgStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = rooms[position]

        // Room name
        holder.tvNamaKelas.text = room.Kelas

        // Capacity — placeholder since no capacity column yet
        holder.tvKapasitas.text = "Kapasitas: -"

        // roomUsage = false → available (Tersedia)
        // roomUsage = true  → in use (Digunakan)
        if (room.Status) {
            holder.tvStatusBadge.text = "Digunakan"
            holder.tvStatusBadge.setBackgroundColor(Color.parseColor("#F44336")) // red
        } else {
            holder.tvStatusBadge.text = "Tersedia"
            holder.tvStatusBadge.setBackgroundColor(Color.parseColor("#4CAF50")) // green
        }

        // Highlight selected card with light purple
        if (room.id == selectedRoomId) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#EDE7F6"))
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE)
        }

        // Card click — select this room
        holder.cardView.setOnClickListener {
            selectedRoomId = room.id
            notifyDataSetChanged()
            onRoomClick(room)
        }
    }

    override fun getItemCount() = rooms.size

    // Call this after any DB change to refresh the list
    fun updateData(newRooms: List<Room>) {
        rooms = newRooms
        notifyDataSetChanged()
    }
}