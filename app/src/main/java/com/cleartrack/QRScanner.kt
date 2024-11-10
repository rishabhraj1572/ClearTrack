package com.cleartrack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode


class QRScanner : AppCompatActivity() {

    private var qrContent: String? = null
    private val scanQrCodeLauncher = registerForActivityResult(ScanQRCode()) { result ->
        getResult(result)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_scanner)
        scanQrCodeLauncher.launch(null)
//        finish()


    }


    private fun getResult(result: QRResult) {
        qrContent = when (result) {
            is QRResult.QRSuccess -> {
                result.content.rawValue
                    ?: result.content.rawBytes?.let { String(it) }.orEmpty()
            }
            QRResult.QRUserCanceled -> "User canceled"
            QRResult.QRMissingPermission -> "Missing permission"
            is QRResult.QRError -> "${result.exception.javaClass.simpleName}: ${result.exception.localizedMessage}"
        }

        Log.d("QR RESULT", "QR content: $qrContent")

        val i : Intent = Intent(this,UpdatesActivity::class.java)
        i.putExtra("orderId",qrContent)

        startActivity(Intent(this,UpdatesActivity::class.java))
        finish()



//        Toast.makeText(this, qrContent ?: "No content found", Toast.LENGTH_SHORT).show()
    }
}
