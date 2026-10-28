package com.example.tp07_authfirebase.view

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.example.tp07_authfirebase.R
import com.example.tp07_authfirebase.databinding.ActivityRegisterBinding
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtGoLogin.text = getText(R.string.go_login)
        binding.txtGoLogin.setOnClickListener { finish() }
        binding.btnRegister.setOnClickListener { register() }
    }

    private fun register() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString()
        val confirm = binding.edtConfirm.text.toString()

        binding.tilEmail.error =
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) null else getString(R.string.error_email_invalid)
        binding.tilPassword.error =
            if (password.length >= 6) null else getString(R.string.error_password_short)
        binding.tilConfirm.error =
            if (password == confirm) null else getString(R.string.error_password_mismatch)
        if (binding.tilEmail.error != null || binding.tilPassword.error != null || binding.tilConfirm.error != null) return

        binding.btnRegister.isEnabled = false
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                startActivity(
                    Intent(this, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
                finish()
            }
            .addOnFailureListener { e ->
                binding.btnRegister.isEnabled = true
                when (e) {
                    is FirebaseAuthUserCollisionException ->
                        binding.tilEmail.error = getString(R.string.error_email_in_use)
                    is FirebaseNetworkException ->
                        binding.tilPassword.error = getString(R.string.error_no_network)
                    else -> binding.tilPassword.error = getString(R.string.error_generic)
                }
            }
    }
}
