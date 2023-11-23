package com.gazi_market

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.gazi_market.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore

class ViewPostActivity : AppCompatActivity() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val documentId = intent.getStringExtra("documentId") ?: return

        // Fetch and display the post
        fetchPost(documentId)
    }

    private fun fetchPost(documentId: String) {
        val db = FirebaseFirestore.getInstance()
        val postsCollection = db.collection("posts")

//        postsCollection.document(documentId).get().addOnSuccessListener { document ->
//            if (document != null) {
//                val post = document.toObject(PostData::class.java)
//                post?.let {
//                    binding.titleTextView.text = it.title
//                    binding.contentTextView.text = it.content
//                    binding.priceTextView.text = "${it.price}원"
//
//                    // Load the image using Glide
//                    Glide.with(this)
//                        .load(it.imageUrl) // Assuming imageUrl is a field in PostData
//                        .into(binding.postImageView)
//                }
//            }
//        }.addOnFailureListener {
//            // Handle the failure case
//        }
    }
}
