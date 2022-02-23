package com.android.soto.Menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.soto.databinding.ActivityMenuBinding

class menuActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTambah.setOnClickListener {
            startActivity(Intent(this, tambahActivity::class.java))
        }
    }
}