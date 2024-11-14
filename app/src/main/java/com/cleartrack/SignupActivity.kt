package com.cleartrack

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class SignupActivity : AppCompatActivity() {

    private var userId: String? = null
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid

        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val cnfPassEditText: EditText = findViewById(R.id.cnfPassEditText)
        val signupButton: Button = findViewById(R.id.signUpBtn)

        val back : Button = findViewById(R.id.back)

        back.setOnClickListener {
            finish()
        }

        // user signup
        signupButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = cnfPassEditText.text.toString().trim()

            // Validate input fields
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Register user
            registerUser(email, password)
        }
    }

    //register a user
    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
//                    startActivity(Intent(this,HomeActivity::class.java))

                    userId = auth.currentUser?.uid
                    //ask user
                    val userDetails = mapOf(
                        "user_id" to userId,
                        "address" to "null",
                        "pincode" to "null",
                        "company" to "null",
                        "is_logistic_partner" to false
                    )

                    userId?.let {
                        db.collection("users").document(it).set(userDetails)
                            .addOnSuccessListener { documentReference ->
                                showLogisticPartnerDialog()
                            }
                            .addOnFailureListener { e ->
                            }
                    }

//                    showLogisticPartnerDialog()

//                    finishAffinity()
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showLogisticPartnerDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Are you a Logistic Partner?")
        builder.setMessage("Please confirm if you are a Logistic partner.")

        builder.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()
            showPartnerDetailsDialog() // Show the details dialog
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
//            proceedToNextStep() // Proceed to the next step (QR activity)
            startActivity(Intent(this,HomeActivity::class.java))
            finishAffinity()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showPartnerDetailsDialog() {
        // Create a dialog for entering company details
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter Your Company Details")

        val companyNameInput = EditText(this)
        companyNameInput.hint = "Company Name"
        val locationInput = EditText(this)
        locationInput.hint = "Location"
        val pincodeInput = EditText(this)
        pincodeInput.hint = "Pincode"

        val emailInput = EditText(this)
        emailInput.hint = "Email"
        emailInput.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        val phoneInput = EditText(this)
        phoneInput.hint = "Phone Number"
        phoneInput.inputType = InputType.TYPE_CLASS_PHONE

        // LinearLayout for edit text
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(companyNameInput)
        layout.addView(locationInput)
        layout.addView(pincodeInput)
        layout.addView(emailInput)
        layout.addView(phoneInput)

        builder.setView(layout)

        builder.setPositiveButton("Submit") { dialog, _ ->
            val companyName = companyNameInput.text.toString()
            val location = locationInput.text.toString()
            val pincode = pincodeInput.text.toString()
            val email = emailInput.text.toString()
            val phone = phoneInput.text.toString()
            if (companyName.isNotEmpty() && location.isNotEmpty() && pincode.isNotEmpty()) {
                // Store the details or process them as needed
                // Proceed to the QR generation activity
//                proceedToNextStep(companyName, location, pincode)
                val updateDetails = mapOf(
                    "address" to location,
                    "company" to companyName,
                    "pincode" to pincode,
                    "email" to email,
                    "phone" to phone,
                    "is_logistic_partner" to true
                )

                userId?.let {
                    db.collection("users").document(it).update(updateDetails)
                        .addOnSuccessListener { documentReference ->
                            proceedToNextStep()
                        }
                        .addOnFailureListener { e ->
                        }
                }

            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun proceedToNextStep() {
        // Proceed to the QR generation activity
        val intent = Intent(this@SignupActivity, HomeActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

}