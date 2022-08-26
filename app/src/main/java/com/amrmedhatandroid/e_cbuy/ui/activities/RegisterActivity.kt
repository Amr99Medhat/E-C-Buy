package com.amrmedhatandroid.e_cbuy.ui.activities

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import com.amrmedhatandroid.e_cbuy.R
import com.amrmedhatandroid.e_cbuy.databinding.ActivityRegisterBinding
import com.amrmedhatandroid.e_cbuy.firebase.FireStoreClass
import com.amrmedhatandroid.e_cbuy.firebase.FirebaseAuthClass
import com.amrmedhatandroid.e_cbuy.models.User

class RegisterActivity : BaseActivity() {
    private lateinit var mActivityRegisterBinding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityRegisterBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(mActivityRegisterBinding.root)
        setView()
        setListeners()
    }

    /**
     * A function to remove the notification bar.
     */
    private fun setView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    /**
     * A function to handle user clicks.
     */
    private fun setListeners() {
        mActivityRegisterBinding.ivBack.setOnClickListener {
            // back to the previous screen.
            onBackPressed()
        }
        mActivityRegisterBinding.tvLogin.setOnClickListener {
            // back to the previous screen.
            onBackPressed()
        }
        mActivityRegisterBinding.btnRegister.setOnClickListener {
            registerUserAuth()
        }
    }

    /**
     * A function to validate the entries of a new  user.
     */
    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(
                mActivityRegisterBinding.edFirstName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }
            TextUtils.isEmpty(
                mActivityRegisterBinding.edLastName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }
            TextUtils.isEmpty(
                mActivityRegisterBinding.edEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(
                mActivityRegisterBinding.edPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            TextUtils.isEmpty(
                mActivityRegisterBinding.edConfirmPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_confirm_password),
                    true
                )
                false
            }
            TextUtils.isEmpty(
                mActivityRegisterBinding.edConfirmPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_confirm_password),
                    true
                )
                false
            }
            mActivityRegisterBinding.edPassword.text.toString()
                .trim { it <= ' ' } != mActivityRegisterBinding.edConfirmPassword.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),
                    true
                )
                false
            }
            !mActivityRegisterBinding.cbTermsAndConditions.isChecked -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_agree_terms_and_conditions),
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
     * A function to register a new user and sign out after that and finish the activity.
     */
    private fun registerUserAuth() {
        // Check with validate function if the entries are valid or not.
        if (validateRegisterDetails()) {

            showProgressDialog(resources.getString(R.string.please_wait))

            val email = mActivityRegisterBinding.edEmail.text.toString().trim { it <= ' ' }
            val password = mActivityRegisterBinding.edPassword.text.toString().trim { it <= ' ' }

            // Create an instance and create a register a user with email and password.
            FirebaseAuthClass().registerUser(this@RegisterActivity, email, password)
        }
    }

    /**
     * A function to handle if register auth was successful.
     */
    fun registerUserAuthSuccess(userId: String) {

        //create an object from user and pass the user data to Firestore.
        val user = User(
            userId,
            mActivityRegisterBinding.edFirstName.text.toString().trim { it <= ' ' },
            mActivityRegisterBinding.edLastName.text.toString().trim { it <= ' ' },
            mActivityRegisterBinding.edEmail.text.toString().trim { it <= ' ' }
        )

        FireStoreClass().registerUser(this@RegisterActivity, user)

        FirebaseAuthClass().signOut()
        finish()
    }

    /**
     * A function to handle if register auth was not successful.
     */
    fun registerUserAuthFailed(error: String) {
        hideProgressDialog()
        showErrorSnackBar(error, true)
    }

    /**
     * A function to handle if upload the user's data to fireStore was successful.
     */
    fun registerSuccess() {
        //hideProgressDialog()
        showErrorSnackBar(
            "You are registered successfully.",
            false
        )
    }

    /**
     * A function to handle if upload the user's data to fireStore was not successful.
     */
    fun registerFailed(error: String) {
        hideProgressDialog()
        showErrorSnackBar(
            error,
            true
        )
    }


}