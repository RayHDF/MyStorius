package com.dicoding.picodiploma.mystorius.view.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.mystorius.data.pref.UserModel
import com.dicoding.picodiploma.mystorius.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.mystorius.view.ViewModelFactory
import com.dicoding.picodiploma.mystorius.view.main.MainActivity
import com.dicoding.picodiploma.mystorius.view.stories.StoriesActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        setupTextWatchers()
        observeViewModel()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.login(email, password)
        }
    }

    private fun setupTextWatchers() {
        val emailTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEmailValid = binding.emailEditText.text.toString().isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(binding.emailEditText.text.toString()).matches()
                binding.loginButton.isEnabled = isEmailValid
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.emailEditText.addTextChangedListener(emailTextWatcher)
        binding.loginButton.isEnabled = false
    }

    private fun observeViewModel() {
        viewModel.loginResponse.observe(this) { response ->
            response.loginResult?.let {
                viewModel.saveSession(UserModel(it.name ?: "", it.token ?: ""))
                AlertDialog.Builder(this).apply {
                    setTitle("Yeah!")
                    setMessage("Anda berhasil login. Sudah tidak sabar untuk belajar ya?")
                    setPositiveButton("Lanjut") { _, _ ->
                        val intent = Intent(context, StoriesActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    create()
                    show()
                }
            }
        }

        viewModel.error.observe(this) { error ->
            AlertDialog.Builder(this).apply {
                setTitle("Error")
                setMessage(error)
                setPositiveButton("OK", null)
                create()
                show()
            }
        }
    }
}