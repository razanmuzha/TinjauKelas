package com.example.tinjaukelas

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tinjaukelas.RoomAdapter
import com.example.tinjaukelas.Room
import com.example.tinjaukelas.RoomRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class RoomActivity : AppCompatActivity() {

    private lateinit var repository: RoomRepository
    private lateinit var adapter: RoomAdapter
    private lateinit var btnRoomUsage: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: FloatingActionButton

    // Tracks whichever card the user tapped
    private var selectedRoom: Room? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository = RoomRepository(this)

        btnRoomUsage = findViewById(R.id.btnRoomUsage)
        recyclerView = findViewById(R.id.recyclerView)

        setupRecyclerView()
        loadRooms()
        setupButtons()
    }

    // ── RECYCLERVIEW SETUP ───────────────────────────────────────────
    private fun setupRecyclerView() {
        adapter = RoomAdapter(emptyList()) { room ->
            // Called when a card is tapped
            selectedRoom = room
            updateButtonState(room)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    // ── LOAD ROOMS FROM DB ───────────────────────────────────────────
    private fun loadRooms() {
        val rooms = repository.getAllRooms()
        adapter.updateData(rooms)
    }

    // ── UPDATE BUTTON TEXT & COLOR BASED ON ROOM STATUS ─────────────
    private fun updateButtonState(room: Room) {
        btnRoomUsage.isEnabled = true

        // roomUsage = false → available → show "Gunakan Ruangan"
        // roomUsage = true  → in use   → show "Kosongkan Ruangan"
        if (room.Status) {
            btnRoomUsage.text = "Kosongkan Ruangan"
            btnRoomUsage.backgroundTintList =
                android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#F44336") // red
                )
        } else {
            btnRoomUsage.text = "Gunakan Ruangan"
            btnRoomUsage.backgroundTintList =
                android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#6200EE") // purple
                )
        }
    }

    // ── BUTTONS ──────────────────────────────────────────────────────
    private fun setupButtons() {
        // Toggle room status when main button is clicked
        btnRoomUsage.setOnClickListener {
            val room = selectedRoom ?: return@setOnClickListener
            val newStatus = !room.Status         // flip the boolean
            repository.updateRoomStatus(room.id, newStatus)
            fabAdd = findViewById(R.id.fabAdd)
            fabAdd.setOnClickListener {
                showAddRoomDialog()
            }

            // Update local reference so button reflects new status
            selectedRoom = room.copy(Status = newStatus)

            Toast.makeText(
                this,
                if (newStatus) "${room.Kelas} sedang digunakan"
                else "${room.Kelas} sudah kosong",
                Toast.LENGTH_SHORT
            ).show()

            loadRooms()
            updateButtonState(selectedRoom!!)
        }
        fabAdd = findViewById(R.id.fabAdd)
        fabAdd.setOnClickListener {
            showAddRoomDialog()
        }
    }


    private fun showAddRoomDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialogform, null)
        val etNama      = dialogView.findViewById<TextInputEditText>(R.id.etNama)
        val etDeskripsi = dialogView.findViewById<TextInputEditText>(R.id.etDeskripsi) // placeholder, not saved to DB
        val btnBatal    = dialogView.findViewById<Button>(R.id.btnBatal)
        val btnSimpan   = dialogView.findViewById<Button>(R.id.btnSimpan)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnBatal.setOnClickListener {
            dialog.dismiss()
        }

        btnSimpan.setOnClickListener {
            val nama = etNama.text.toString().trim()

            if (nama.isEmpty()) {
                etNama.error = "Nama tidak boleh kosong"
                return@setOnClickListener
            }

            // New room always starts as available (false)
            val newRoom = Room(
                Kelas  = nama,
                Status = false,
                userId    = 1       // replace with actual logged-in user ID later
            )
            repository.insertRoom(newRoom)

            Toast.makeText(this, "$nama berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            loadRooms()
        }

        dialog.show()
    }
}
