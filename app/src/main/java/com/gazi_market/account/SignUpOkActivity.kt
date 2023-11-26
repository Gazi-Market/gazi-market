package com.gazi_market.account

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gazi_market.MainActivity
import com.gazi_market.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignUpOkActivity : AppCompatActivity() {
    private var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_ok)
        auth = Firebase.auth

        val receivedIntent = intent // 현재 액티비티의 Intent를 가져옵니다.
        val receivedEmail = receivedIntent.getStringExtra("email")
        val receivedPassword = receivedIntent.getStringExtra("password")

        // 로그인 버튼
        val loginButton = findViewById<Button>(R.id.login)
        loginButton.setOnClickListener {
            if (receivedEmail != null && receivedPassword != null) {
                signIn(receivedEmail, receivedPassword)
            }
        }
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