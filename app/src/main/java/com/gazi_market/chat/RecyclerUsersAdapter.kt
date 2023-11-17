package com.gazi_market.chat

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.gazi_market.MainActivity
import com.gazi_market.R
import com.gazi_market.databinding.ListPersonItemBinding
import com.gazi_market.model.ChatRoom
import com.gazi_market.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RecyclerUsersAdapter(val context: Context) :
    RecyclerView.Adapter<RecyclerUsersAdapter.ViewHolder>() {
    var users: ArrayList<User> =arrayListOf()        //검색어로 일치한 사용자 목록
    val allUsers: ArrayList<User> =arrayListOf()    //전체 사용자 목록
    lateinit var currnentUser: User
    private val db : FirebaseFirestore = Firebase.firestore

    init {
        setupAllUserList()
    }

//    fun setupAllUserList() {        //전체 사용자 목록 불러오기
//        val myUid = FirebaseAuth.getInstance().currentUser?.uid.toString()        //현재 사용자 아이디
//        FirebaseDatabase.getInstance().getReference("User").child("users")   //사용자 데이터 요청
//            .addValueEventListener(object : ValueEventListener {
//                override fun onCancelled(error: DatabaseError) {
//                }
//
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    users.clear()
//                    for (data in snapshot.children) {
//                        val item = data.getValue<User>()
//                        if (item?.uid.equals(myUid)) {
//                            currnentUser = item!!             //전체 사용자 목록에서 현재 사용자는 제외
//                            continue
//                        }
//                        allUsers.add(item!!)              //전체 사용자 목록에 추가
//                    }
//                    users = allUsers.clone() as ArrayList<User>
//                    notifyDataSetChanged()              //화면 업데이트
//                }
//            })
//    }
    fun setupAllUserList() {
        val myUid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                users.clear()

                for (document in result) {
                    val item = document.toObject(User::class.java)

                    if (item.uid == myUid) {
                        currnentUser = item
                    } else {
                        allUsers.add(item)
                    }
                }

                users = ArrayList(allUsers)
                notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // 실패 시 처리
            }
    }

    fun searchItem(target: String) {            //검색
        if (target.equals("")) {      //검색어 없는 경우 전체 목록 표시
            users = allUsers.clone() as ArrayList<User>
        } else {
            var matchedList = allUsers.filter{ it.name!!.contains(target)}//검색어 포함된 항목 불러오기
            users.clear()
            matchedList.forEach{users.add(it)}
        }
        notifyDataSetChanged()          //화면 업데이트
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_person_item, parent, false)
        return ViewHolder(ListPersonItemBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txt_name.text= users[position].name
        holder.txt_email.text= users[position].email

        holder.background.setOnClickListener()
        {
            addChatRoom(position)        //해당 사용자 선택 시
        }
    }

//    fun addChatRoom(position: Int) {     //채팅방 추가
//        val opponent = users[position]   //채팅할 상대방 정보
//        var database = FirebaseDatabase.getInstance().getReference("ChatRoom")    //넣을 database reference 세팅
//        var chatRoom = ChatRoom(         //추가할 채팅방 정보 세팅
//            mapOf(currnentUser.uid!!to true, opponent.uid!!to true),
//            null
//        )
//        var myUid = FirebaseAuth.getInstance().uid//내 Uid
//        database.child("chatRooms")
//            .orderByChild("users/${opponent.uid}").equalTo(true)       //상대방 Uid가 포함된 채팅방이 있는 지 확인
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onCancelled(error: DatabaseError) {}
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (snapshot.value== null) {              //채팅방이 없는 경우
//                        database.child("chatRooms").push().setValue(chatRoom).addOnSuccessListener{// 채팅방 새로 생성 후 이동
//                            goToChatRoom(chatRoom, opponent)
//                        }
//                    } else {
//                        context.startActivity(Intent(context, MainActivity::class.java))
//                        goToChatRoom(chatRoom, opponent)                    //해당 채팅방으로 이동
//                    }
//
//                }
//            })
//    }
    fun addChatRoom(position: Int) {
        val opponent = users[position] // 채팅할 상대방 정보
        val chatRoom = ChatRoom(mapOf(
            currnentUser.uid!! to true,
            opponent.uid!! to true
        ), null)

        db.collection("chatRoom")
            .whereEqualTo("users.${currnentUser.uid}", true)
            .whereEqualTo("users.${opponent.uid}", true)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // 채팅방이 없는 경우 새로운 채팅방 생성
                    db.collection("chatRoom")
                        .add(chatRoom)
                        .addOnSuccessListener { documentReference ->
                            goToChatRoom(chatRoom, opponent)
                        }
                        .addOnFailureListener { e ->
                            // 실패 시 처리
                        }
                } else {
                    // 채팅방이 이미 있는 경우
                    context.startActivity(Intent(context, MainActivity::class.java))
                    goToChatRoom(chatRoom, opponent)
                }
            }
            .addOnFailureListener { e ->
                // 실패 시 처리
            }
    }



    fun goToChatRoom(chatRoom: ChatRoom, opponentUid: User) {       //채팅방으로 이동
        var intent = Intent(context, ChattingActivity::class.java)
        intent.putExtra("ChatRoom", chatRoom)       //채팅방 정보
        intent.putExtra("Opponent", opponentUid)    //상대방 정보
        intent.putExtra("ChatRoomKey", "")   //채팅방 키
        context.startActivity(intent)
        (context as AppCompatActivity).finish()
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class ViewHolder(itemView: ListPersonItemBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var background = itemView.background
        var txt_name = itemView.txtName
        var txt_email = itemView.txtEmail
    }

}
