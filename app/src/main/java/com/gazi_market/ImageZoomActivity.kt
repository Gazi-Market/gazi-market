package com.gazi_market

// ImageZoomActivity.kt
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ImageZoomActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_zoom)

        val imageView = findViewById<ImageView>(R.id.image_zoom_view)

        // 인텐트에서 이미지 URL을 가져옵니다.
        val imageURL = intent.getStringExtra("imageURL")

        if (!imageURL.isNullOrEmpty()) {
            val storageReference = Firebase.storage.reference
            storageReference.child(imageURL!!).downloadUrl
                .addOnSuccessListener { uri ->
                    Glide.with(this)
                        .load(uri)
                        .into(imageView)
                }
                .addOnFailureListener { exception ->
                    Log.e(ContentValues.TAG, "이미지 다운로드 실패: $exception")
                }
        }

        // 뒤로 가기 버튼 처리
        imageView.setOnClickListener {
            finish()
        }
    }
}
