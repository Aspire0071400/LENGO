package com.uniix.lengo_chatapp.auth

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.uniix.lengo_chatapp.MainActivity
import com.uniix.lengo_chatapp.databinding.ActivitySignUpBinding
import com.uniix.lengo_chatapp.models.User
import java.util.*


class SignUpActivity : AppCompatActivity() {

    private lateinit var signUpActivity: ActivitySignUpBinding

    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseFirestore.getInstance()
    private lateinit var downloadUrl: String
    private lateinit var userPhoneNumber: String
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpActivity = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(signUpActivity.root)

        userPhoneNumber = intent.getStringExtra("PHONE_NUMBER").toString()

        signUpActivity.userImage.setOnClickListener {
            checkPermissionForImage()
        }

        signUpActivity.dob.setOnClickListener {
            val calender = Calendar.getInstance()
            val year = calender.get(Calendar.YEAR)
            val month = calender.get(Calendar.MONTH)
            val day = calender.get(Calendar.DAY_OF_MONTH)
            val dialog = DatePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                dateSetListener,
                year,
                month,
                day
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }

        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val date = "$day/${month + 1}/$year"
            signUpActivity.dob.setText(date)
        }

        signUpActivity.nextBtn.setOnClickListener {
            signUpActivity.nextBtn.isEnabled = false
            val name = signUpActivity.name.text.toString()
            val dob = signUpActivity.dob.text.toString()
            val gender = when {
                signUpActivity.male.isChecked -> {
                    "Male"
                }
                signUpActivity.female.isChecked -> {
                    "Female"
                }
                else -> {
                    "Other"
                }
            }
            if (!::downloadUrl.isInitialized) {
                Toast.makeText(this, "Photo cannot be empty!", Toast.LENGTH_SHORT).show()
            } else if (name.isEmpty()) {
                Toast.makeText(this, "Name cannot be empty!", Toast.LENGTH_SHORT).show()
            } else if (dob.isEmpty()) {
                Toast.makeText(this, "DOB cannot be empty!", Toast.LENGTH_SHORT).show()
            } else if (gender.isEmpty()) {
                Toast.makeText(this, "Please select your gender!", Toast.LENGTH_SHORT).show()
            } else {
                val user = User(
                    name,
                    downloadUrl,
                    downloadUrl/*Needs to thumbnail url*/,
                    auth.uid!!,
                    dob,
                    gender,
                    userPhoneNumber
                )
                database.collection("users").document(auth.uid!!).set(user).addOnSuccessListener {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener {
                    signUpActivity.nextBtn.isEnabled = true
                }
            }
        }
    }

    private fun checkPermissionForImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                && (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ) {
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                val permissionWrite = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(
                    permission,
                    1001
                ) // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_READ LIKE 1001
                requestPermissions(
                    permissionWrite,
                    1002
                ) // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_WRITE LIKE 1002
            } else {
                pickImageFromGallery()
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(
            intent,
            1000
        ) // GIVE AN INTEGER VALUE FOR IMAGE_PICK_CODE LIKE 1000
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1000) {
            data?.data?.let {
                signUpActivity.userImage.setImageURI(it)
                startUpload(it)
            }
        }
    }

    private fun startUpload(filePath: Uri) {
        signUpActivity.nextBtn.isEnabled = false
        progressDialog = createProgressDialog("Uploading Image...", false)
        progressDialog.show()
        val ref = storage.reference.child("uploads/" + auth.uid.toString())
        val uploadTask = ref.putFile(filePath)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                downloadUrl = task.result.toString()
                progressDialog.dismiss()
                signUpActivity.nextBtn.isEnabled = true
            } else {
                signUpActivity.nextBtn.isEnabled = true
                // Handle failures
                Toast.makeText(this, "Something went wrong. Please try again!", Toast.LENGTH_LONG)
                    .show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Something went wrong. Please try again!", Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onBackPressed() {}

    private fun createProgressDialog(message: String, isCancelable: Boolean): ProgressDialog {
        return ProgressDialog(this).apply {
            setCancelable(isCancelable)
            setCanceledOnTouchOutside(false)
            setMessage(message)
        }
    }
}