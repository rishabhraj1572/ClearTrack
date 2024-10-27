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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CreateOrderActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var imageView: ImageView
    private lateinit var expDocButton: Button
    private lateinit var impDocButton: Button
    private lateinit var goodsDocButton: Button

    private lateinit var expAttStatus: TextView
    private lateinit var impAttStatus: TextView
    private lateinit var goodsAttStatus: TextView
    private lateinit var submitBtn: Button

    private var currentStatusTextView: TextView? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val PICK_PDF_REQUEST_EXPORTER = 2
        private const val PICK_PDF_REQUEST_IMPORTER = 3
        private const val PICK_PDF_REQUEST_GOODS = 4
    }

    private var imgUri: Uri? = null
    private var orderID: String? = null
    
    private var expDocUri: Uri?=null
    private var impDocUri: Uri?=null
    private var goodsDocUri: Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        auth = FirebaseAuth.getInstance()
        orderID = auth.currentUser?.let { orderId(it.uid) }

        imageView = findViewById(R.id.select_photo)

        // Initialize buttons and status TextViews
        expDocButton = findViewById(R.id.exp_doc)
        impDocButton = findViewById(R.id.imp_doc)
        goodsDocButton = findViewById(R.id.goods_doc)
        submitBtn = findViewById(R.id.submiBtn)

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
            attachPDF("Exporter Document")
        }

        impDocButton.setOnClickListener {
            currentStatusTextView = impAttStatus // Set the current status TextView
            attachPDF("Importer Document")
        }

        goodsDocButton.setOnClickListener {
            currentStatusTextView = goodsAttStatus // Set the current status TextView
            attachPDF("Goods Document")
        }


        //submitBtn
        submitBtn.setOnClickListener{

            //uploading all attached files
            if(imgUri == null || expDocUri==null||impDocUri==null||goodsDocUri==null){
                Toast.makeText(this,"Please attach required files",Toast.LENGTH_SHORT).show()
            }else{
                uploadDocs(orderID,"user_image",imgUri,false)
                uploadDocs(orderID,"exporter_doc",expDocUri,true)
                uploadDocs(orderID,"importer_doc",impDocUri,true)
                uploadDocs(orderID,"goods_doc",goodsDocUri,true)
            }


            //firestore work here



        }
    }

    //defining order id
    private fun orderId(userId: String): String {
        val currentTime = System.currentTimeMillis()
        return userId+"_"+currentTime
    }

    //uploading files
    private fun uploadDocs(orderID : String?,doc_name :String, uri : Uri?, is_pdf : Boolean){

        var fileReference: StorageReference?
        
        if(is_pdf){
            fileReference = FirebaseStorage.getInstance().reference.child("order_docs/$orderID/$doc_name.pdf")
        }else{
            fileReference = FirebaseStorage.getInstance().reference.child("order_docs/$orderID/$doc_name.jpg")
        }


        if (uri != null) {
            fileReference.putFile(uri)
                .addOnSuccessListener {
    //                Toast.makeText(this, "File uploaded successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to upload file $doc_name", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun attachPDF(docType: String) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        
        if(docType == "Exporter Document"){
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST_EXPORTER)
        }else if(docType == "Importer Document"){
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST_IMPORTER)
        }else if(docType == "Goods Document"){
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST_GOODS)
        }

       
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            PICK_IMAGE_REQUEST -> {
                if (resultCode == RESULT_OK && data != null) {
                    val imageUri: Uri? = data.data
                    imageView.setImageURI(imageUri) //replace with glide or set image size up to 500kb

                    //imageUri here
                    imgUri = imageUri

                }
            }
            PICK_PDF_REQUEST_EXPORTER -> {
                if (resultCode == RESULT_OK && data != null) {
                    val pdfUri: Uri? = data.data
                    if (pdfUri != null) {
                        currentStatusTextView?.text = "Attached"

                        //pdf uri here
                        expDocUri = pdfUri
                        Toast.makeText(this, "Document attached successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to select PDF", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            PICK_PDF_REQUEST_IMPORTER -> {
                if (resultCode == RESULT_OK && data != null) {
                    val pdfUri: Uri? = data.data
                    if (pdfUri != null) {
                        currentStatusTextView?.text = "Attached"

                        //pdf uri here
                        impDocUri = pdfUri
                        Toast.makeText(this, "Document attached successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to select PDF", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            
            PICK_PDF_REQUEST_GOODS -> {
                if (resultCode == RESULT_OK && data != null) {
                    val pdfUri: Uri? = data.data
                    if (pdfUri != null) {
                        currentStatusTextView?.text = "Attached"

                        //pdf uri here
                        goodsDocUri = pdfUri
                        Toast.makeText(this, "Document attached successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to select PDF", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
