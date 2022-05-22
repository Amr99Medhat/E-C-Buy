package com.amrmedhatandroid.e_cbuy.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import com.amrmedhatandroid.e_cbuy.R
import com.amrmedhatandroid.e_cbuy.database.PreferenceManager
import com.amrmedhatandroid.e_cbuy.databinding.ActivityLoginBinding
import com.amrmedhatandroid.e_cbuy.firebase.FireStoreClass
import com.amrmedhatandroid.e_cbuy.firebase.FirebaseAuthClass
import com.amrmedhatandroid.e_cbuy.models.User
import com.amrmedhatandroid.e_cbuy.utils.Constants

class LoginActivity : BaseActivity() {
    lateinit var mActivityLoginBinding: ActivityLoginBinding
    lateinit var mPreferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mActivityLoginBinding.root)
        mPreferenceManager = PreferenceManager(this@LoginActivity)
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
        mActivityLoginBinding.btnLogin.setOnClickListener {
            logInRegisteredUser()
        }
        mActivityLoginBinding.tvForgotPassword.setOnClickListener {
            // Launch the forgot password screen when the user clicks on the text.
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
        mActivityLoginBinding.tvRegister.setOnClickListener {
            // Launch the register screen when the user clicks on the text.
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    /**
     * A function to validate the entries of a new user.
     */
    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(
                mActivityLoginBinding.edEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(
                mActivityLoginBinding.edPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }
        }
    }

    /**
     * A function to log-in the user through his email and password.
     */
    private fun logInRegisteredUser() {
        if (validateLoginDetails()) {

            //Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Get the text from editText and trim the space.
            val email = mActivityLoginBinding.edEmail.text.toString().trim { it <= ' ' }
            val password = mActivityLoginBinding.edPassword.text.toString().trim { it <= ' ' }

            // Log-In using FirebaseAuth
            FirebaseAuthClass().logInRegisteredUser(this@LoginActivity, email, password)
        }
    }

    fun successLogInRegisteredUser() {
        FireStoreClass().getUserDetails(this@LoginActivity)
    }

    fun failedLogInRegisteredUser(error: String) {
        // Hide the progress dialog.
        hideProgressDialog()
        showErrorSnackBar(error, true)
    }

    fun userLoggedInSuccessfully(user: User) {
        // Hide the progress dialog
        hideProgressDialog()



        saveBasicUserData(user)

        if (user.profileCompleted == 0) {
            // If the user profile is incomplete then the UserProfileActivity
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS,user)
            startActivity(intent)
        } else {
            // Redirect the user to MainActivity after log in.
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
        }
        finish()
    }

    fun userLoggedInFailed(error: String) {
        // Hide the progress dialog
        hideProgressDialog()
        showErrorSnackBar(error, true)
    }

    /**
     * A function to save the basic data about the user in shared prefs.
     */
    private fun saveBasicUserData(user: User) {
        mPreferenceManager.putString(
            Constants.LOGGED_IN_USERNAME,
            "${user.firstName} ${user.lastName}"
        )
    }
}