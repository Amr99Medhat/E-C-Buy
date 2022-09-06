package com.amrmedhatandroid.e_cbuy.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.amrmedhatandroid.e_cbuy.R
import com.amrmedhatandroid.e_cbuy.databinding.ActivityUserProfileBinding
import com.amrmedhatandroid.e_cbuy.firebase.FireStorageClass
import com.amrmedhatandroid.e_cbuy.firebase.FireStoreClass
import com.amrmedhatandroid.e_cbuy.models.User
import com.amrmedhatandroid.e_cbuy.utils.Constants
import com.amrmedhatandroid.e_cbuy.utils.GlideLoader
import java.io.FileNotFoundException

class UserProfileActivity : BaseActivity() {
    private lateinit var mActivityUserProfileBinding: ActivityUserProfileBinding
    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityUserProfileBinding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(mActivityUserProfileBinding.root)
        getUserDetails()
        setUserDetailsOnScreen()
        setListeners()

        // If the Intent came from Settings Activity.
        handleProfileEditScreen()
    }

    private fun getUserDetails() {
        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            // Get the user details from intent as parcelableExtra
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }
    }

    private fun setUserDetailsOnScreen() {
        mActivityUserProfileBinding.edFirstName.setText(mUserDetails.firstName)
        mActivityUserProfileBinding.edLastName.setText(mUserDetails.lastName)
        mActivityUserProfileBinding.edEmail.setText(mUserDetails.email)
        if (mUserDetails.profileCompleted == 0) {
            mActivityUserProfileBinding.tilMobileNumber.requestFocus()
        }
    }

    private fun setListeners() {
        mActivityUserProfileBinding.ivUserPhoto.setOnClickListener {
            checkPermissions()
        }
        mActivityUserProfileBinding.btnSubmit.setOnClickListener {

            if (validateUserProfileDetails()) {
                showProgressDialog(resources.getString(R.string.please_wait))

                if (mSelectedImageFileUri != null) {
                    FireStorageClass().uploadImageToCloudStorage(
                        this@UserProfileActivity,
                        mSelectedImageFileUri,Constants.USER_PROFILE_IMAGE
                    )
                } else {
                    updateUserProfileDetails()
                }
            }
        }
        mActivityUserProfileBinding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun checkPermissions() {
        // Here we will check if the permission is already allowed or we need to request for it.
        // First of all we will check the READ_EXTERNAL_STORAGE permission and if it is not allowed so request it.
        if (ContextCompat.checkSelfPermission(
                this@UserProfileActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            pickImage()
        } else {

            /* Request permissions to be granted to this application. Theses permissions must be requested
             in your manifest, they should not be granted to your app, and they should have protection level*/
            ActivityCompat.requestPermissions(
                this@UserProfileActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.READ_STORAGE_PERMISSION_CODE
            )

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImage()
        } else {
            Toast.makeText(
                this@UserProfileActivity,
                resources.getString(R.string.read_storage_permission_denied),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun pickImage() {
        Constants.pickImage(pickImage)
    }

    private fun validateUserProfileDetails(): Boolean {
        return when {
            TextUtils.isEmpty(
                mActivityUserProfileBinding.edMobileNumber.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_mobile_number), true)
                false
            }
            mActivityUserProfileBinding.edMobileNumber.text.toString()
                .trim { it <= ' ' }.length < 11 -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_mobile_number_less_than_11),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    /**
    If the Intent came from Settings Activity.
     */
    private fun handleProfileEditScreen() {
        if (mUserDetails.profileCompleted != 0) {
            // Edit the Screen info
            mActivityUserProfileBinding.tvTitle.text = resources.getString(R.string.edit_profile)
            mActivityUserProfileBinding.ivBack.visibility = View.VISIBLE

            // Add the Missing Data about the User.
            GlideLoader(this@UserProfileActivity).loadUserImage(
                mUserDetails.image,
                mActivityUserProfileBinding.ivUserPhoto
            )
            if (mUserDetails.mobile != 0L) {
                mActivityUserProfileBinding.edMobileNumber.setText(mUserDetails.mobile.toString())
            }
            if (mUserDetails.gender == Constants.MALE) {
                mActivityUserProfileBinding.rbMale.isChecked = true
            } else {
                mActivityUserProfileBinding.rbFemale.isChecked = true
            }

            // Enable access and edit the EditTexts.
            mActivityUserProfileBinding.edFirstName.isEnabled = true
            mActivityUserProfileBinding.edLastName.isEnabled = true
            mActivityUserProfileBinding.tilFirstName.background = null
            mActivityUserProfileBinding.tilLastName.background = null

        }
    }

    private fun updateUserProfileDetails() {
        val userHashMap = HashMap<String, Any>()

        val firstName = mActivityUserProfileBinding.edFirstName.text.toString().trim { it <= ' ' }
        if (firstName != mUserDetails.firstName) {
            userHashMap[Constants.FIRST_NAME] = firstName
        }
        val lastName = mActivityUserProfileBinding.edLastName.text.toString().trim { it <= ' ' }
        if (firstName != mUserDetails.lastName) {
            userHashMap[Constants.LAST_NAME] = lastName
        }
        val mobileNumber =
            mActivityUserProfileBinding.edMobileNumber.text.toString()
                .trim { it <= ' ' }

        val gender = if (mActivityUserProfileBinding.rbMale.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }

        if (mUserProfileImageURL.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }

        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        if (gender.isNotEmpty() && gender != mUserDetails.gender) {
            userHashMap[Constants.GENDER] = gender
        }
        // userHashMap[Constants.GENDER] = gender

        userHashMap[Constants.COMPLETE_PROFILE] = 1

        //showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().updateUserProfileData(this@UserProfileActivity, userHashMap)
    }

    fun updateUserProfileDataSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@UserProfileActivity,
            resources.getString(R.string.msg_profile_updated_success),
            Toast.LENGTH_SHORT
        ).show()

        startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
        finish()
    }

    fun updateUserProfileDataFailed(error: String) {
        hideProgressDialog()
        showErrorSnackBar(error, true)
    }

    fun imageUploadSuccess(imageURL: String) {
        //hideProgressDialog()

        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }

    private val pickImage: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if (result.data != null) {
                    mSelectedImageFileUri = result.data!!.data
                    try {
                        GlideLoader(this@UserProfileActivity).loadUserImage(
                            mSelectedImageFileUri!!,
                            mActivityUserProfileBinding.ivUserPhoto
                        )

                    } catch (e: FileNotFoundException) {
                        showErrorSnackBar(
                            resources.getString(R.string.image_selection_failed),
                            true
                        )
                    }
                }
            } else if (result.resultCode == RESULT_CANCELED) {
                // A log printed when user close or cancel the image selection.
                Log.d("Request Cancelled", "Image Selection Cancelled")
            }
        }
}