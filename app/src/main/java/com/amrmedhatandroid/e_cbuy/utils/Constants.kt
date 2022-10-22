package com.amrmedhatandroid.e_cbuy.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher

object Constants {
    // Collections names in Cloud FireStore.
    const val USERS: String = "users"
    const val PRODUCTS: String = "products"
    const val CART_ITEMS: String = "cart_items"
    const val ADDRESSES: String = "addresses"

    const val EC_Buy_PREFERENCES: String = "EC_Buy_Prefs"
    const val LOGGED_IN_USERNAME: String = "logged_in-username"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE: Int = 2
    const val MALE: String = "male"
    const val FEMALE: String = "female"
    const val FIRST_NAME: String = "firstName"
    const val LAST_NAME: String = "lastName"
    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"
    const val IMAGE: String = "image"
    const val COMPLETE_PROFILE: String = "profileCompleted"
    const val USER_PROFILE_IMAGE: String = "user_profile_image"
    const val PRODUCT_IMAGE: String = "product_image"
    const val USER_ID: String = "user_id"
    const val EXTRA_PRODUCT_ID: String = "extra_product_id"
    const val EXTRA_PRODUCT_OWNER_ID: String = "extra_product_owner_id"
    const val DEFAULT_CART_QUANTITY: String = "1"
    const val PRODUCT_ID: String = "product_id"
    const val CART_QUANTITY: String = "cart_quantity"
    const val HOME: String = "Home"
    const val OFFICE: String = "Office"
    const val OTHER: String = "other"
    const val EXTRA_ADDRESS_DETAILS: String = "AddressDetails"


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