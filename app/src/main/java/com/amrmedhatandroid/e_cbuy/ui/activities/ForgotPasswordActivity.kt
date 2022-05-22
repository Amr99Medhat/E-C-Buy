package com.amrmedhatandroid.e_cbuy.ui.activities

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.amrmedhatandroid.e_cbuy.R
import com.amrmedhatandroid.e_cbuy.databinding.ActivityForgotPasswordBinding
import com.amrmedhatandroid.e_cbuy.firebase.FirebaseAuthClass

class ForgotPasswordActivity : BaseActivity() {
    lateinit var mActivityForgotPasswordBinding: ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityForgotPasswordBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(mActivityForgotPasswordBinding.root)
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
        mActivityForgotPasswordBinding.ivBack.setOnClickListener {
            // back to the previous screen.
            onBackPressed()
        }
        mActivityForgotPasswordBinding.btnSubmit.setOnClickListener {
            sendEmailToResetPassword()
        }
    }

    /**
     * A function to validate the entries of a new  user.
     */
    private fun validateResetPasswordDetails(): Boolean {
        return when {
            TextUtils.isEmpty(
                mActivityForgotPasswordBinding.edEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            else -> {
                true
            }
        }
    }

    /**
     * A function to send a e-mail to the user from firebase to reset the user's password.
     */
    private fun sendEmailToResetPassword() {
        if (validateResetPasswordDetails()) {

            //Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Get the text from editText and trim the space.
            val email = mActivityForgotPasswordBinding.edEmail.text.toString().trim { it <= ' ' }

            // Reset password using FirebaseAuth
            FirebaseAuthClass().sendEmailToResetPassword(this@ForgotPasswordActivity, email)
        }
    }

    /**
     * A function to handle if the result form firebase is correct and finish this activity.
     */
    fun successSendEmailToResetPassword() {
        // Hide the progress dialog.
        hideProgressDialog()
        Toast.makeText(
            this@ForgotPasswordActivity,
            resources.getString(R.string.email_sent_success),
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }

    /**
     * A function to handle if the result form firebase is not correct or there is an error.
     */
    fun failedSendEmailToResetPassword(error: String) {
        // Hide the progress dialog.
        hideProgressDialog()
        showErrorSnackBar(error, true)
    }

}