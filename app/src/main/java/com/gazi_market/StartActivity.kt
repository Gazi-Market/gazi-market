package com.gazi_market


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.gazi_market.account.LoginActivity
import com.gazi_market.account.SignupActivity
import com.google.firebase.auth.FirebaseAuth


class StartActivity : AppCompatActivity() {
    private var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        auth = FirebaseAuth.getInstance()

        // 회원가입
        val logoutbutton = findViewById<Button>(R.id.startButton)
        logoutbutton.setOnClickListener {
            // 회원가입 화면으로
            val intent = Intent(this, SignupActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        // 로그인
        val signin = findViewById<TextView>(R.id.go_login)
        signin.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }


    }
}