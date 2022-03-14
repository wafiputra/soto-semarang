package com.android.soto.Laporan

import android.content.Context
import android.os.Environment
import android.widget.Toast
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.IndexedColorMap
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class excel {
    private lateinit var file: File
    private lateinit var fileOutputStream: FileOutputStream
    private lateinit var context: Context

    fun createWorkbook(): Workbook {
        val workbook = XSSFWorkbook()
        val sheet: Sheet = workbook.createSheet("Laporan Bulanan")
        val cellStyle = getHeaderStyle(workbook)

        createSheetHeader(cellStyle, sheet)
        addData(0,sheet)

        return workbook
    }

    fun createSheetHeader(cellStyle: CellStyle, sheet: Sheet) {
        val row = sheet.createRow(0)
        val HEADER_LIST = listOf("column_1", "column_2", "column_3")
        for ((index, value) in HEADER_LIST.withIndex()) {
            val columnWidth = (15*500)
            sheet.setColumnWidth(index, columnWidth)
            val cell = row.createCell(index)
            cell?.setCellValue(value)
            cell.cellStyle = cellStyle
        }
    }

    fun  getHeaderStyle(workbook: Workbook): CellStyle {
        val cellStyle: CellStyle = workbook.createCellStyle()
        val colorMap: IndexedColorMap = (workbook as XSSFWorkbook).stylesSource.indexedColors
        val color = XSSFColor(IndexedColors.GREY_50_PERCENT, colorMap).indexed
        cellStyle.fillForegroundColor = color
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)

        return cellStyle
    }

    fun addData(rowIndex: Int, sheet: Sheet) {
        val row = sheet.createRow(rowIndex)

        createCell(row, 0, "value 1")
        createCell(row, 1, "value 2")
        createCell(row, 2, "value 3")
    }

    fun createCell(row: Row, columnIndex: Int, value: String) {
        val cell = row.createCell(columnIndex)
        cell?.setCellValue(value)
    }

    fun createExcel(workbook: Workbook) {

        val df = SimpleDateFormat("yyyyMMdd")

        val folderName = "Laporan Bulanan"
        val fileName = folderName+df.format(Date())+".xls"
        val filePath = File.separator + Environment.getExternalStorageDirectory() + File.separator + folderName + File.separator + fileName
        file = File(File.separator + Environment.getExternalStorageDirectory() + File.separator + folderName)

        if (!file.exists()) {
            file.mkdirs()
        }

        try {
            fileOutputStream = FileOutputStream(filePath)
            workbook.write(fileOutputStream)
            Toast.makeText(context, "Excel created in $filePath", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}