package com.gazi_market.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.gazi_market.MainActivity
import com.gazi_market.R
import com.gazi_market.StartActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {
    private var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth

        val backBtn = findViewById<ImageView>(R.id.img_btn_back)
        backBtn.setOnClickListener {
            startActivity(Intent(this@LoginActivity, StartActivity::class.java))
            finish()
        }

        val id = findViewById<EditText>(R.id.signupID)
        val pwd = findViewById<EditText>(R.id.signupPassword)

        // 로그인 버튼
        val loginButton = findViewById<Button>(R.id.signup)
        loginButton.setOnClickListener {
            signIn(id.text.toString(),pwd.text.toString())
        }

        // 초기에 버튼 비활성화
        loginButton.isEnabled = false

        // EditText 변경을 감지하여 버튼 상태 업데이트
        id.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateLoginButtonState(loginButton, id.text.toString(), pwd.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        pwd.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateLoginButtonState(loginButton, id.text.toString(), pwd.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun updateLoginButtonState(loginButton: Button, email: String, password: String) {
        // 두 필드가 모두 비어있지 않을 때만 버튼 활성화
        loginButton.isEnabled = email.isNotEmpty() && password.isNotEmpty()
    }

    // 로그아웃하지 않을 시 자동 로그인 , 회원가입시 바로 로그인 됨
    public override fun onStart() {
        super.onStart()
        moveMainPage(auth?.currentUser)
    }

    // 로그인
    private fun signIn(email: String, password: String) {

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext, "로그인에 성공 하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        moveMainPage(auth?.currentUser)
                    } else {
                        Toast.makeText(
                            baseContext, "로그인에 실패 하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }


    // 유저정보 넘겨주고 메인 액티비티 호출
    fun moveMainPage(user: FirebaseUser?){
        if( user!= null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }
}