package com.gazi_market

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.gazi_market.chat.ChatRoomFragment
import com.gazi_market.databinding.ActivityMainBinding
import com.gazi_market.home.Home
import com.gazi_market.myPage.MyPageFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Home())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> replaceFragment(Home())
                R.id.chat -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, ChatRoomFragment())
                        .commit()
                    return@setOnItemSelectedListener true
                }
                R.id.my_page -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, MyPageFragment())
                        .commit()
                    return@setOnItemSelectedListener true
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}