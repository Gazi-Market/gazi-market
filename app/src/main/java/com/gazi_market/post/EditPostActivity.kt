package com.gazi_market.post

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.gazi_market.databinding.ActivityEditPostBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class EditPostActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditPostBinding
    private var selectedImageUri: Uri? = null
    private var documentId: String? = null
    private val db: FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            activityResult.launch(intent)
        }
        binding.registerBtn.setOnClickListener { saveEditedPost() }
        binding.imgBtnBack.setOnClickListener { onBackPressed() }

        documentId = intent.getStringExtra("documentId")
        loadPostData()
    }


    private fun loadPostData() {
        val postDocRef = documentId?.let { db.collection("posts").document(it) }

        postDocRef?.get()?.addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val title = document.getString("title")
                val content = document.getString("content")
                val price = document.getDouble("price")
                val isSoldOut = document.getBoolean("soldOut") == true
                val priceString = if (isSoldOut) "판매 완료" else price?.toInt().toString() + "원"
                val imageURL = document.getString("image") ?: "/image/logo.png"

                val storageReference = Firebase.storage.reference
                storageReference.child(imageURL).downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(this).load(uri).into(binding.selectImageView)
                }.addOnFailureListener { exception ->
                    Log.e(ContentValues.TAG, "이미지 다운로드 실패: $exception")
                }

                binding.editTitle.setText(title)
                binding.editContent.setText(content)
                binding.editPrice.setText(priceString.replace("원", ""))

            } else {
                Log.d(ContentValues.TAG, "No such document")
            }
        }
    }

    private fun getTimeAgo(time: Long): String {
        val SECOND_MILLIS: Long = 1000
        val MINUTE_MILLIS = 60 * SECOND_MILLIS
        val HOUR_MILLIS = 60 * MINUTE_MILLIS
        val DAY_MILLIS = 24 * HOUR_MILLIS
        val MONTH_MILLIS = 30 * DAY_MILLIS
        val now = System.currentTimeMillis()
        val diff = now - time

        return when {
            time > now || time <= 0 || diff < MINUTE_MILLIS -> "방금 전"
            diff < 2 * MINUTE_MILLIS -> "1분 전"
            diff < 50 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS}분 전"
            diff < 90 * MINUTE_MILLIS -> "1시간 전"
            diff < 24 * HOUR_MILLIS -> "${diff / HOUR_MILLIS}시간 전"
            diff < 48 * HOUR_MILLIS -> "어제"
            diff < 30 * DAY_MILLIS -> "${diff / DAY_MILLIS}일 전"
            diff < 2 * MONTH_MILLIS -> "한 달 전"
            else -> "${diff / MONTH_MILLIS}달 전"
        }
    }

    private fun saveEditedPost() {
        val title = binding.editTitle.text.toString()
        val price = binding.editPrice.text.toString().toIntOrNull() ?: 0
        val content = binding.editContent.text.toString()

        if (selectedImageUri != null) {
            uploadImageToFirebaseStorage(selectedImageUri!!) { imageName ->
                updatePostData(title, content, price, "image/$imageName")
            }
        } else {
            updatePostData(title, content, price, null)
        }

        onBackPressed()
    }

    private fun updatePostData(title: String, content: String, price: Int, imageUrl: String?) {
        val updatedData = hashMapOf<String, Any>(
            "title" to title, "content" to content, "price" to price
        )
        imageUrl?.let { updatedData["image"] = it }
        documentId?.let { docId -> db.collection("posts").document(docId).update(updatedData) }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri, callback: (String) -> Unit) {
        val filename = UUID.randomUUID().toString()
        val ref = Firebase.storage.reference.child("image/$filename")
        ref.putFile(imageUri).addOnSuccessListener { callback(filename) }
    }

    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode != RESULT_OK || it.data == null) return@registerForActivityResult
        selectedImageUri = it.data!!.data
        Glide.with(this).load(selectedImageUri).into(binding.selectImageView)
    }

    override fun onBackPressed() {
        val intent = Intent(this, DetailPostActivity::class.java)
        intent.putExtra("documentId", documentId)
        startActivity(intent)
        finish()
    }
}
