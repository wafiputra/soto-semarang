package com.android.soto.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.soto.Menu.ubahActivity
import com.android.soto.Model.Menu
import com.android.soto.databinding.ItemListMenuBinding
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class menuViewAdapter(private val menuList : ArrayList<Menu>, private val context: Context) : RecyclerView.Adapter<menuViewAdapter.menuViewHolder>() {

//    private lateinit var mListener: onItemClickListener

//    interface onItemClickListener{
//        fun onItemClick(position: Int, state: String)
//        fun onUpdateClick(position: Int, state: String)
//        fun onDeleteClick(position: Int, state: String)
//    }
//
//    fun setOnItemClickListener(listener: onItemClickListener){
//        mListener = listener
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): menuViewHolder {
        val binding = ItemListMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return menuViewHolder(binding)
//        , mListener
    }

    override fun onBindViewHolder(holder: menuViewHolder, position: Int) {
        val currentItem = menuList[position]

        currentItem?.let {
            val storageReference = FirebaseStorage.getInstance().getReference("Images//${it.gambar}")
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                holder.nama.text = currentItem.nama
                holder.kategori.text = currentItem.kategori
                Glide.with(context).load(uri).into(holder.gambar)
            }
        }

        holder.ubah.setOnClickListener {
            val intent = Intent(context, ubahActivity::class.java)
            intent.putExtra("id", currentItem.id)
            context.startActivity(intent)
        }
        holder.hapus.setOnClickListener {
            val ref = FirebaseDatabase.getInstance().getReference("Menu")
            val storRef = FirebaseStorage.getInstance()

            val builder = AlertDialog.Builder(context)
            builder.setMessage("Apakah anda yakin akan menghapus ${currentItem.nama} ?")
                .setCancelable(false)
                .setPositiveButton("Yakin") { dialog, id ->
                    ref.child(currentItem.id.toString()).removeValue().addOnSuccessListener {
                        storRef.getReference("Images//${currentItem.gambar}").delete().addOnSuccessListener {
                            Toast.makeText(context, "Menu berhasil dihapus", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(context, "Gagal menghapus menu", Toast.LENGTH_SHORT).show()
                    }

                }.setNegativeButton("Batal") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }


    }

    override fun getItemCount(): Int {

        return menuList.size
    }

    class menuViewHolder(val binding: ItemListMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        val nama = binding.tvMenu
        val kategori = binding.tvKategori
        val satuan = binding.tvSatuan
        val gambar = binding.ivMenu
        val ubah = binding.btnUbah
        val hapus = binding.btnHapus
//        , listener: onItemClickListener
//        init {
//
//            binding.listItem.setOnClickListener {
//                listener.onItemClick(adapterPosition, "item")
//            }
//            binding.btnUbah.setOnClickListener {
//                listener.onUpdateClick(adapterPosition, "update")
//            }
//            binding.btnHapus.setOnClickListener {
//                listener.onDeleteClick(adapterPosition, "delete")
//            }
//
//        }
    }
}