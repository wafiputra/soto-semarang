package com.android.soto.Model

import java.util.*

data class laporanHarian(
    val id: String? = null,
    val nama: String? = null,
    val tgl: Date = Date(),
    val jumlah: Int
) {
}