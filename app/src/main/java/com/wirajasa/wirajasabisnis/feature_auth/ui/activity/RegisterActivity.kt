package com.wirajasa.wirajasabisnis.feature_auth.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import com.wirajasa.wirajasabisnis.R
import com.wirajasa.wirajasabisnis.databinding.ActivityRegisterBinding
import com.wirajasa.wirajasabisnis.feature_buyer.ui.activity.MainActivity
import com.wirajasa.wirajasabisnis.feature_auth.ui.viewmodel.RegisterViewModel
import com.wirajasa.wirajasabisnis.feature_auth.domain.usecases.Validate
import com.wirajasa.wirajasabisnis.core.utility.NetworkResponse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private val viewModel by viewModels<RegisterViewModel>()
    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.apply {
            btnRegister.setOnClickListener(this@RegisterActivity)
            tvClickHaveAccount.setOnClickListener(this@RegisterActivity)

            edtEmail.addTextChangedListener {
                layoutEmail.error?.let {
                    layoutEmail.isErrorEnabled = false
                }
            }
            edtPassword.addTextChangedListener {
                layoutPassword.error?.let {
                    layoutPassword.isErrorEnabled = false
                }
            }
            edtConfirmPassword.addTextChangedListener {
                layoutConfirmPassword.error?.let {
                    layoutConfirmPassword.isErrorEnabled = false
                }
            }
        }
    }

    override fun onClick(v: View?) {
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()
        val confirmedPassword = binding.edtConfirmPassword.text.toString()
        when (v?.id) {
            binding.btnRegister.id -> {
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    currentFocus?.windowToken, 0
                )

                if (!Validate().email(email)) binding.layoutEmail.error =
                    getString(R.string.tv_empty_invalid_email)

                if (!Validate().password(password)) binding.layoutPassword.error =
                    getString(R.string.tv_empty_password)

                if (!Validate().password(confirmedPassword)) {
                    binding.layoutConfirmPassword.error =
                        getString(R.string.tv_empty_confirmation_password)
                    return
                }

                if (password != confirmedPassword) {
                    binding.layoutConfirmPassword.error =
                        getString(R.string.tv_different_password)
                    return
                }

                viewModel.signUpWithEmailAndPassword(email, password).observe(this) { response ->
                    when (response) {
                        is NetworkResponse.GenericException -> {
                            response.cause?.let { showToast(it) }
                            showLoading(false)
                        }
                        is NetworkResponse.Loading -> {
                            response.status?.let { binding.tvLoading.text = it }
                            if (binding.pbLoading.visibility != View.VISIBLE) showLoading(true)
                        }
                        is NetworkResponse.Success -> {
                            showToast(getString(R.string.tv_welcome_user, response.data.username))
                            val intent = Intent(this, MainActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                        }
                    }
                }
            }
            binding.tvClickHaveAccount.id -> finish()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.apply {
            btnRegister.visibility = View.INVISIBLE
            layoutClickLogin.visibility = View.INVISIBLE
            pbLoading.visibility = View.VISIBLE
            tvLoading.visibility = View.VISIBLE
        } else binding.apply {
            btnRegister.visibility = View.VISIBLE
            layoutClickLogin.visibility = View.VISIBLE
            pbLoading.visibility = View.GONE
            tvLoading.visibility = View.GONE
        }
    }
}