package com.gazi_market

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import java.text.NumberFormat
import java.util.Locale

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

        Log.d("HomeRecyclerViewAdapter", postData.toString())

        viewHolder.apply {
            tvPostTitle.text = postData.title
            tvPrice.text = if (postData.soldOut) "판매 완료" else // 100만원 이상은 단위를 만원으로 표시
                if (postData.price >= 1000000) {
                    val price = postData.price / 10000
                    val priceFormat = NumberFormat.getNumberInstance(Locale.KOREA).format(price)
                    "${priceFormat}만원"
                } else {
                    val priceFormat = NumberFormat.getNumberInstance(Locale.KOREA).format(postData.price)
                    "${priceFormat}원"
                }
        }

        holder.itemView.setOnClickListener {
            val postData = postList[position]
            val documentId = postData.documentId
            val intent = Intent(holder.itemView.context, DetailPostActivity::class.java)
            intent.putExtra("documentId", documentId)
            holder.itemView.context.startActivity(intent)
        }

        if (postData.image.isNullOrEmpty()) {
            Log.e("HomeRecyclerViewAdapter", postData.documentId + " cannot found image")
            return
        }
        Firebase.storage.reference.child(postData.image).downloadUrl.addOnSuccessListener { uri ->
            Glide.with(context).load(uri.toString()).into(holder.imageView)
        }
    }
}