package com.gazi_market.myPage

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.gazi_market.R
import com.gazi_market.account.LoginActivity
import com.gazi_market.chat.AddChatRoomActivity
import com.gazi_market.chat.RecyclerChatRoomsAdapter
import com.gazi_market.databinding.ChatRoomLayoutBinding
import com.gazi_market.databinding.FragmentMyPageBinding
import com.gazi_market.model.ChatRoom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyPageFragment : Fragment() {

    private lateinit var binding: FragmentMyPageBinding
    val myUid = FirebaseAuth.getInstance().currentUser?.uid.toString()   //현재 사용자 Uid
    private val db : FirebaseFirestore = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeListener()
        getName()
    }

    private fun initializeListener() {
        binding.logout.setOnClickListener {
            signOut()
        }
    }

    private fun getName() {
        db.collection("users")
            .document(myUid)
            .get()
            .addOnSuccessListener {
                binding.name.text = it["name"].toString()
            }
            .addOnFailureListener { exception ->
                // Handle failures
            }
    }
    private fun signOut() {
        try {
            val builder = AlertDialog.Builder(requireContext())
                .setTitle("로그아웃")
                .setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton(
                    "확인"
                ) { dialog, id ->
                    try {
                        FirebaseAuth.getInstance().signOut()
                        startActivity(Intent(requireContext(), LoginActivity::class.java))
                        dialog.dismiss()
                        requireActivity().finish()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        dialog.dismiss()
                        Toast.makeText(
                            requireContext(),
                            "로그아웃 중 오류가 발생하였습니다.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .setNegativeButton("취소") { dialog, id ->
                    dialog.dismiss()
                }
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                requireContext(),
                "로그아웃 중 오류가 발생하였습니다.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

}