package com.gazi_market.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gazi_market.databinding.ChatRoomLayoutBinding
import com.google.firebase.database.*


class ChatRoomFragment : Fragment() {

    private lateinit var binding: ChatRoomLayoutBinding
    private lateinit var firebaseDatabase: DatabaseReference
    private lateinit var recycler_chatroom: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ChatRoomLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
        setupRecycler()

    }

    private fun initializeView() {
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("ChatRoom")!!
        recycler_chatroom = binding.recyclerChatrooms
    }


    private fun setupRecycler() {
        recycler_chatroom.layoutManager = LinearLayoutManager(requireContext())
        recycler_chatroom.adapter = RecyclerChatRoomsAdapter(requireContext())
    }

}





//class ChatRoomActivity : AppCompatActivity() {
//    lateinit var btnAddchatRoom: Button
//    lateinit var btnSignout: Button
//    lateinit var binding: ChatRoomLayoutBinding
//    lateinit var firebaseDatabase: DatabaseReference
//    lateinit var recycler_chatroom: RecyclerView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ChatRoomLayoutBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        initializeView()
//        initializeListener()
//        setupRecycler()
//    }
//
//    fun initializeView() { //뷰 초기화
//        try {
//            firebaseDatabase = FirebaseDatabase.getInstance().getReference("ChatRoom")!!
//            btnSignout = binding.btnSignout
//            btnAddchatRoom = binding.btnNewMessage
//            recycler_chatroom = binding.recyclerChatrooms
//        }catch (e:Exception)
//        {
//            e.printStackTrace()
//            Toast.makeText(this,"화면 초기화 중 오류가 발생하였습니다.",Toast.LENGTH_LONG).show()
//        }
//    }
//    fun initializeListener()  //버튼 클릭 시 리스너 초기화
//    {
//        btnSignout.setOnClickListener()
//        {
//            signOut()
//        }
//        btnAddchatRoom.setOnClickListener()  //새 메시지 화면으로 이동
//        {
//            startActivity(Intent(this@ChatRoomActivity, AddChatRoomActivity::class.java))
//            finish()
//        }
//    }
//
//    fun setupRecycler() {
//        recycler_chatroom.layoutManager = LinearLayoutManager(this)
//        recycler_chatroom.adapter = RecyclerChatRoomsAdapter(this)
//    }
//
//    fun signOut()    //로그아웃 실행
//    {
//        try {
//            val builder = AlertDialog.Builder(this)
//                .setTitle("로그아웃")
//                .setMessage("로그아웃 하시겠습니까?")
//                .setPositiveButton("확인"
//                ) { dialog, id ->
//                    try {
//                        FirebaseAuth.getInstance().signOut()             //로그아웃
//                        startActivity(Intent(this@ChatRoomActivity, LoginActivity::class.java))
//                        dialog.dismiss()
//                        finish()
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                        dialog.dismiss()
//                        Toast.makeText(this, "로그아웃 중 오류가 발생하였습니다.", Toast.LENGTH_LONG).show()
//                    }
//                }
//                .setNegativeButton("취소"          //다이얼로그 닫기
//                ) { dialog, id ->
//                    dialog.dismiss()
//                }
//            builder.show()
//        }catch (e:Exception)
//        {
//            e.printStackTrace()
//            Toast.makeText(this,"로그아웃 중 오류가 발생하였습니다.",Toast.LENGTH_LONG).show()
//        }
//    }
//
//    override fun onBackPressed() {
//        signOut()
//    }
//}