package com.cleartrack

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore

class ShowDetailsActiivty : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var orderId : String
    private lateinit var image : ImageView

    private lateinit var expDocButton: Button
    private lateinit var impDocButton: Button
    private lateinit var goodsDocButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_details_actiivty)

        expDocButton = findViewById(R.id.exp_doc)
        impDocButton = findViewById(R.id.imp_doc)
        goodsDocButton = findViewById(R.id.goods_doc)

        db = FirebaseFirestore.getInstance()
        orderId = intent.getStringExtra("orderId").toString()
        fetchDataFromFirestore()

        expDocButton.setOnClickListener {
            fetchAndDownloadDocument(orderId, "exporter_doc")
        }

        impDocButton.setOnClickListener {
            fetchAndDownloadDocument(orderId, "importer_doc")
        }

        goodsDocButton.setOnClickListener {
            fetchAndDownloadDocument(orderId, "goods_doc")
        }

        Log.e("Order id",orderId)

    }

    private fun fetchAndDownloadDocument(orderID: String, docName: String) {
        db.collection("orders").document(orderID).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val docUrl = documentSnapshot.getString(docName)
                    if (docUrl != null) {
                        downloadPdf(docUrl, "$docName.pdf")
                        openInBrowser(docUrl)
                    } else {
                        Toast.makeText(this, "Document URL not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch document: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openInBrowser(url: String) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        } catch (e: Exception) {
            Toast.makeText(this, "Could not open browser: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun downloadPdf(url: String, fileName: String) {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)

        val downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        if (!downloadDirectory.exists()) {
            downloadDirectory.mkdirs()
        }
        val request = DownloadManager.Request(uri)
            .setTitle(fileName)
            .setDescription("Downloading PDF document")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName) // Set to public directory

        try {
            val downloadId = downloadManager.enqueue(request)
            Log.d("Download", "Download started with ID: $downloadId")
            Toast.makeText(this, "Download started", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Handle any exceptions that occur
            Toast.makeText(this, "Download failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }



    private fun fetchDataFromFirestore() {
        val userRef =
            db.collection("orders").document(orderId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fullName = document.get("name").toString()
                    val phoneNumber = document.get("phone") .toString()
                    val email = document.get("email").toString()
                    val expAddress = document.get("exporter_address").toString()
                    val impAddress = document.get("importer_address").toString()
                    val impName = document.get("importer_name").toString()
                    val impPhone = document.get("importer_number").toString()
                    val productDesc = document.get("product_description").toString()
                    val userimage = document.get("user_image").toString()
                    findViewById<TextView>(R.id.fullName).text = fullName
                    findViewById<TextView>(R.id.phoneNumber).text = phoneNumber
                    findViewById<TextView>(R.id.email).text = email
                    findViewById<TextView>(R.id.exp_address).text = expAddress
                    findViewById<TextView>(R.id.imp_address).text = impAddress
                    findViewById<TextView>(R.id.imp_name).text = impName
                    findViewById<TextView>(R.id.imp_phone).text = impPhone
                    findViewById<TextView>(R.id.product_desc).text = productDesc

                    Glide.with(this)
                        .load(userimage)
                        .apply(
                            RequestOptions()
                            .override(150, 150)
                            .centerCrop())
                        .into(findViewById<ImageView>(R.id.select_photo))

                } else {
                    Toast.makeText(this, "Document not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Error fetching data: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}