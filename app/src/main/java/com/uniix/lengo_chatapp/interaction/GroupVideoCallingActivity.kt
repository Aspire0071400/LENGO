package com.uniix.lengo_chatapp.interaction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uniix.lengo_chatapp.MainActivity
import com.uniix.lengo_chatapp.R
import com.uniix.lengo_chatapp.databinding.ActivityGroupVideoCallingBinding
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.MalformedURLException
import java.net.URL

class GroupVideoCallingActivity : AppCompatActivity() {

    private lateinit var groupVideoCallingActivity: ActivityGroupVideoCallingBinding
    private lateinit var url: URL
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseFirestore.getInstance()
    private lateinit var appName: String
    private lateinit var phoneNumber: String
    private lateinit var tokenCode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupVideoCallingActivity = ActivityGroupVideoCallingBinding.inflate(layoutInflater)
        setContentView(groupVideoCallingActivity.root)

        setSupportActionBar(groupVideoCallingActivity.toolbarGroupVideoCall)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(true)
        }

        try {
            url = URL("https://meet.jit.si")
            val defaultOptions: JitsiMeetConferenceOptions = JitsiMeetConferenceOptions.Builder()
                .setServerURL(url)
                .setWelcomePageEnabled(false)
                .build()
            JitsiMeet.setDefaultConferenceOptions(defaultOptions)
        } catch (e: MalformedURLException) {
            //e.printStackTrace()
            Toast.makeText(this, "Something went wrong, try again!!", Toast.LENGTH_SHORT).show()
        }

        token()

        groupVideoCallingActivity.startBtn.setOnClickListener {
            if (groupVideoCallingActivity.codeBox.text.trim().toString().isNotEmpty()) {
                if (groupVideoCallingActivity.codeBox.text.trim().toString() == tokenCode) {
                    startGroupVideoCall()
                } else {
                    Toast.makeText(this, "Invalid code!!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter the code!!", Toast.LENGTH_SHORT).show()
            }
        }

        groupVideoCallingActivity.joinBtn.setOnClickListener {
            if (groupVideoCallingActivity.codeBox.text.trim().toString().isNotEmpty()) {
                if (groupVideoCallingActivity.codeBox.text.trim().toString().length == 19
                    && groupVideoCallingActivity.codeBox.text.trim().toString()
                        .startsWith("Clique+91")
                    && groupVideoCallingActivity.codeBox.text.trim().toString() != tokenCode
                ) {
                    startGroupVideoCall()
                } else {
                    Toast.makeText(this, "Invalid code!!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter the code!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun token() {
        database.collection("users").document(auth.uid!!).get().addOnSuccessListener {
            if (it.exists()) {
                if (auth.uid == it.get("uid")) {
                    appName = "Clique"
                    phoneNumber = it.getString("phoneNumber").toString()
                    tokenCode = appName + phoneNumber
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Something went wrong, Please try again!!", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun startGroupVideoCall() {
        val options: JitsiMeetConferenceOptions = JitsiMeetConferenceOptions.Builder()
            .setRoom(groupVideoCallingActivity.codeBox.text.toString())
            .setWelcomePageEnabled(false)
            .build()

        JitsiMeetActivity.launch(this, options)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    override fun onBackPressed() {
        finish()
        startActivity(
            Intent(this, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }
}