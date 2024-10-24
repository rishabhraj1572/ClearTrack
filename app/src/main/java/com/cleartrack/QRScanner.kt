package com.cleartrack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.g00fy2.quickie.ScanQRCode


class QRScanner : AppCompatActivity() {
    val scanQrCodeLauncher = registerForActivityResult(ScanQRCode()) { result ->
        // Handle the QR result

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_scanner)
        scanQrCodeLauncher.launch(null)
    }
}
