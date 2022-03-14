package com.android.soto.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.soto.Model.Menu
import com.android.soto.Model.Transaksi
import com.android.soto.databinding.ItemListPesananBinding
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class pesanViewAdapter(private val pesanList : ArrayList<Menu>, private val context: Context, private val idR : String) : RecyclerView.Adapter<pesanViewAdapter.pesanViewHolder>() {

//    private val jum = 0
    private lateinit var ref: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): pesanViewHolder {
        val binding = ItemListPesananBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return pesanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: pesanViewHolder, position: Int) {
        val currentItem = pesanList[position]
        var jum = 0
        var idT = ""
        val dateFormat = SimpleDateFormat("yyyyMMdd")
        var currDate = dateFormat.format(Date())
        ref = FirebaseDatabase.getInstance().getReference("Transaksi")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("format", snapshot.childrenCount.toString())
                if (snapshot.childrenCount.toString().equals("0")) {
                    val df = SimpleDateFormat("yyyyMMddSSS")
                    val cd = df.format(Date())
                    idT = "tsspn"+cd
                } else {
                    val df = SimpleDateFormat("yyyyMMddSSS")
                    val cd = df.format(Date())
                    idT = "tsspn"+cd
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("format", "Format : $error")
            }

        })
//        if (ref.)
//        val idT = ref.push().key.toString()

        currentItem?.let {
            val storageReference = FirebaseStorage.getInstance().getReference("Images//${it.gambar}")
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                holder.menu.text = currentItem.nama
                holder.kategori.text = currentItem.kategori
                holder.jumlah.setText("$jum")
                Glide.with(context).load(uri).into(holder.gambar)
            }
        }


        holder.kurang.setOnClickListener {
            jum--
            if (jum < 0){
                jum = 0
                holder.jumlah.setText("$jum")
            } else {
                holder.jumlah.setText("$jum")
                if (jum > 0) {
                    saveData(currentItem.id, "$currDate", "$jum", idT)
                } else {
                    deleteData(idT)
                }
            }
        }

        holder.tambah.setOnClickListener {
            jum++
            holder.jumlah.setText("$jum")
            saveData(currentItem.id, "$currDate", "$jum", idT)
        }
    }

    private fun deleteData(id: String) {
        ref = FirebaseDatabase.getInstance().getReference("Transaksi")
        ref.child(id).removeValue().addOnSuccessListener {
            Toast.makeText(context, "Data terhapus", Toast.LENGTH_SHORT).show()

        }
    }

    private fun saveData(idM: String?, date: String, jum: String, idT: String) {
        ref = FirebaseDatabase.getInstance().getReference("Transaksi")
        val transaksi = Transaksi(idT,idM,date,jum,idR)
        ref.child(idT).setValue(transaksi).addOnSuccessListener {
//            Toast.makeText(context, "Data ditambah", Toast.LENGTH_SHORT).show()
        }

    }

    override fun getItemCount(): Int {
        return pesanList.size
    }

    class pesanViewHolder(val binding:ItemListPesananBinding): RecyclerView.ViewHolder(binding.root) {
        val menu = binding.tvNama
        val kategori = binding.tvHarga
        val gambar = binding.imageView
        val kurang = binding.btnKurang
        val tambah = binding.btnTambah
        val jumlah = binding.etJumlah

    }
}