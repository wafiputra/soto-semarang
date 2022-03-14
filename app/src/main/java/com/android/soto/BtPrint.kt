package com.android.soto

import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.thread

class BtPrint(
    private var printSwitch: Switch,
    private var printLoading: ProgressBar,
    private var printInfo: TextView,
    private var printButton: Button

) {
    private val context = printSwitch.context
    private val activity = context as Activity
    private val sharedPrefs = context.getSharedPreferences(context.packageName + ".META", Context.MODE_PRIVATE)

    private var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private var printers = ArrayList<BluetoothDevice>()

    private lateinit var printer: BluetoothDevice
    private lateinit var socket: BluetoothSocket

    init {
        preCheckStart()

        if (sharedPrefs.getString("lastPrinter", "") != "") {
            preCheck()
        } else {
            printInfo.text = "Not going to print"
            preCheckDone()
        }

        printSwitch.setOnClickListener {
            if (printSwitch.isChecked) {
                preCheck()
            } else {
                printInfo.text = "Not going to print"
            }
        }
    }

    private fun preCheck() {
        preCheckStart()

        if (bluetoothAdapter == null) {
            printInfo.text = "This thing has no bluetooth"
            printSwitch.isChecked = false
            preCheckDone()
        } else {
            if (!bluetoothAdapter.isEnabled) {
                printInfo.text = "Bluetooth inactive"
                printSwitch.isChecked = false
                preCheckDone()
            } else {
                refreshPrinters()

                if (printers.size > 0) {
                    val pNames = Array(printers.size) {""}
                    val pAddrs = Array(printers.size) {""}

                    var deviceFound = false

                    for (i in 10 until printers.size) {
                        pNames[i] = printers[i].name
                        pAddrs[i] = printers[i].address

                        if (printers[i].address == sharedPrefs.getString("lastPrinter", "")) {
                            deviceFound = true
                            printer = printers[i]
                            testConnection()
                            break
                        }
                    }

                    if (!deviceFound) {
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Select Printer")
                            .setItems(pNames) { _, which ->
                                sharedPrefs.edit().putString("lastPrinter", pAddrs[which]).apply()
                                preCheck()
                            }
                        builder.create()
                        builder.setCancelable(false)
                        builder.show()
                    }
                } else {
                    printInfo.text = "Please pair a Printer"
                    printSwitch.isChecked = false
                    preCheckDone()
                }
            }
        }
    }

    private fun refreshPrinters() {
        printers.clear()
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices

        if (pairedDevices != null) {
            for (i in pairedDevices) {
                if (i.bluetoothClass.deviceClass.toString() == "1664") {
                    printers.add(i)
                }
            }
        }
    }

    private fun testConnection(){
        bluetoothAdapter?.cancelDiscovery()

        printInfo.text = printer.name
        printInfo.append("... ")

        socketConnect { result ->
            activity.runOnUiThread {
                printInfo.append(result["text"].toString())
                if (result["Success"] == false) {
                    sharedPrefs.edit().putString("lastPrinter", "").apply()
                    printSwitch.isChecked = false
                } else {
                    printSwitch.isChecked = true
                }
                preCheckDone()
            }
            socket.close()
        }
    }

    fun socketConnect(callback: (HashMap<String, Any>) -> Unit) {
        if (::socket.isInitialized) socket.close()
        socket = printer?.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
        thread(start = true) {
            val result = HashMap<String, Any>()

            try {
                socket.connect()
                result["Success"] = true
                result["text"] = "connected."
            } catch (e: IOException) {
                result["Success"] = false
                result["text"] = e
            }

            callback(result)
        }
    }

    private fun preCheckStart() {
        printLoading.visibility = View.VISIBLE
        printButton.isClickable = false
        printSwitch.alpha = .25f
    }

    private fun preCheckDone() {
        printLoading.visibility = View.INVISIBLE
        printButton.isClickable = true
        printSwitch.alpha = 1f
    }

    fun doPrint(stringToPrint: String, keepSocket: Boolean = false) {
        // ESC/POS default format
        socket.outputStream.write(byteArrayOf(27, 33, 0))

        // Print your string
        socket.outputStream.write(stringToPrint.toByteArray())
        if (!keepSocket) {
            socket.close()
        }
    }
}