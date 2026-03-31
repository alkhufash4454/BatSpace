package com.batspace.app.ui.lock

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.batspace.app.databinding.ActivityLockBinding
import com.batspace.app.data.prefs.ClonePreferences
import com.batspace.app.data.repository.CloneRepository

class LockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLockBinding
    private val prefs by lazy { ClonePreferences(this) }
    private val repo by lazy { CloneRepository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnUnlock.setOnClickListener {
            val pin = binding.etPin.text.toString()
            if (pin.isBlank()) { Toast.makeText(this, "Enter PIN", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            val enteredHash = repo.hashPin(pin)
            if (enteredHash == prefs.getGlobalPin()) {
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show()
                binding.etPin.text?.clear()
            }
        }

        binding.btnBiometric.setOnClickListener { showBiometricPrompt() }
        binding.btnCancel.setOnClickListener { finish() }
    }

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                setResult(RESULT_OK); finish()
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                Toast.makeText(applicationContext, errString, Toast.LENGTH_SHORT).show()
            }
            override fun onAuthenticationFailed() {
                Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        }
        val prompt = BiometricPrompt(this, executor, callback)
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Bat Space Lock")
            .setSubtitle("Authenticate to access this clone")
            .setNegativeButtonText("Use PIN")
            .build()
        prompt.authenticate(info)
    }
}
