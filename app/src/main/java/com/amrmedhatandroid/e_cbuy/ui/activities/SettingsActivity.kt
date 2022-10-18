package com.amrmedhatandroid.e_cbuy.ui.activities

import android.content.Intent
import android.os.Bundle
import com.amrmedhatandroid.e_cbuy.R
import com.amrmedhatandroid.e_cbuy.databinding.ActivitySettingsBinding
import com.amrmedhatandroid.e_cbuy.firebase.FireStoreClass
import com.amrmedhatandroid.e_cbuy.firebase.FirebaseAuthClass
import com.amrmedhatandroid.e_cbuy.models.User
import com.amrmedhatandroid.e_cbuy.utils.Constants
import com.amrmedhatandroid.e_cbuy.utils.GlideLoader

class SettingsActivity : BaseActivity() {
    private lateinit var mActivitySettingsBinding: ActivitySettingsBinding
    private lateinit var mUserDetails: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivitySettingsBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(mActivitySettingsBinding.root)
        setListeners()
    }

    /**
     * A function to handle user clicks.
     */
    private fun setListeners() {
        mActivitySettingsBinding.ivBack.setOnClickListener {
            // back to the previous screen.
            onBackPressed()
        }
        mActivitySettingsBinding.btnLogout.setOnClickListener {
            //signOut.
            FirebaseAuthClass().signOut()

            //Go to LoginActivity.
            val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        mActivitySettingsBinding.tvEdit.setOnClickListener {
            val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
            startActivity(intent)
        }
        mActivitySettingsBinding.llAddress.setOnClickListener {
            startActivity(Intent(this@SettingsActivity, AddressListActivity::class.java))
        }
    }

    private fun getUserDetails() {

        //Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().getUserDetails(this@SettingsActivity)
    }

    fun userDetailsSuccess(user: User) {

        // Assign the user data into mUserDetails var.
        mUserDetails = user

        hideProgressDialog()

        // Set the User's Image.
        GlideLoader(this@SettingsActivity).loadUserImage(
            user.image,
            mActivitySettingsBinding.ivUserPhoto
        )
        mActivitySettingsBinding.tvName.text = "${user.firstName} ${user.lastName}"
        mActivitySettingsBinding.tvEmail.text = user.email
        mActivitySettingsBinding.tvGender.text = user.gender
        mActivitySettingsBinding.tvMobileNumber.text = "${user.mobile}"
    }

    fun userDetailsFailed(error: String) {
        // Hide the progress dialog
        hideProgressDialog()
        showErrorSnackBar(error, true)
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }
}