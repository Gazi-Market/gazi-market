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
import com.google.firebase.Timestamp
import java.text.NumberFormat
import java.util.Locale

class DetailPostActivity : AppCompatActivity() {

    private lateinit var postUser: User
    private lateinit var postUserUid: String
    lateinit var binding: ActivityDetailPostBinding
    private val db: FirebaseFirestore = Firebase.firestore
    private var imageURL: String? = null
    private var isSoldOut: Boolean = false
    private lateinit var documentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        loadPostData()
    }

    private fun setupUI() {
        binding.imgBtnBack.setOnClickListener { onBackPressed() }
        binding.registerBtn.setOnClickListener { addChatRoom(documentId) }
        binding.productImageView.setOnClickListener { openImageZoomActivity() }
        setupPopupMenu()
    }

    private fun loadPostData() {
        val documentId = intent.getStringExtra("documentId") ?: return
        val postDocRef = db.collection("posts").document(documentId)

        postDocRef.get().addOnSuccessListener { document ->
            if (document == null || !document.exists()) return@addOnSuccessListener

            postUserUid = document.getString("uid").toString()
            isSoldOut = document.getBoolean("soldOut") ?: false
            imageURL = document.getString("image") ?: "/image/logo.png"
            
            if (Firebase.auth.currentUser?.uid == postUserUid)
                binding.registerBtn.visibility = View.GONE
            else binding.imgBtnEtc.visibility = View.GONE

            val createdAtTimestamp = document.getTimestamp("createdAt")
            binding.createdAtTextView.text = formatCreatedAt(createdAtTimestamp)
            binding.priceTextView.text = getPriceString(document.getDouble("price"))
            binding.contentTextView.text = document.getString("content") ?: "Content is null"
            binding.titleTextView.text = document.getString("title") ?: "Title is null"

            updatePopupMenu(isSoldOut)
            loadProductImage()
            getPostUser()
        }
    }

    private fun formatCreatedAt(createdAt: Timestamp?): String {
        return createdAt?.toDate()?.let { getTimeAgo(it.time) } ?: "날짜 정보 없음"
    }

    private fun setupPopupMenu() {
        binding.imgBtnEtc.setOnClickListener {
            val popupMenu = PopupMenu(ContextThemeWrapper(this, R.style.PopupMenuStyle), it)
            popupMenu.menuInflater.inflate(R.menu.post_detail_menu, popupMenu.menu)
            popupMenu.show()
            setPopupMenuListeners(popupMenu)
        }
    }

    private fun setPopupMenuListeners(popupMenu: PopupMenu) {
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_mark_sold -> {
                    toggleSoldOutStatus()
                    true
                }

                R.id.menu_edit -> {
                    editPost()
                    true
                }

                else -> false
            }
        }
    }

    private fun openImageZoomActivity() {
        val intent = Intent(this, ImageZoomActivity::class.java)
        intent.putExtra("imageURL", imageURL) // 이미지 URL 전달
        startActivity(intent)
    }

    private fun updatePopupMenu(soldOut: Boolean) {
        binding.imgBtnEtc.setOnClickListener {
            val popupMenu =
                PopupMenu(ContextThemeWrapper(this, R.style.PopupMenuStyle), binding.imgBtnEtc)
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

        db.collection("posts").document(documentId).update("soldOut", newStatus)
            .addOnSuccessListener {
                isSoldOut = newStatus
                updatePopupMenu(isSoldOut)
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

    private fun editPost() {
        val documentId = intent.getStringExtra("documentId") ?: return
        val editIntent = Intent(this, EditPostActivity::class.java).apply {
            putExtra("documentId", documentId)
        }
        startActivity(editIntent)
    }

    private fun getPriceString(price: Double?): String {
        if (isSoldOut) return "판매 완료"
        val priceFormat = NumberFormat.getNumberInstance(Locale.KOREA)
        return price?.let { "${priceFormat.format(it)}원" } ?: "가격 정보 없음"
    }

    private fun loadProductImage() {
        val storageReference = Firebase.storage.reference
        imageURL?.let {
            storageReference.child(it).downloadUrl.addOnSuccessListener { uri ->
                Glide.with(this).load(uri).into(binding.productImageView)
            }
        }
    }

    private fun getPostUser() {
        db.collection("users").document(postUserUid).get().addOnSuccessListener { result ->
            try {
                postUser = result.toObject(User::class.java)!!
                binding.nicknameTextView.text = postUser.name ?: "Nickname"
            } catch (e: NullPointerException) {
                binding.nicknameTextView.text = "Nickname"
                Log.e(TAG, "Cannot found user data")
                return@addOnSuccessListener
            }
        }
    }

    private fun addChatRoom(documentId: String) {
        val user = Firebase.auth.currentUser ?: return
        val otherUserId = postUser.uid ?: return
        val chatRoom = ChatRoom(mapOf(user.uid to true, otherUserId to true), null, documentId)

        checkExistingChatRoom(user.uid, otherUserId) { existingChatRoom ->
            val room = existingChatRoom ?: chatRoom
            goToChatRoom(room, postUser)
        }
    }

    private fun checkExistingChatRoom(
        userId: String,
        otherUserId: String,
        onResult: (ChatRoom?) -> Unit,
    ) {
        db.collection("chatRoom")
            .whereEqualTo("users.$userId", true)
            .whereEqualTo("users.$otherUserId", true)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val chatRoom = if (querySnapshot.isEmpty) null
                else querySnapshot.documents[0].toObject(ChatRoom::class.java)
                onResult(chatRoom)
            }
    }


    private fun goToChatRoom(chatRoom: ChatRoom, opponentUid: User) {  //채팅방으로 이동
        val intent = Intent(this@DetailPostActivity, ChattingActivity::class.java)
        intent.putExtra("ChatRoom", chatRoom)  //채팅방 정보
        intent.putExtra("Opponent", opponentUid)  //상대방 정보
        intent.putExtra("ChatRoomKey", "")  //채팅방 키
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