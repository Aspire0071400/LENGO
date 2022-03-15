package com.uniix.lengo_chatapp.interaction

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.uniix.lengo_chatapp.MainActivity
import com.uniix.lengo_chatapp.R
import com.uniix.lengo_chatapp.R.drawable.defaulavatar
import com.uniix.lengo_chatapp.databinding.ActivityOthersProfileBinding

const val ID = "ID"

class OthersProfileActivity : AppCompatActivity() {

    // Initializing Variables
    private lateinit var othersProfileActivity: ActivityOthersProfileBinding
    private lateinit var friendId: String
    private val database = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        othersProfileActivity = ActivityOthersProfileBinding.inflate(layoutInflater)
        setContentView(othersProfileActivity.root)

        setSupportActionBar(othersProfileActivity.toolbarOtherProfile)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(true)
        }

        friendId = intent.getStringExtra(ID).toString()
        viewData()

    }

    private fun viewData() {
        database.collection("users").document(friendId).get().addOnSuccessListener {
            if (it.exists()) {
                if (friendId == it.get("uid")) {
                    othersProfileActivity.otherName.text = it.getString("name").toString()
                    othersProfileActivity.otherDob.text = "DOB: " + it.getString("dob").toString()
                    othersProfileActivity.otherStatus.editText?.setText(
                        it.getString("status").toString()
                    )
                    othersProfileActivity.otherStatusDetail.hint = "Your status"
                    othersProfileActivity.otherGenderDetails.text =
                        "Gender: " + it.getString("gender").toString()
                    val userImgUrl = it.getString("imageUrl").toString()
                    Picasso.get()
                        .load(userImgUrl)
                        .placeholder(defaulavatar)
                        .error(defaulavatar)
                        .into(othersProfileActivity.userImage)
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Something went wrong, Please try again!!", Toast.LENGTH_LONG)
                .show()
        }
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

    companion object {
        fun createOthersProfileActivity(context: Context, id: String): Intent {
            val intent = Intent(context, OthersProfileActivity::class.java)
            intent.putExtra(ID, id)
            return intent
        }
    }

}