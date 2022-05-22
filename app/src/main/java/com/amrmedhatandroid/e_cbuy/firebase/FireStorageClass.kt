package com.amrmedhatandroid.e_cbuy.firebase

import android.app.Activity
import android.net.Uri
import android.util.Log
import com.amrmedhatandroid.e_cbuy.ui.activities.UserProfileActivity
import com.amrmedhatandroid.e_cbuy.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FireStorageClass {

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?) {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "." + Constants.getFileExtension(
                activity,
                imageFileURI
            )
        )

        sRef.putFile(imageFileURI!!).addOnSuccessListener { taskSnapshot ->
            // The image upload is success
            Log.e("Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

            // Get the downloadable url from the task snapshot
            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.e("Downloadable Image URL", uri.toString())
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.hideProgressDialog()
                        }
                    }
                    Log.e(activity.javaClass.simpleName, exception.message!!, exception)
                }
        }
    }
}