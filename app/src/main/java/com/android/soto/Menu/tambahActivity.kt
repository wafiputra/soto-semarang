package com.android.soto.Menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.soto.Model.Menu
import com.android.soto.databinding.ActivityTambahBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class tambahActivity : AppCompatActivity() {

    private lateinit var binding:ActivityTambahBinding
    private lateinit var ref:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ref = FirebaseDatabase.getInstance().getReference("Menu")

        binding.btnSimpan.setOnClickListener {
            saveData()
        }
    }

    private fun saveData() {
        val nama = binding.etNama.text.toString().trim()
        val kategori = binding.etKategori.text.toString().trim()
        val satuan = binding.etSatuan.text.toString().trim()

        val menuId = ref.push().key.toString()
        val menu = Menu(menuId, nama, kategori, satuan)

        if (nama.isEmpty()) {
            binding.etNama.error = "Nama menu harus di isi !"
        } else
        if (kategori.isEmpty()) {
            binding.etKategori.error = "Kategori menu harus di isi !"
        }else
        if (satuan.isEmpty()) {
            binding.etSatuan.error = "Satuan menu harus di isi !"
        }else

        if (!menuId.isEmpty() && !nama.isEmpty() && !kategori.isEmpty() && !satuan.isEmpty()) {
            ref.child(menuId).setValue(menu).addOnCompleteListener {
                Toast.makeText(this, "Berhasil di tambahkan", Toast.LENGTH_SHORT).show()

            }
        } else {
            Toast.makeText(this, "Data harus di isi !", Toast.LENGTH_SHORT).show()
        }

    }
}