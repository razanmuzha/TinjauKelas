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
import android.view.View
import android.content.Intent
class RoomActivity : AppCompatActivity() {

    private lateinit var repository: RoomRepository
    private lateinit var adapter: RoomAdapter
    private lateinit var btnRoomUsage: Button
    private lateinit var btnAbsen: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: FloatingActionButton

    private var selectedRoom: Room? = null
    private var userId: Int = -1
    private var userRole: String = "siswa"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        userId = prefs.getInt("userId", -1)
        userRole = prefs.getString("userRole", "siswa") ?: "siswa"

        repository = RoomRepository(this)
        btnRoomUsage = findViewById(R.id.btnRoomUsage)
        recyclerView = findViewById(R.id.recyclerView)
        fabAdd  = findViewById(R.id.fabAdd)

        fabAdd.visibility = if (userRole == "admin") View.VISIBLE else View.GONE

        setupRecyclerView()
        loadRooms()
        setupButtons()
    }
    private fun setupRecyclerView() {
        adapter = RoomAdapter(emptyList()) { room ->
            selectedRoom = room
            updateButtonState(room)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadRooms() {
        val rooms = repository.getAllRooms()
        adapter.updateData(rooms)
    }

    private fun updateButtonState(room: Room) {
        btnRoomUsage.isEnabled = true
        if (room.Status) {
            btnRoomUsage.text = "Kosongkan Ruangan"
            btnRoomUsage.backgroundTintList =
                android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#F44336")
                )
        } else {
            btnRoomUsage.text = "Gunakan Ruangan"
            btnRoomUsage.backgroundTintList =
                android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#6200EE")
                )
        }
    }

    private fun setupButtons() {
        btnRoomUsage.setOnClickListener {
            val room = selectedRoom ?: return@setOnClickListener
            if (!room.Status) {
                val activeRoom = repository.getActiveRoomByUser(userId)
                if (activeRoom != null) {
                    Toast.makeText(
                        this,
                        "Kamu masih menempati ${activeRoom.Kelas}, keluar dulu!",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
            }
            val newStatus = !room.Status
            repository.updateRoomStatus(room.id, newStatus)
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

        fabAdd.setOnClickListener {
            showAddRoomDialog()
        }

        btnAbsen.setOnClickListener {
            //TODO: implementasi absensi untuk guru
            //val intent = Intent(this, AbsenActivity::class.java)
            //intent.putExtra("guruId", userId)
            //startActivity(intent)
                Toast.makeText(this, "Fitur absensi belum tersedia", Toast.LENGTH_SHORT).show()
        }
    }



    private fun showAddRoomDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialogform, null)
        val etNama      = dialogView.findViewById<TextInputEditText>(R.id.etNama)
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
            val newRoom = Room(Kelas = nama, Status = false, userId = userId)
            repository.insertRoom(newRoom)
            Toast.makeText(this, "$nama berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            loadRooms()
        }
        dialog.show()
    }
}
