package com.amrmedhatandroid.e_cbuy.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher

object Constants {

    const val USERS: String = "users"
    const val EC_Buy_PREFERENCES: String = "EC_Buy_Prefs"
    const val LOGGED_IN_USERNAME: String = "logged_in-username"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE: Int = 2
    const val MALE: String = "male"
    const val FEMALE: String = "female"
    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"
    const val IMAGE: String = "image"
    const val COMPLETE_PROFILE: String = "profileCompleted"
    const val USER_PROFILE_IMAGE: String = "user_profile_image"

    fun pickImage(pickImage: ActivityResultLauncher<Intent>) {
        // An intent for launching the image selection of phone storage.
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        pickImage.launch(intent)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        /*MineTypeMap : Two-way map that maps MIME-types to file extensions and vice verse.

        getSingleton() : Get the singleton instance of MineTypeMap.

        getExtensionFromMineType : Return the registered extension for the given MIME type.

        contentResolver.getType : Return the MIME type of the given content URL.*/

        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))

    }

}