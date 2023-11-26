package com.gazi_market

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val postsCollection = db.collection("posts")
    private val _saleItems = MutableLiveData<List<PostData>>()
    val saleItemsLiveData: LiveData<List<PostData>> = _saleItems
    private var allSaleItems = listOf<PostData>()

    init {
        postsCollection.orderBy("createdAt").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("HomeViewModel", "Error fetching documents: ", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val items = mutableListOf<PostData>()
                for (document in snapshot.documents) {
                    val item = document.toObject<PostData>()
                    Log.d("HomeViewModel", item.toString())
                    item?.let { items.add(item) }
                }
                allSaleItems = items.asReversed()
                _saleItems.value = allSaleItems
            }
        }
    }

    fun loadAllItems() {
        _saleItems.value = allSaleItems
    }

    fun loadSaleItems() {
        _saleItems.value = allSaleItems.filter { !it.soldOut }
    }

    fun loadSoldOutItems() {
        _saleItems.value = allSaleItems.filter { it.soldOut }
    }

    fun loadItemsByPriceRange(minPrice: Int?, maxPrice: Int?) {
        _saleItems.value = allSaleItems.filter { item ->
            val isWithinMinPrice = minPrice?.let { item.price >= it } ?: true
            val isWithinMaxPrice = maxPrice?.let { item.price <= it } ?: true
            isWithinMinPrice && isWithinMaxPrice
        }
    }
}