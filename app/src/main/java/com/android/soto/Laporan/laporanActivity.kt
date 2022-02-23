package com.android.soto.Laporan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.soto.databinding.ActivityLaporanBinding

class laporanActivity : AppCompatActivity() {

    private lateinit var binding:ActivityLaporanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaporanBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}