package com.gazi_market.account

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gazi_market.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {
    private var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContentView(R.layout.activity_signup)

        val signupID = findViewById<EditText>(R.id.signupID)
        val signupPwd = findViewById<EditText>(R.id.signupPassword)
        val signupPwdCheck = findViewById<EditText>(R.id.checkPassword)
        val signupNickName = findViewById<EditText>(R.id.nickName)

        val signupButton = findViewById<Button>(R.id.signup_okButton)

        var isID = false
        var isPWD = false
        var isPWD2 = false

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 밑에 나올 텍스트
                val checkID = findViewById<TextView>(R.id.checkID)
                val checkPWD = findViewById<TextView>(R.id.checkPWD)
                val checkPWD2 = findViewById<TextView>(R.id.checkPWD2)

                // 해당 EditText의 String
                val email = signupID.text.toString()
                val pwd = signupPwd.text.toString()
                val pwdCheck = signupPwdCheck.text.toString()
                val nickName = signupNickName.text.toString()

                if(email.isEmpty()){
                    checkID.visibility = View.GONE
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    // 이메일 형식이 아닌 경우
                    checkID.text = "X 올바르지 않은 이메일 주소 형식"
                    checkID.setTextColor(Color.parseColor("#FF0000"))
                    checkID.visibility = View.VISIBLE
                    isID = false
                } else {
                    // 이메일이 유효한 경우
                    checkID.text = "O 올바른 이메일 형식"
                    checkID.setTextColor(Color.parseColor("#009B00"))
                    checkID.visibility = View.VISIBLE
                    isID = true
                }

                val passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#\$%^&*])[a-zA-Z0-9!@#\$%^&*]{8,}\$".toRegex()
                if(pwd.isEmpty()){
                    checkPWD.visibility = View.GONE
                }
                else if (!passwordPattern.matches(pwd)) {
                    checkPWD.text = "X 영문/숫자/특수문자 조합으로 8자 이상"
                    checkPWD.setTextColor(Color.parseColor("#FF0000"))
                    checkPWD.visibility = View.VISIBLE
                    isPWD = false
                } else {
                    checkPWD.text = "O 영문/숫자/특수문자 조합으로 8자 이상"
                    checkPWD.setTextColor(Color.parseColor("#009B00"))
                    checkPWD.visibility = View.VISIBLE
                    isPWD = true

                }

                if(pwdCheck.isEmpty()){
                    checkPWD2.visibility = View.GONE
                }
                else if (!pwd.equals(pwdCheck)) {
                    checkPWD2.text = "X 비밀번호 불일치"
                    checkPWD2.setTextColor(Color.parseColor("#FF0000"))
                    checkPWD2.visibility = View.VISIBLE
                    isPWD2 = false
                } else {
                    checkPWD2.text = "O 비밀번호 일치"
                    checkPWD2.setTextColor(Color.parseColor("#009B00"))
                    checkPWD2.visibility = View.VISIBLE
                    isPWD2 = true
                }

                signupButton.isEnabled = isID && isPWD && isPWD2 && nickName.isNotEmpty()

            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }

        signupID.addTextChangedListener(textWatcher)
        signupPwd.addTextChangedListener(textWatcher)
        signupPwdCheck.addTextChangedListener(textWatcher)
        signupNickName.addTextChangedListener(textWatcher)

        signupButton.setOnClickListener {

            createAccount(signupID.text.toString(), signupPwd.text.toString())
        }
    }
    // 계정 생성
    private fun createAccount(email: String, password: String) {

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this, "계정 생성 완료.",
                            Toast.LENGTH_SHORT
                        ).show()
                        // 회원가입 화면으로
                        val intent = Intent(this, SignUpOkActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.putExtra("email", email)
                        intent.putExtra("password", password)
                        startActivity(intent)
                        //finish() // 가입창 종료
                    } else {
                        Toast.makeText(
                            this, "계정 생성 실패",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}