package com.gazi_market

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gazi_market.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Home : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var recyclerViewSaleItem: RecyclerView
    private lateinit var recyclerViewSaleItemAdapter: HomeRecyclerViewAdapter
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var tvTitle: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.btnFilter.setOnClickListener { showFilterOptions() }

        binding.postBtn.setOnClickListener {
            val intent = Intent(requireContext(), PostActivity::class.java)
            startActivity(intent)
        }

        recyclerViewSaleItem = binding.recyclerView
        tvTitle = binding.tvTitle

        database = Firebase.database.reference
        auth = Firebase.auth

        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        homeViewModel.saleItemsLiveData.observe(viewLifecycleOwner, Observer { items ->
            if (items != null) recyclerViewSaleItemAdapter.setSaleItems(items)
        })

        recyclerViewSaleItem.layoutManager = GridLayoutManager(context, 3)
        recyclerViewSaleItemAdapter = activity?.let { HomeRecyclerViewAdapter(it) }!!
        recyclerViewSaleItem.adapter = recyclerViewSaleItemAdapter

        return binding.root
    }

    private fun showFilterOptions() {
        val filterOptions = arrayOf("전체", "판매중", "판매완료")
        AlertDialog.Builder(context).setTitle("필터 선택").setItems(filterOptions) { dialog, which ->
            when (filterOptions[which]) {
                "전체" -> homeViewModel.loadAllItems()
                "판매중" -> homeViewModel.loadSaleItems()
                "판매완료" -> homeViewModel.loadSoldOutItems()
            }
        }.setNegativeButton("취소", null).show()
    }
}