package com.gazi_market.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import com.gazi_market.MainActivity
import com.gazi_market.StartActivity
import com.gazi_market.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.imgBtnBack.setOnClickListener {
            startActivity(Intent(this@LoginActivity, StartActivity::class.java))
            finish()
        }
        binding.signup.isEnabled = false  // 초기에 버튼 비활성화
        binding.signup.setOnClickListener {
            signIn(binding.signupID.text.toString(), binding.signupPassword.text.toString())
        }
        binding.signupID.addTextChangedListener(textWatcher())
        binding.signupPassword.addTextChangedListener(textWatcher())
    }

    private fun textWatcher() = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val idString = binding.signupID.text.toString()
            val pwString = binding.signupPassword.text.toString()
            updateLoginButtonState(binding.signup, idString, pwString)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun updateLoginButtonState(loginButton: Button, email: String, password: String) {
        // 두 필드가 모두 비어있지 않을 때만 버튼 활성화
        loginButton.isEnabled = email.isNotEmpty() && password.isNotEmpty()
    }

    public override fun onStart() {
        super.onStart()
        moveMainPage(auth?.currentUser)
    }

    private fun signIn(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) return
        auth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(baseContext, "로그인에 성공 하였습니다.", Toast.LENGTH_SHORT).show()
                moveMainPage(auth?.currentUser)
            } else {
                Toast.makeText(baseContext, "로그인에 실패 하였습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun moveMainPage(user: FirebaseUser?) {
        if (user == null) return
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}