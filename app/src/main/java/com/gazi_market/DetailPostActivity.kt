package com.gazi_market

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.gazi_market.chat.AddChatRoomActivity
import com.gazi_market.chat.ChattingActivity
import com.gazi_market.databinding.ActivityDetailPostBinding
import com.gazi_market.model.ChatRoom
import com.gazi_market.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DetailPostActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailPostBinding
    lateinit var postUser: User
    private val db : FirebaseFirestore = Firebase.firestore
    lateinit var nickname : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = FirebaseFirestore.getInstance()

        // documentId 가져오기
        val documentId = intent.getStringExtra("documentId").toString() ?: ""

        // posts 컬렉션에서 documentId에 해당하는 문서 참조 가져오기
        val docRef = db.collection("posts").document(documentId)

        // 문서 가져오기
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    nickname = document.getString("nickname").toString()
                    val title = document.getString("title")
                    val content = document.getString("content")
                    val price = document.getDouble("price")
                    val isSoldOut = document.getBoolean("soldOut") // TODO: 수정 필요
                    val createdAt = document.getTimestamp("createdAt")
                    val formattedDate = createdAt?.toDate()?.let { getTimeAgo(it.time) }

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
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        binding.registerBtn.setOnClickListener {
            addChatRoom()
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

    fun getPostUser(){
        val user = Firebase.auth.currentUser

        db.collection("users")
            .document(nickname)
            .get()
            .addOnSuccessListener { result ->
                postUser = result.toObject(User::class.java)!!
                findViewById<TextView>(R.id.nicknameTextView).text =
                    postUser.name ?: "Nickname is null"

                if(postUser.uid == user?.uid){
                    binding.registerBtn.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->
                // 실패 시 처리
            }
    }
    fun addChatRoom() {
        val user = Firebase.auth.currentUser
        val chatRoom = ChatRoom(mapOf(
            user?.uid!! to true,
            postUser.uid!! to true
        ), null)

        db.collection("chatRoom")
            .whereEqualTo("users.${user?.uid}", true)
            .whereEqualTo("users.${postUser.uid}", true)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Log.d("chatRoom_TEST", "Empty")
                    // 채팅방이 없는 경우 새로운 채팅방 생성
                    db.collection("chatRoom")
                        .add(chatRoom)
                        .addOnSuccessListener { documentReference ->
                            goToChatRoom(chatRoom, postUser)
                        }
                        .addOnFailureListener { e ->
                            // 실패 시 처리
                        }
                } else {
                    val existingChatRoom = querySnapshot.documents[0].toObject(ChatRoom::class.java)
                    goToChatRoom(existingChatRoom!!, postUser)
                }
            }
            .addOnFailureListener { e ->
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
}