package com.android.soto.Menu

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.soto.Model.Menu
import com.android.soto.databinding.ActivityTambahBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class tambahActivity : AppCompatActivity() {

    private lateinit var binding:ActivityTambahBinding
    private lateinit var ref:DatabaseReference
    private lateinit var imageUri : Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ref = FirebaseDatabase.getInstance().getReference("Menu")


        binding.btnSimpan.setOnClickListener {
            saveData()
        }
        binding.btnKembali.setOnClickListener {
            startActivity(Intent(this, menuActivity::class.java))
        }
        binding.btnGambar.setOnClickListener {
            pickImage()
        }
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageUri = data?.data!!
            binding.ivMenu.setImageURI(imageUri)
        }
    }


    private fun saveData() {
        val nama = binding.etNama.text.toString().trim()
        val kategori = binding.etKategori.text.toString().trim()
//        val satuan = binding.etSatuan.text.toString().trim()

        val menuId = ref.push().key.toString()
        val image = "image$menuId"
        val menu = Menu(menuId, nama, kategori, image)
        val storageReference = FirebaseStorage.getInstance().getReference("Images/$image")

        if (nama.isEmpty()) {
            binding.etNama.error = "Nama menu harus di isi !"
        } else
        if (kategori.isEmpty()) {
            binding.etKategori.error = "Kategori menu harus di isi !"
        }else if (!menuId.isEmpty() && !nama.isEmpty() && !kategori.isEmpty()) {
            storageReference.putFile(imageUri).addOnSuccessListener {
                binding.ivMenu.setImageURI(null)
                Toast.makeText(this, "Gambar tersimpan", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Gambar gagal tersimpan", Toast.LENGTH_SHORT).show()
            }

            ref.child(menuId).setValue(menu).addOnCompleteListener {
                Toast.makeText(this, "Berhasil di tambahkan", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, menuActivity::class.java))
            }
        } else {
            Toast.makeText(this, "Data harus di isi !", Toast.LENGTH_SHORT).show()
        }

    }
}