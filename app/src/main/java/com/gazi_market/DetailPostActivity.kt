package com.gazi_market

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.bumptech.glide.Glide
import com.gazi_market.databinding.ActivityDetailPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DetailPostActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailPostBinding
    lateinit var myUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = FirebaseFirestore.getInstance()
        myUid = FirebaseAuth.getInstance().currentUser?.uid!!

        // documentId 가져오기
        val documentId = intent.getStringExtra("documentId").toString() ?: ""

        // posts 컬렉션에서 documentId에 해당하는 문서 참조 가져오기
        val postDocRef = db.collection("posts").document(documentId)

        // users 컬렉션에서 myUid에 해당하는 문서 참조 가져오기
        val userDocRef = db.collection("users").document(myUid)

        userDocRef.get()
            .addOnSuccessListener { document ->
                val nickname = document.getString("name")

                findViewById<TextView>(R.id.nicknameTextView).text =
                    nickname ?: "Nickname is null"
            }
        // 문서 가져오기
        postDocRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val title = document.getString("title")
                    val content = document.getString("content")
                    val price = document.getDouble("price")
                    val isSoldOut = document.getBoolean("soldOut") // TODO: 수정 필요
                    val createdAt = document.getTimestamp("createdAt")
                    val formattedDate = createdAt?.toDate()?.let { getTimeAgo(it.time) }
                    val imageURL = document.getString("image")

                    if (!imageURL.isNullOrEmpty()) {
                        val storageReference = Firebase.storage.reference
                        storageReference.child(imageURL).downloadUrl
                            .addOnSuccessListener { uri ->
                                Glide.with(this)
                                    .load(uri)
                                    .into(binding.productImageView)
                            }
                            .addOnFailureListener { exception ->
                                Log.e(TAG, "이미지 다운로드 실패: $exception")
                            }
                    }
                    // imageURL이 없을 때 처리 (기본 이미지 표시 등)
                    else {

                    }

                    // 데이터가 null이 아닌지 확인 후 TextView에 설정
                    findViewById<TextView>(R.id.titleTextView).text = title ?: "Title is null"
                    findViewById<TextView>(R.id.contentTextView).text = content ?: "Content is null"
                    findViewById<TextView>(R.id.createdAtTextView).text =
                        formattedDate ?: "날짜가 없습니다"
                    findViewById<TextView>(R.id.priceTextView).text =
                        if (isSoldOut == true) "판매 완료" else price?.toInt().toString() + "원"
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
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
}