package com.gazi_market

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.gazi_market.databinding.ActivityPostBinding
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.sql.Timestamp
import java.util.Date

class PostActivity : AppCompatActivity() {

    lateinit var binding: ActivityPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 설정
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val customToolbar = layoutInflater.inflate(R.layout.custom_post_toolbar, null)
        val toolbarTitle = customToolbar.findViewById<TextView>(R.id.toolbar_title)
        toolbarTitle.text = "판매글 등록"
        toolbar.addView(customToolbar)
        setSupportActionBar(toolbar) // 액션바로 설정

        // 이미지 처리
        binding.selectImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            activityResult.launch(intent)
        }

        // 등록하기 버튼
        binding.registerBtn.setOnClickListener {
            // 등록 내용 가져오기
            val title = findViewById<EditText>(R.id.editTitle).text.toString()
            val price = findViewById<EditText>(R.id.editPrice).text.toString().toInt()
            val content = findViewById<EditText>(R.id.editContent).text.toString()
            val currentTimeStamp = Timestamp(Date().time)

            val db = FirebaseFirestore.getInstance()
            val postsCollection = db.collection("posts")
            val newDocumentRef = postsCollection.document()

            val postData = PostData(
                documentId = newDocumentRef.id,
                nickname = "예시닉네임",
                title = title,
                content = content,
                price = price,
                soldOut = false,
                createdAt = currentTimeStamp,
                image = "/image/abc"
            )
            createPost(newDocumentRef, postData)
        }
    }

    // 이미지 가져오기
    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK && it.data != null) {
            val uri = it.data!!.data
            Glide.with(this)
                .load(uri)
                .into(binding.selectImageView)
        }
    }

    // 데이터 등록
    private fun createPost(documentRef: DocumentReference, data: PostData) {
        documentRef.set(data).addOnSuccessListener {
            val documentId = documentRef.id
            val intent = Intent(this@PostActivity, DetailPostActivity::class.java)
            intent.putExtra("documentId", documentId)
            startActivity(intent)
            Log.d("SUCCESS", "데이터 저장 성공")
        }.addOnFailureListener {
            Log.d("FAIL", "데이터 저장 실패")
        }
    }
}