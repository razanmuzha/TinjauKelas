package com.example.tinjaukelas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val userRepository = UserRepository(this)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val user = userRepository.login(email, password)
            if (user != null) {
                // Simpan userId ke session
                getSharedPreferences("session", MODE_PRIVATE)
                    .edit()
                    .putInt("userId", user.id)
                    .putString("userRole", user.role)
                    .apply()

                startActivity(Intent(this, RoomActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Email atau password salah!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}