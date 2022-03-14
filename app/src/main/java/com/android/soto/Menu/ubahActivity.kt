package com.android.soto.Menu

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.soto.Model.Menu
import com.android.soto.databinding.ActivityUbahBinding
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class ubahActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUbahBinding
    private lateinit var ref : DatabaseReference
    private lateinit var storRef : FirebaseStorage
    private lateinit var imageUri : Uri
    var id = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUbahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = intent.getStringExtra("id").toString()
        Log.d("test", intent.getStringExtra("id").toString())
        ref = FirebaseDatabase.getInstance().getReference("Menu")
        storRef = FirebaseStorage.getInstance()

        if (id != null) {
            ref.child(id).addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val menu = snapshot.getValue(Menu::class.java)
                    Log.d("content", menu?.nama.toString())
                    binding.etNama.setText(menu?.nama.toString())
                    binding.etKategori.setText(menu?.kategori.toString())
//                    binding.etSatuan.setText(menu?.satuan.toString())
                    menu?.let {
                        val storageReference = storRef.getReference("Images//${it.gambar}")
                        storageReference.downloadUrl.addOnSuccessListener { uri->
                            Glide.with(this@ubahActivity).load(uri).into(binding.ivMenu)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ubahActivity, "Data gagal dimuat $error", Toast.LENGTH_SHORT).show()
//                    Toast.makeText(this, "Gambar gagal tersimpan", Toast.LENGTH_SHORT).show()
                }

            })
        }

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
        val intentImg = Intent()
        intentImg.type = "image/*"
        intentImg.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intentImg, 100)
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
        val gambar = "image$id"
        val menu = Menu(id,nama,kategori,gambar)
//        Log.d("test", menu.toString())
        val storageReference = storRef.getReference("Images/$gambar")

        if (nama.isEmpty()) {
            binding.etNama.error = "Nama menu harus di isi !"
        } else if (kategori.isEmpty()) {
            binding.etKategori.error = "Kategori menu harus di isi !"
        }else if (!id.isEmpty() && !nama.isEmpty() && !kategori.isEmpty()) {

            storageReference.putFile(imageUri).addOnSuccessListener {
//                Log.d("test", menu.toString())
                binding.ivMenu.setImageURI(null)
                Toast.makeText(this, "Gambar tersimpan", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Gambar gagal tersimpan", Toast.LENGTH_SHORT).show()
            }

            ref.child(id).setValue(menu).addOnCompleteListener {
                Toast.makeText(this, "Berhasil di tambahkan", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, menuActivity::class.java))
            }
        } else {
            Toast.makeText(this, "Data harus di isi !", Toast.LENGTH_SHORT).show()
        }
    }
}