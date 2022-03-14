package com.android.soto.Model

data class Transaksi(
        val id : String ?= null,
        val idMenu : String ?= null,
        val tanggal : String ?= null,
        val jumlah : String ?= null,
        val idRiwayat : String ?= null
)