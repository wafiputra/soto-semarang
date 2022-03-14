package com.android.soto.Menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.soto.databinding.ActivityGambarBinding

class gambarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGambarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGambarBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}