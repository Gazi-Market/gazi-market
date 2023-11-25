package com.gazi_market

import android.content.Intent
import android.net.Uri
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.sql.Timestamp
import java.util.Date
import java.util.UUID

class PostActivity : AppCompatActivity() {

    lateinit var binding: ActivityPostBinding
    private var selectedImageUri: Uri? = null

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

            // 이미지 파이어베이스 스토리지에 저장
            if(selectedImageUri!=null){
                selectedImageUri?.let { uri ->
                    uploadImageToFirebaseStorage(uri) { imageName ->
                    }
                }
            }

            val title = findViewById<EditText>(R.id.editTitle).text.toString()
            val price = findViewById<EditText>(R.id.editPrice).text.toString().toInt()
            val content = findViewById<EditText>(R.id.editContent).text.toString()
            val currentTimeStamp = Timestamp(Date().time)
            val db = FirebaseFirestore.getInstance()
            val postsCollection = db.collection("posts")
            val newDocumentRef = postsCollection.document()

            val nickname = FirebaseAuth.getInstance().currentUser?.uid!!.toString()

            if(selectedImageUri!=null){
                selectedImageUri?.let { uri ->
                    uploadImageToFirebaseStorage(uri) { imageName ->
                        val postData = PostData(
                            documentId = newDocumentRef.id,
                            nickname = nickname,
                            title = title,
                            content = content,
                            price = price,
                            soldOut = false,
                            createdAt = currentTimeStamp,
                            image = "image/${imageName}"
                        )
                        createPost(newDocumentRef, postData)
                    }
                }
            }
        }
    }

    // 이미지 업로드 후 이미지 이름을 반환하는 콜백
    private fun uploadImageToFirebaseStorage(imageUri: Uri, callback: (String) -> Unit) {
        val filename = UUID.randomUUID().toString()
        val ref = Firebase.storage.reference.child("image").child("$filename")

        ref.putFile(imageUri)
            .addOnSuccessListener {
                callback(filename) // 이미지 업로드 성공 시 콜백 호출
            }
            .addOnFailureListener {
                // 업로드 실패 시 로직
            }
    }

    // 이미지 가져오기
    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK && it.data != null) {
            selectedImageUri = it.data!!.data
            Glide.with(this)
                .load(selectedImageUri)
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