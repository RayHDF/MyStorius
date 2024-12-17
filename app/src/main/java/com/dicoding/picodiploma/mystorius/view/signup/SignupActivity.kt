package com.dicoding.picodiploma.mystorius.view.signup

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
import com.dicoding.picodiploma.mystorius.databinding.ActivitySignupBinding
import com.dicoding.picodiploma.mystorius.view.ViewModelFactory

class SignupActivity : AppCompatActivity() {
    private val viewModel by viewModels<SignupViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.GONE

        setupView()
        setupAction()
        setupTextWatchers()
        observeViewModel()

        if (savedInstanceState != null) {
            binding.nameEditText.setText(savedInstanceState.getString("name"))
            binding.emailEditText.setText(savedInstanceState.getString("email"))
            binding.passwordEditText.setText(savedInstanceState.getString("password"))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("name", binding.nameEditText.text.toString())
        outState.putString("email", binding.emailEditText.text.toString())
        outState.putString("password", binding.passwordEditText.text.toString())
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
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            viewModel.register(name, email, password)
        }
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isNameValid = binding.nameEditText.text.toString().length >= 3
                val isEmailValid = binding.emailEditText.text.toString()
                    .isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(binding.emailEditText.text.toString())
                    .matches()
                val isPasswordValid = binding.passwordEditText.text.toString().length >= 8

                binding.signupButton.isEnabled = isNameValid && isEmailValid && isPasswordValid
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.nameEditText.addTextChangedListener(textWatcher)
        binding.emailEditText.addTextChangedListener(textWatcher)
        binding.passwordEditText.addTextChangedListener(textWatcher)

        binding.signupButton.isEnabled = false
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.successResponse.observe(this) { response ->
            AlertDialog.Builder(this).apply {
                setTitle("Yeah!")
                setMessage("Akun dengan ${response.message} sudah jadi nih. Yuk, login dan belajar coding.")
                setPositiveButton("Lanjut") { _, _ ->
                    finish()
                }
                create()
                show()
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