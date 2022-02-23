package com.android.soto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.soto.Laporan.laporanActivity
import com.android.soto.Menu.menuActivity
import com.android.soto.Pesan.pesanActivity
import com.android.soto.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMenu.setOnClickListener {
            startActivity(Intent(this, menuActivity::class.java))
        }
        binding.btnPesan.setOnClickListener {
            startActivity(Intent(this, pesanActivity::class.java))
        }
        binding.btnLaporan.setOnClickListener {
            startActivity(Intent(this, laporanActivity::class.java))
        }
    }
}