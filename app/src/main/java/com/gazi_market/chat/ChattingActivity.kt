package com.gazi_market.chat

import android.content.ContentValues
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gazi_market.MainActivity
import com.gazi_market.post.PostData
import com.gazi_market.R
import com.gazi_market.databinding.ActivityChattingBinding
import com.gazi_market.model.ChatRoom
import com.gazi_market.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone

class ChattingActivity : AppCompatActivity() {
    lateinit var binding: ActivityChattingBinding
    lateinit var btn_exit: ImageButton
    lateinit var btn_submit: ImageButton
    lateinit var txt_title: TextView
    lateinit var txt_name: TextView
    lateinit var edt_message: EditText
    lateinit var firebaseDatabase: DatabaseReference
    lateinit var recycler_talks: RecyclerView
    lateinit var chatRoom: ChatRoom
    lateinit var opponentUser: User
    lateinit var chatRoomKey: String
    lateinit var myUid: String
    private val db : FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeProperty()
        initializeView()
        initializeListener()
        setupChatRooms()
    }

    fun initializeProperty() {  //변수 초기화
        myUid = FirebaseAuth.getInstance().currentUser?.uid!!              //현재 로그인한 유저 id
        firebaseDatabase = FirebaseDatabase.getInstance().reference!!

        chatRoom = (intent.getSerializableExtra("ChatRoom")) as ChatRoom      //채팅방 정보
        chatRoomKey = intent.getStringExtra("ChatRoomKey").toString()
        opponentUser = (intent.getSerializableExtra("Opponent")) as User    //상대방 유저 정보
    }

    fun initializeView() {    //뷰 초기화
        btn_exit = binding.imgbtnQuit
        edt_message = binding.edtMessage
        recycler_talks = binding.recyclerMessages
        btn_submit = binding.btnSubmit
        txt_title = binding.txtTitle
        txt_name = binding.txtName
        txt_name.text = opponentUser.name ?: ""

        setTitleAndImage()
    }

    fun initializeListener() {
        btn_exit.setOnClickListener {
            startActivity(Intent(this@ChattingActivity, MainActivity::class.java))
        }
        btn_submit.setOnClickListener { putMessage() }
        edt_message.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty() && s.trim { it <= ' ' }.isNotEmpty()) {
                    // 텍스트가 있는 경우, 아이콘을 파란색으로 변경
                    btn_submit.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.sky_blue))
                    btn_submit.isEnabled = true
                } else {
                    // 텍스트가 없는 경우, 아이콘을 회색으로 변경
                    btn_submit.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(applicationContext, R.color.gray))
                    btn_submit.isEnabled = false
                }
            }
        })
    }


    fun setTitleAndImage(){
        var imageURL : String = ""

        db.collection("posts")
            .document(chatRoom.postId)
            .get()
            .addOnSuccessListener { result ->
                var post = result.toObject(PostData::class.java)!!
                txt_title.text = post.title
                imageURL = post.image

                if (!imageURL.isNullOrEmpty()) {
                    // Firebase Storage에서 이미지 다운로드 URL 생성
                    val storageReference = Firebase.storage.reference
                    storageReference.child(imageURL).downloadUrl
                        .addOnSuccessListener { uri ->
                            Glide.with(this)
                                .load(uri)
                                .circleCrop()
                                .into(binding.postImage)
                        }
                        .addOnFailureListener { exception ->
                            Log.e(ContentValues.TAG, "이미지 다운로드 실패: $exception")
                        }
                }
            }
            .addOnFailureListener { exception ->
                // 실패 시 처리
            }

    }

    fun setupChatRooms() {              //채팅방 목록 초기화 및 표시
        if (chatRoomKey.isNullOrBlank())
            setupChatRoomKey()
        else
            setupRecycler()
    }

//    fun setupChatRoomKey() {            //chatRoomKey 없을 경우 초기화 후 목록 초기화
//        FirebaseDatabase.getInstance().getReference("ChatRoom")
//            .child("chatRooms").orderByChild("users/${opponentUser.uid}").equalTo(true)    //상대방의 Uid가 포함된 목록이 있는지 확인
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onCancelled(error: DatabaseError) {}
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    for (data in snapshot.children) {
//                        chatRoomKey = data.key!!          //chatRoomKey 초기화
//                        setupRecycler()                  //목록 업데이트
//                        break
//                    }
//                }
//            })
//    }

    fun setupChatRoomKey() {
        db.collection("chatRoom")
            .whereEqualTo("users.${opponentUser.uid}", true) // 상대방의 Uid가 포함된 목록이 있는지 확인
            .whereEqualTo("users.${myUid}", true)
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    Log.d("setupChatRoomKey", "${chatRoomKey} is ${document.id}")
                    chatRoomKey = document.id // chatRoomKey 초기화
                    setupRecycler() // 목록 업데이트
                    break
                }
            }
            .addOnFailureListener { exception ->
                // 실패 시 처리
            }
    }

//    fun putMessage() {       //메시지 전송
//        try {
//            var message = Message(myUid, getDateTimeString(), edt_message.text.toString())    //메시지 정보 초기화
//            Log.i("ChatRoomKey", chatRoomKey)
//            FirebaseDatabase.getInstance().getReference("ChatRoom").child("chatRooms")
//                .child(chatRoomKey).child("messages")                   //현재 채팅방에 메시지 추가
//                .push().setValue(message).addOnSuccessListener {
//                    Log.i("putMessage", "메시지 전송에 성공하였습니다.")
//                    edt_message.text.clear()
//                }.addOnCanceledListener {
//                    Log.i("putMessage", "메시지 전송에 실패하였습니다")
//                }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.i("putMessage", "메시지 전송 중 오류가 발생하였습니다.")
//        }
//    }

    fun putMessage() {
        if (edt_message.text.isEmpty()) return
        try {
            val message = hashMapOf(
                "senderUid" to myUid,
                "sended_date" to getDateTimeString(),
                "content" to edt_message.text.toString()
            )

            Log.i("ChatRoomKey", chatRoomKey)

            db.collection("chatRoom")
                .document(chatRoomKey)
                .collection("messages")
                .add(message)
                .addOnSuccessListener {
                    Log.i("putMessage", "메시지 전송에 성공하였습니다.")
                    edt_message.text.clear()
                }
                .addOnFailureListener {
                    Log.i("putMessage", "메시지 전송에 실패하였습니다")
                }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.i("putMessage", "메시지 전송 중 오류가 발생하였습니다.")
        }
    }

    fun getDateTimeString(): String {          //메시지 보낸 시각 정보 반환
        try {
            var localDateTime = LocalDateTime.now()
            localDateTime.atZone(TimeZone.getDefault().toZoneId())
            var dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            return localDateTime.format(dateTimeFormatter).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("getTimeError")
        }
    }

    fun setupRecycler() {            //목록 초기화 및 업데이트
        recycler_talks.layoutManager = LinearLayoutManager(this)
        recycler_talks.adapter = RecyclerMessagesAdapter(this, chatRoomKey, opponentUser.uid)
    }
}