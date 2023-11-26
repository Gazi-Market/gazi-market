package com.gazi_market

import android.content.ContentValues.TAG
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.content.Intent
import android.view.View
import android.widget.TextView
import com.gazi_market.chat.ChattingActivity
import com.bumptech.glide.Glide
import com.gazi_market.databinding.ActivityDetailPostBinding
import com.gazi_market.model.ChatRoom
import com.gazi_market.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import android.widget.PopupMenu
import android.view.ContextThemeWrapper

class DetailPostActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailPostBinding
    lateinit var postUser: User
    private val db: FirebaseFirestore = Firebase.firestore
    lateinit var postUserUid: String
    private var imageURL: String? = null
    private var isSoldOut: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backBtn = findViewById<ImageView>(R.id.img_btn_back)
        backBtn.setOnClickListener { onBackPressed() }

        val etcBtn = findViewById<ImageView>(R.id.img_btn_etc)
        etcBtn.setOnClickListener {
            val popupMenu = PopupMenu(ContextThemeWrapper(this, R.style.PopupMenuStyle), etcBtn)
            popupMenu.menuInflater.inflate(R.menu.post_detail_menu, popupMenu.menu)
            popupMenu.setForceShowIcon(true)
            popupMenu.show()
        }

        val db = FirebaseFirestore.getInstance()
        val documentId = intent.getStringExtra("documentId").toString() ?: ""
        val postDocRef = db.collection("posts").document(documentId)

        postDocRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                postUserUid = document.getString("uid").toString()
                isSoldOut = document.getBoolean("soldOut") ?: false
                imageURL = document.getString("image") ?: "/image/logo.png"
                updatePopupMenu(isSoldOut)
                val title = document.getString("title")
                val content = document.getString("content")
                val price = document.getDouble("price")
                val createdAt = document.getTimestamp("createdAt")
                val formattedDate = createdAt?.toDate()?.let { getTimeAgo(it.time) }
                val storageReference = Firebase.storage.reference
                storageReference.child(imageURL!!).downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(this).load(uri).into(binding.productImageView)
                }.addOnFailureListener { exception ->
                    Log.e(TAG, "이미지 다운로드 실패: $exception")
                }

                // 데이터가 null이 아닌지 확인 후 TextView에 설정
                findViewById<TextView>(R.id.titleTextView).text = title ?: "Title is null"
                findViewById<TextView>(R.id.contentTextView).text = content ?: "Content is null"
                findViewById<TextView>(R.id.createdAtTextView).text =
                    formattedDate ?: "날짜가 없습니다"
                findViewById<TextView>(R.id.priceTextView).text =
                    if (isSoldOut == true) "판매 완료" else price?.toInt().toString() + "원"

                // post를 작성한 유저 가져오기
                getPostUser()
            } else {
                Log.d(TAG, "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d(TAG, "get failed with ", exception)
        }

        binding.registerBtn.setOnClickListener {
            addChatRoom(documentId)
        }

        binding.productImageView.setOnClickListener {
            val intent = Intent(this, ImageZoomActivity::class.java)
            intent.putExtra("imageURL", imageURL) // 이미지 URL 전달
            startActivity(intent)
        }
    }

    private fun updatePopupMenu(soldOut: Boolean) {
        val etcBtn = findViewById<ImageView>(R.id.img_btn_etc)
        etcBtn.setOnClickListener {
            val popupMenu = PopupMenu(ContextThemeWrapper(this, R.style.PopupMenuStyle), etcBtn)
            popupMenu.menuInflater.inflate(R.menu.post_detail_menu, popupMenu.menu)

            val titleString = if (isSoldOut) "판매중으로 변경" else "판매완료로 변경"
            popupMenu.menu.findItem(R.id.menu_mark_sold).title = titleString

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_mark_sold -> {
                        toggleSoldOutStatus()
                        finish()
                        return@setOnMenuItemClickListener true
                    }

                    R.id.menu_edit -> {
                        val documentId = intent.getStringExtra("documentId").toString()
                        val editIntent = Intent(this, EditPostActivity::class.java)
                        editIntent.putExtra("documentId", documentId)
                        startActivity(editIntent)
                        return@setOnMenuItemClickListener true
                    }

                    else -> return@setOnMenuItemClickListener false
                }
            }
            popupMenu.show()
        }
    }

    private fun toggleSoldOutStatus() {
        val newStatus = !isSoldOut
        val documentId = intent.getStringExtra("documentId").toString()

        db.collection("posts").document(documentId)
            .update("soldOut", newStatus)
            .addOnSuccessListener {
                isSoldOut = newStatus
                updatePopupMenu(isSoldOut)
                // UI 업데이트 또는 사용자에게 상태 변경 알림
            }
            .addOnFailureListener { e ->
                // 오류 처리
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

    fun getPostUser() {
        val user = Firebase.auth.currentUser

        db.collection("users").document(postUserUid).get().addOnSuccessListener { result ->
            postUser = result.toObject(User::class.java)!!
            findViewById<TextView>(R.id.nicknameTextView).text =
                postUser.name ?: "Nickname is null"

            if (postUser.uid == user?.uid) {
                binding.registerBtn.visibility = View.GONE
            }
        }.addOnFailureListener { exception ->
            // 실패 시 처리
        }
    }

    fun addChatRoom(documentId: String) {
        val user = Firebase.auth.currentUser
        val chatRoom = ChatRoom(
            mapOf(
                user?.uid!! to true,
                postUser.uid!! to true,
            ), null, documentId
        )

        db.collection("chatRoom").whereEqualTo("users.${user?.uid}", true)
            .whereEqualTo("users.${postUser.uid}", true).get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Log.d("chatRoom_TEST", "Empty")
                    // 채팅방이 없는 경우 새로운 채팅방 생성
                    db.collection("chatRoom").add(chatRoom)
                        .addOnSuccessListener { documentReference ->
                            goToChatRoom(chatRoom, postUser)
                        }.addOnFailureListener { e ->
                            // 실패 시 처리
                        }
                } else {
                    val existingChatRoom = querySnapshot.documents[0].toObject(ChatRoom::class.java)
                    goToChatRoom(existingChatRoom!!, postUser)
                }
            }.addOnFailureListener { e ->
                // 실패 시 처리
            }
    }

    fun goToChatRoom(chatRoom: ChatRoom, opponentUid: User) {       //채팅방으로 이동
        var intent = Intent(this@DetailPostActivity, ChattingActivity::class.java)
        intent.putExtra("ChatRoom", chatRoom)       //채팅방 정보
        intent.putExtra("Opponent", opponentUid)    //상대방 정보
        intent.putExtra("ChatRoomKey", "")   //채팅방 키
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}