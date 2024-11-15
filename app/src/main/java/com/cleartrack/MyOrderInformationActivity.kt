package com.cleartrack

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.io.FileOutputStream

class MyOrderInformationActivity : AppCompatActivity(){

    private lateinit var qrCodeIV: ImageView
    private lateinit var showDetailsBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_order_information)

        val qr_url = intent.getStringExtra("qr_url")
        val orderId = intent.getStringExtra("orderId")

        qrCodeIV = findViewById(R.id.idIVQrcode)

        val saveQr : Button = findViewById(R.id.saveQR)
        val shareQr : Button = findViewById(R.id.shareQR)
        showDetailsBtn = findViewById(R.id.showDetails)

        Glide.with(this)
            .load(qr_url)
            .apply(
                RequestOptions()
                    .override(150, 150)
                    .centerCrop())
            .into(findViewById<ImageView>(R.id.idIVQrcode))

        saveQr.setOnClickListener{
            if (qr_url != null) {
                saveQr(qr_url)
            }
        }

        shareQr.setOnClickListener{
            if (qr_url != null) {
                shareBitmapFromUrl(this,qr_url)
            }
        }

        showDetailsBtn.setOnClickListener{
            val intent = Intent(this, ShowDetailsActiivty::class.java)
            intent.putExtra("orderId",orderId)
            startActivity(intent)
        }


    }

    private fun saveQr(url : String){

        val filename = "ClearTrack_${System.currentTimeMillis()}.png"
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)

        val downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        if (!downloadDirectory.exists()) {
            downloadDirectory.mkdirs()
        }
        val request = DownloadManager.Request(uri)
            .setTitle(filename)
//            .setDescription("Downloading PDF document")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, filename) // Set to public directory

        try {
            val downloadId = downloadManager.enqueue(request)
//            Log.d("Download", "Download started with ID: $downloadId")
//            Toast.makeText(this, "Download started", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Handle any exceptions that occur
            Toast.makeText(this, "Download failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }

    }

    fun shareBitmapFromUrl(context: Context, imageUrl: String) {
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .into(object : com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                    // Save the bitmap to a temporary file
                    val cachePath = File(context.externalCacheDir, "shared_images")
                    cachePath.mkdirs() // Create directory if not exists
                    val file = File(cachePath, "image.png")
                    FileOutputStream(file).use { out ->
                        resource.compress(Bitmap.CompressFormat.PNG, 100, out) // Save bitmap
                    }

                    // Get the URI using FileProvider
                    val fileUri: Uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )

                    // Create a sharing intent
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "image/png"
                        putExtra(Intent.EXTRA_STREAM, fileUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    // Start the sharing intent
                    context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
                }

                override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {
                    // Handle if needed when the load is cleared
                }
            })
    }
}
