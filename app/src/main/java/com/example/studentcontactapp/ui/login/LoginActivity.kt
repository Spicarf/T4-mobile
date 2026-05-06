package com.example.studentcontactapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studentcontactapp.databinding.ActivityLoginBinding
import com.example.studentcontactapp.ui.home.MainActivity
import com.example.studentcontactapp.utils.PrefManager

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PrefManager(this)

        // Check remember me
        if (prefManager.isRememberMe() && prefManager.isLoggedIn()) {
            navigateToMain()
            return
        }

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()

            if (username == "admin" && password == "123456") {
                prefManager.saveLoginSession(username)

                if (binding.cbRememberMe.isChecked) {
                    prefManager.setRememberMe(true)
                }

                navigateToMain()
            } else {
                Toast.makeText(this, "Username atau password salah!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}