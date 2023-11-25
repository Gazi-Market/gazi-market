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
    lateinit var myUid: String
    lateinit var postUser: User
    private val db : FirebaseFirestore = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backBtn = findViewById<ImageView>(R.id.img_btn_back)
        backBtn.setOnClickListener {
            finish()
        }

        val etcBtn = findViewById<ImageView>(R.id.img_btn_etc)
        etcBtn.setOnClickListener {
            val popupMenu = PopupMenu(ContextThemeWrapper(this, R.style.PopupMenuStyle), etcBtn)
            popupMenu.menuInflater.inflate(R.menu.post_detail_menu, popupMenu.menu)

            // 아이콘 보이게 하기
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popupMenu.setForceShowIcon(true)
            } else {
                try {
                    val fields = popupMenu.javaClass.declaredFields
                    for (field in fields) {
                        if ("mPopup" == field.name) {
                            field.isAccessible = true
                            val menuPopupHelper = field.get(popupMenu)
                            val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                            val setForceIcons = classPopupHelper.getMethod("setForceShowIcon", Boolean::class.java)
                            setForceIcons.invoke(menuPopupHelper, true)
                            break
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // 팝업 메뉴 아이템 클릭 이벤트 처리
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_edit -> {
                        // "판매글 수정하기" 클릭 시 할 동작 구현
                        // 예시: 수정 화면으로 이동하거나 해당 기능 실행
                        return@setOnMenuItemClickListener true
                    }
                    R.id.menu_mark_sold -> {
                        // "판매완료로 변경" 클릭 시 할 동작 구현
                        // 예시: 상태 변경 작업 수행 또는 해당 기능 실행
                        return@setOnMenuItemClickListener true
                    }
                    else -> return@setOnMenuItemClickListener false
                }
            }

            popupMenu.show()
        }


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
            .document(myUid)// TODO: 여기 원래 nickname 이였음
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