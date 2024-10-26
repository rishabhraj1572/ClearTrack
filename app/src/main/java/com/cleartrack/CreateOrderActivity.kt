package com.cleartrack

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class CreateOrderActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var imageView: ImageView
    private lateinit var expDocButton: Button
    private lateinit var impDocButton: Button
    private lateinit var goodsDocButton: Button

    private lateinit var expAttStatus: TextView
    private lateinit var impAttStatus: TextView
    private lateinit var goodsAttStatus: TextView

    private var currentStatusTextView: TextView? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val PICK_PDF_REQUEST = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        auth = FirebaseAuth.getInstance()
        imageView = findViewById(R.id.select_photo)

        // Initialize buttons and status TextViews
        expDocButton = findViewById(R.id.exp_doc)
        impDocButton = findViewById(R.id.imp_doc)
        goodsDocButton = findViewById(R.id.goods_doc)

        expAttStatus = findViewById(R.id.exp_att_status)
        impAttStatus = findViewById(R.id.imp_att_status)
        goodsAttStatus = findViewById(R.id.goods_att_status)

        //for photo
        imageView.setOnClickListener {
            openImagePicker()
        }

        // click listeners for document upload buttons
        expDocButton.setOnClickListener {
            currentStatusTextView = expAttStatus // Set the current status TextView
            uploadPdf("Exporter Document")
        }

        impDocButton.setOnClickListener {
            currentStatusTextView = impAttStatus // Set the current status TextView
            uploadPdf("Importer Document")
        }

        goodsDocButton.setOnClickListener {
            currentStatusTextView = goodsAttStatus // Set the current status TextView
            uploadPdf("Goods Document")
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun uploadPdf(docType: String) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            PICK_IMAGE_REQUEST -> {
                if (resultCode == RESULT_OK && data != null) {
                    val imageUri: Uri? = data.data
                    imageView.setImageURI(imageUri) // Display the selected image in the ImageView
                    // Handle uploading or other purposes for the image
                }
            }
            PICK_PDF_REQUEST -> {
                if (resultCode == RESULT_OK && data != null) {
                    val pdfUri: Uri? = data.data
                    if (pdfUri != null) {
                        // Handle the PDF upload logic here

                        // Update the attached status
                        currentStatusTextView?.text = "Attached"

                        // Optionally, you could show a message that a new document is attached
                        Toast.makeText(this, "Document attached successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        // Handle error if the URI is null
                        Toast.makeText(this, "Failed to select PDF", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
