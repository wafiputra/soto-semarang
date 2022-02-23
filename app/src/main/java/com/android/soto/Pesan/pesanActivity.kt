package com.android.soto.Pesan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.soto.databinding.ActivityPesanBinding

class pesanActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPesanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesanBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}