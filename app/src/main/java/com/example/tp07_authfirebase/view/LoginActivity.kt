package com.example.tp07_authfirebase.view

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.example.tp07_authfirebase.R
import com.example.tp07_authfirebase.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (FirebaseAuth.getInstance().currentUser != null) {
            openMain()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtGoRegister.text = getText(R.string.go_register)
        binding.txtGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.btnLogin.setOnClickListener { login() }
    }

    private fun login() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString()

        binding.tilEmail.error =
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) null else getString(R.string.error_email_invalid)
        binding.tilPassword.error =
            if (password.isNotEmpty()) null else getString(R.string.error_password_required)
        if (binding.tilEmail.error != null || binding.tilPassword.error != null) return

        binding.btnLogin.isEnabled = false
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { openMain() }
            .addOnFailureListener { e ->
                binding.btnLogin.isEnabled = true
                binding.tilPassword.error = getString(
                    if (e is FirebaseNetworkException) R.string.error_no_network else R.string.error_credentials
                )
            }
    }

    private fun openMain() {
        startActivity(
            Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }
}
