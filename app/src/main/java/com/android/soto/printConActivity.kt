package com.android.soto

import android.app.Activity
import android.bluetooth.BluetoothSocket
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.soto.databinding.ActivityPrintConBinding
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import java.io.OutputStream
import java.util.ArrayList

class printConActivity : AppCompatActivity(), PrintingCallback {

    private lateinit var binding: ActivityPrintConBinding
    internal var printing: Printing? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrintConBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        if (printing != null) {
            printing!!.printingCallback = this
        }
        Log.d("test", "$printing")

        binding.btnPair.setOnClickListener {
            if (Printooth.hasPairedPrinter()) {
//                Printooth.removeCurrentPrinter()
                changePairandUnpair()
            } else {
                startActivityForResult(Intent(this@printConActivity, ScanningActivity::class.java), ScanningActivity.SCANNING_FOR_PRINTER)
                changePairandUnpair()
            }
        }

        binding.btnImage.setOnClickListener {
            if (!Printooth.hasPairedPrinter()) {
                startActivityForResult(Intent(this, ScanningActivity::class.java), ScanningActivity.SCANNING_FOR_PRINTER)
            } else {
                printImage()
            }
        }

        binding.printButton.setOnClickListener {
            if (!Printooth.hasPairedPrinter()) {
                startActivityForResult(Intent(this, ScanningActivity::class.java), ScanningActivity.SCANNING_FOR_PRINTER)
            } else {
                printText()
            }
        }
    }

    private fun printText() {
        val printable = ArrayList<Printable>()
        printable.add(RawPrintable.Builder(byteArrayOf(27,100,4)).build())

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
        printable.add(
            TextPrintable.Builder()
                .setText("---Terima Kasih---")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setLineSpacing(1)
                .build())

        printing!!.print(printable)
    }

    private fun printImage() {
        TODO("Not yet implemented")
    }

    private fun changePairandUnpair() {
        if (Printooth.hasPairedPrinter()) {
            binding.btnPair.text = "Unpair ${Printooth.getPairedPrinter()!!.name}"
        } else {
            binding.btnPair.text = "Pair with Printer"
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