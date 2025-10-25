package com.example.easyemi.addProduct

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import java.io.FileOutputStream

class BarcodePrintAdapter(private val context: Context, private val bitmap: Bitmap) : PrintDocumentAdapter() {
    override fun onLayout(
        oldAttributes: PrintAttributes?, newAttributes: PrintAttributes?,
        cancellationSignal: android.os.CancellationSignal?, callback: LayoutResultCallback?, extras: android.os.Bundle?
    ) {
        callback?.onLayoutFinished(PrintDocumentInfo.Builder("barcode.pdf")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .build(), true)
    }

    override fun onWrite(
        pages: Array<out android.print.PageRange>?, destination: android.os.ParcelFileDescriptor?,
        cancellationSignal: android.os.CancellationSignal?, callback: WriteResultCallback?
    ) {
        PdfDocument().apply {
            val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
            val page = startPage(pageInfo)
            page.canvas.drawBitmap(bitmap, 0f, 0f, null)
            finishPage(page)
            writeTo(FileOutputStream(destination?.fileDescriptor))
            close()
        }
        callback?.onWriteFinished(arrayOf(android.print.PageRange.ALL_PAGES))
    }
}
