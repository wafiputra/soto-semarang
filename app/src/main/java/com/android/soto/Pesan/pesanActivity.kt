package com.android.soto.Pesan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.android.soto.Adapter.pesanViewAdapter
import com.android.soto.MainActivity
import com.android.soto.Model.Menu
import com.android.soto.Model.Riwayat
import com.android.soto.Model.Transaksi
import com.android.soto.Model.laporanHarian
import com.android.soto.databinding.ActivityPesanBinding
import com.google.firebase.database.*
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class pesanActivity : AppCompatActivity(), PrintingCallback {

    private lateinit var binding: ActivityPesanBinding
    private lateinit var menuArrayList: ArrayList<Menu>
    private lateinit var refM: DatabaseReference
    private lateinit var refR: DatabaseReference
    internal var printing: Printing? = null
    var idR = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvListPesanan.layoutManager = GridLayoutManager(this, 2)
        refR = FirebaseDatabase.getInstance().getReference("Riwayat")
        idR = refR.push().key.toString()
        menuArrayList = arrayListOf<Menu>()

        getData()

        binding.btnKembali.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.btnLaporan.setOnClickListener {
            createReport()
        }

        initView()
    }


    // FOR PRINTING
    private fun initView() {
        if (printing != null) {
            printing!!.printingCallback = this
        }
        Log.d("test", "$printing")

        binding.btnPesan.setOnClickListener {
//            saveData()
            if (!Printooth.hasPairedPrinter()) {
                startActivityForResult(Intent(this, ScanningActivity::class.java), ScanningActivity.SCANNING_FOR_PRINTER)
            } else {
                saveData()
            }
        }

        binding.btnPair.setOnClickListener {
            if (Printooth.hasPairedPrinter()) {
                changePairandUnpair()
            } else {
                startActivityForResult(Intent(this@pesanActivity, ScanningActivity::class.java), ScanningActivity.SCANNING_FOR_PRINTER)
                changePairandUnpair()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK) {
            initPrinting()
        }

        changePairandUnpair()
    }

    private fun initPrinting() {
        if (Printooth.hasPairedPrinter()) {
            printing = Printooth.printer()
            Log.d("test", "$printing")
        } else if (printing != null) {
            printing!!.printingCallback = this
        }
    }

    private fun changePairandUnpair() {
        if (Printooth.hasPairedPrinter()) {
            binding.btnPair.text = "Unpair ${Printooth.getPairedPrinter()!!.name}"
        } else {
            binding.btnPair.text = "Pair with Printer"
        }
    }

    private fun saveData() {
        val nama = binding.etPemesan.text.toString().trim()
        val df = SimpleDateFormat("yyyyMMdd")
        val tanggal = df.format(Date())
        val riwayat = Riwayat(idR, nama, tanggal)
        refR.child(idR).setValue(riwayat).addOnSuccessListener {
            Toast.makeText(this, "Print struk", Toast.LENGTH_SHORT).show()
            print()
            //for refresh page
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Gagal menyimpan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun print() {
        val ref = FirebaseDatabase.getInstance().getReference("Transaksi")
        val printable = java.util.ArrayList<Printable>()
        printable.add(RawPrintable.Builder(byteArrayOf(27, 100, 4)).build())

        printable.add(
            TextPrintable.Builder()
                .setText("Soto Semarang Pak Noor")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setNewLinesAfter(1)
                .setLineSpacing(1)
                .build())
        printable.add(
            TextPrintable.Builder()
                .setText("Nama Menu                 Jumlah")
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setLineSpacing(1)
                .setNewLinesAfter(1)
                .build())

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                    for (snapT in snapshot.children) {
                        var dataT = snapT.getValue(Transaksi::class.java)
                        dataT?.let {
                            if (it.idRiwayat.equals("$idR")) {
                                refM.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            for (snapM in snapshot.children) {
                                                var dataM = snapM.getValue(Menu::class.java)
                                                dataM?.let {
                                                    if (dataT.idMenu.equals(it.id)) {
                                                        Log.d("test", "nama : ${it.nama} jumlah : ${dataT.jumlah}")
                                                        printable.add(
                                                            TextPrintable.Builder()
                                                                .setText("${it.nama}")
                                                                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                                                                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                                                                .build())
                                                        printable.add(
                                                            TextPrintable.Builder()
                                                                .setText("${dataT.jumlah}")
                                                                .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
                                                                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                                                                .setLineSpacing(5)
                                                                .build())
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                })
                            }
                        }
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        printable.add(
            TextPrintable.Builder()
                .setText("---Terima Kasih---")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setLineSpacing(1)
                .build())

//        Log.d("test", "$printable")

        printing?.print(printable)
    }

    //FOR DAILY REPORT
    private fun createReport() {
        val df = SimpleDateFormat("yyyyMMdd")
        val laporanArrayList = ArrayList<laporanHarian>()
        val ref = FirebaseDatabase.getInstance().getReference("Transaksi")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snapT in snapshot.children) {
                        val dataT = snapT.getValue(Transaksi::class.java)
                        if (dataT?.tanggal.equals(df.format(Date()))) {
                            val ref = FirebaseDatabase.getInstance().getReference("Menu")
                            ref.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        for (snapM in snapshot.children) {
                                            val dataM = snapM.getValue(Menu::class.java)
                                            if (dataT?.idMenu.equals(dataM?.id)) {
                                                if (dataM?.kategori.equals("Makanan")) {
                                                    dataT?.let {
                                                        laporanArrayList.add(
                                                                laporanHarian(
                                                                        id = it.id,
                                                                        nama = dataM?.nama,
                                                                        tgl = it.tanggal!!.toDate(),
                                                                        jumlah = it.jumlah!!.toInt()
                                                                )
                                                        )
                                                        data(df.format(Date()).toString(), laporanArrayList)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun data(date: String, list: ArrayList<laporanHarian>) {
        val tgl = date.toDate()
        val historyList = ArrayList<laporanHarian>()

        for (history in list) {
            historyList.add(
                    laporanHarian(
                            id = history.id,
                            nama = history.nama,
                            tgl = history.tgl,
                            jumlah = history.jumlah
                    )
            )
        }
        val sumCount = historyList.sumOf { it.jumlah }
        val hasil = "Jumlah penjualan makanan pada $tgl = $sumCount porsi"
        sendMessage(hasil)
    }

    //FOR SENT DAILY REPORT
    private fun sendMessage(hasil: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.setPackage("com.whatsapp")
        intent.putExtra(Intent.EXTRA_TEXT, hasil)
        startActivity(intent)
    }

    //FOR RETRIEVE RECYCLERVIEW DATA
    private fun getData() {
        refM = FirebaseDatabase.getInstance().getReference("Menu")
        refM.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    menuArrayList.clear()
                    for (menuSnapshot in snapshot.children) {
                        val menu = menuSnapshot.getValue(Menu::class.java)
                        menuArrayList.add(menu!!)
                    }
                    var adapter = pesanViewAdapter(menuArrayList, this@pesanActivity, idR)
                    binding.rvListPesanan.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun String.toDate(format: String? = "yyyyMMdd"): Date {
        val localeID = Locale("in", "ID")
        return try {
            val formatter = SimpleDateFormat(format, localeID)
            formatter.parse(this) ?: Date()
        } catch (e: Exception) {
            Date()
        }
    }

    override fun connectingWithPrinter() {
        Toast.makeText(this, "Connecting to printer", Toast.LENGTH_SHORT).show()
    }

    override fun connectionFailed(error: String) {
        Toast.makeText(this, "Failed $error", Toast.LENGTH_SHORT).show()
    }

    override fun onError(error: String) {
        Toast.makeText(this, "Error $error", Toast.LENGTH_SHORT).show()
    }

    override fun onMessage(message: String) {
        Toast.makeText(this, "Message $message", Toast.LENGTH_SHORT).show()
    }

    override fun printingOrderSentSuccessfully() {
        Toast.makeText(this, "Order sent to Printer", Toast.LENGTH_SHORT).show()
    }
}