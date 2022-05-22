package com.amrmedhatandroid.e_cbuy.firebase

import android.app.Activity
import com.amrmedhatandroid.e_cbuy.ui.activities.ForgotPasswordActivity
import com.amrmedhatandroid.e_cbuy.ui.activities.LoginActivity
import com.amrmedhatandroid.e_cbuy.ui.activities.RegisterActivity
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthClass {

    private val mFirebaseAuth = FirebaseAuth.getInstance()

    fun sendEmailToResetPassword(activity: Activity, email: String) {

        // Reset password using FirebaseAuth
        mFirebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    when (activity) {
                        is ForgotPasswordActivity -> {
                            activity.successSendEmailToResetPassword()
                        }
                    }
                } else {
                    when (activity) {
                        is ForgotPasswordActivity -> {
                            activity.failedSendEmailToResetPassword(task.exception!!.message.toString())
                        }
                    }
                }
            }
    }


    fun logInRegisteredUser(activity: Activity, email: String, password: String) {

        // Log-In using FirebaseAuth
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    when (activity) {
                        is LoginActivity -> {
                            activity.successLogInRegisteredUser()
                        }
                    }
                } else {
                    when (activity) {
                        is LoginActivity -> {
                            activity.failedLogInRegisteredUser(task.exception!!.message.toString())
                        }
                    }
                }
            }
    }


    fun registerUser(activity: Activity, email: String, password: String) {

        // Create an instance and create a register a user with email and password.
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                // If the registration is successfully done.
                if (task.isSuccessful) {

                    when (activity) {
                        is RegisterActivity -> {
                            //Firebase registered user.
                            activity.registerUserAuthSuccess(task.result.user!!.uid)
                        }
                    }
                } else {
                    // If the registering is not successful then show error message.
                    when (activity) {
                        is RegisterActivity -> {
                            //Firebase registered user.
                            activity.registerUserAuthFailed(task.exception!!.message.toString())
                        }
                    }
                }
            }
    }

    fun getCurrentUserID(): String {
        // An Instance of current user using FirebaseAuth
        val currentUser = mFirebaseAuth.currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentID = ""
        if (currentUser != null) {
            currentID = currentUser.uid
        }

        return currentID

    }

    fun signOut() {
        mFirebaseAuth.signOut()
    }
}