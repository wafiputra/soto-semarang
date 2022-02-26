package com.sotosemarang.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sotosemarang.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.BtnMenu.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.BtnPemesanan.setOnClickListener {
//            startActivity(Intent(this, pesanActivity::class.java))
        }
        binding.BtnLaporanBulanan.setOnClickListener {
//            startActivity(Intent(this, laporanActivity::class.java))
        }
    }
}