package com.android.soto.Menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.soto.Adapter.menuViewAdapter
import com.android.soto.MainActivity
import com.android.soto.Model.Menu
import com.android.soto.databinding.ActivityMenuBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class menuActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMenuBinding
    private lateinit var menuArrayList : ArrayList<Menu>
    private lateinit var ref : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvListMenu.layoutManager = LinearLayoutManager(this)
        menuArrayList = arrayListOf<Menu>()
        getMenuData()

        binding.btnKembali.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.btnTambah.setOnClickListener {
            startActivity(Intent(this, tambahActivity::class.java))
        }
    }

    private fun getMenuData() {
        ref = FirebaseDatabase.getInstance().getReference("Menu")

        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    menuArrayList.clear()
                    for (menuSnapshot in snapshot.children) {
                        val menu = menuSnapshot.getValue(Menu::class.java)
                        menuArrayList.add(menu!!)
                    }

                    var adapter = menuViewAdapter(menuArrayList, this@menuActivity)
                    binding.rvListMenu.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
}