package com.uniix.lengo_chatapp.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SimPhonebookContract.SimRecords.PHONE_NUMBER
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.uniix.lengo_chatapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var loginActivity: ActivityLoginBinding
    private lateinit var phoneNumber: String
    private lateinit var countryCode: String
    private lateinit var alertDialogBuilder: MaterialAlertDialogBuilder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginActivity = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginActivity.root)

        loginActivity.phoneNumber.addTextChangedListener {
            loginActivity.next.isEnabled = !(it.isNullOrEmpty() || it.length < 10)
        }

        // Hint Request for Phone Number
        loginActivity.next.setOnClickListener {
            checkNumber()
        }
    }

    private fun checkNumber() {
        /*countryCode = loginActivity.ccp.selectedCountryCodeWithPlus
                                <com.hbb20.CountryCodePicker
                                android:id="@+id/ccp"
                                android:layout_width="wrap_content"
                                android:layout_height="wrap_content"
                                android:layout_gravity="center"
                                android:gravity="center"
                                app:ccp_autoDetectCountry="true"
                                app:ccp_showNameCode="false"
                                tools:ccp_contentColor="#fcfdfd"
                                tools:ccp_textSize="20dp" /> */

        phoneNumber = countryCode + loginActivity.phoneNumber.text.toString()

        if (validatePhoneNumber(loginActivity.phoneNumber.text.toString())) {
            notifyUserBeforeVerify(
                "We will be verifying the phone number:$phoneNumber\n" +
                        "Is this OK, or would you like to edit the number?"
            )
        } else {
            Toast.makeText(this, "Please enter a valid number to continue!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun validatePhoneNumber(phone: String): Boolean {
        if (phone.isEmpty()) {
            return false
        }
        return true
    }

    private fun notifyUserBeforeVerify(message: String) {
        alertDialogBuilder = MaterialAlertDialogBuilder(this).apply {
            setMessage(message)
            setPositiveButton("Ok") { _, _ ->
                showLoginActivity()
            }

            setNegativeButton("Edit") { dialog, _ ->
                dialog.dismiss()
            }

            setCancelable(false)
            create()
            show()
        }
    }

    private fun showLoginActivity() {
        startActivity(Intent(this, OtpActivity::class.java).putExtra(PHONE_NUMBER, phoneNumber))
        finish()
    }
}