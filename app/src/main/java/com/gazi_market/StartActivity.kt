package com.gazi_market


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gazi_market.account.LoginActivity
import com.gazi_market.account.SignupActivity
import com.gazi_market.databinding.ActivityStartBinding
import com.google.firebase.auth.FirebaseAuth


class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        if (auth!!.currentUser != null) moveLogin()

        binding.startButton.setOnClickListener { moveSignUp() }
        binding.goLogin.setOnClickListener { moveLogin() }
    }

    private fun moveSignUp() {
        val intent = Intent(this, SignupActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun moveLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}