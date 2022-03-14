package com.android.soto.Laporan

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import com.android.soto.R
import com.android.soto.databinding.ActivityLaporanBinding
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class laporanActivity : AppCompatActivity() {

    private lateinit var binding:ActivityLaporanBinding
    private lateinit var ref: DatabaseReference
    var cal = Calendar.getInstance()
    var cal1 = Calendar.getInstance()
    var text = "Tanggal/Bulan/Tahun"
    var tglAwal = ""
    var tglAkhir = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaporanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tglAwal = binding.viewAwal.setText("$text").toString()
        tglAkhir = binding.viewAkhir.setText("$text").toString()

        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view : DatePicker?, year: Int, monthOfyear: Int, dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfyear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        val dateSetListener1 = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view : DatePicker?, year: Int, monthOfyear: Int, dayOfMonth: Int) {
                cal1.set(Calendar.YEAR, year)
                cal1.set(Calendar.MONTH, monthOfyear)
                cal1.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        binding.btnAwal.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@laporanActivity,
                    R.style.MyDatePickerStyle,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        })

        binding.btnAkhir.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@laporanActivity,
                    R.style.MyDatePickerStyle,
                    dateSetListener1,
                    cal1.get(Calendar.YEAR),
                    cal1.get(Calendar.MONTH),
                    cal1.get(Calendar.DAY_OF_MONTH)).show()
            }
        })

        binding.btnDownload.setOnClickListener {
            getData()
        }
    }

    private fun getData() {
        ref = FirebaseDatabase.getInstance().getReference("Riwayat")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.viewAwal.text = sdf.format(cal.getTime())
        binding.viewAkhir.text = sdf.format(cal1.getTime())
    }
}