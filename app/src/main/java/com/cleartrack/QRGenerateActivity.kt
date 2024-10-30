package com.cleartrack

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.IOException

class QRGenerateActivity : AppCompatActivity() {

    private lateinit var qrCodeIV: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_qr)

        val orderID = intent.getStringExtra("orderId") ?: "empty"

        qrCodeIV = findViewById(R.id.idIVQrcode)

        val bitmap = generateQRCode(orderID)

        val saveQr : Button = findViewById(R.id.saveQR)
        val shareQr : Button = findViewById(R.id.shareQR)

        saveQr.setOnClickListener{
            if (bitmap != null) {
                saveQRCode(bitmap)
            }
        }

        shareQr.setOnClickListener{
            if (bitmap != null) {
                shareQRCode(bitmap)
            }
        }

    }

    private fun generateQRCode(text: String): Bitmap? {
        val barcodeEncoder = BarcodeEncoder()
        return try {
            // This method returns a Bitmap image of the
            // encoded text with a height and width of 400 pixels.
            val bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 400, 400)
            qrCodeIV.setImageBitmap(bitmap)
            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }


    private fun saveQRCode(bitmap: Bitmap): Uri? {
        val filename = "ClearTrack_${System.currentTimeMillis()}.png"
        var imageUri: Uri? = null

        try {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/QRCode")
            }

            val resolver = contentResolver
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            imageUri?.let { uri ->
                resolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
            }

            Toast.makeText(this, "QR Code saved to Gallery", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save QR Code", Toast.LENGTH_SHORT).show()
        }

        return imageUri
    }


    private fun shareQRCode(bitmap: Bitmap) {
        val uri = saveQRCode(bitmap) ?: return
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/png"
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        startActivity(Intent.createChooser(shareIntent, "Share QR Code"))
    }



}
