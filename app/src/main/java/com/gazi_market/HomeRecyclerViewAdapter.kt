package com.gazi_market

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.storage.storage

class HomeRecyclerViewAdapter(var context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var postList: List<PostData> = emptyList()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvPostTitle = itemView.findViewById<TextView>(R.id.tvPostTitle)
        var tvPrice = itemView.findViewById<TextView>(R.id.tvPrice)
        var imageView = itemView.findViewById<ImageView>(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_summary, parent, false)
        return MyViewHolder(view) // 판매 중
    }

    fun setSaleItems(items: List<PostData>) {
        this.postList = items
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val postData: PostData = postList[position]
        val viewHolder = holder as MyViewHolder

        viewHolder.apply {
            tvPostTitle.text = postData.title
            tvPrice.text = if (postData.isSoldOut) "판매 완료" else "${postData.price}원"
        }

        Firebase.storage.reference.child(postData.image).downloadUrl.addOnSuccessListener { uri ->
            Glide.with(context).load(uri.toString()).into(holder.imageView)
        }

        holder.itemView.setOnClickListener {
            val postData = postList[position]
            val documentId = postData.documentId
            val intent = Intent(holder.itemView.context, DetailPostActivity::class.java)
            intent.putExtra("documentId", documentId)
            holder.itemView.context.startActivity(intent)
        }
    }
}