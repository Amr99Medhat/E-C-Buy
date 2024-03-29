package com.amrmedhatandroid.e_cbuy.utils

import android.content.Context
import android.widget.ImageView
import com.amrmedhatandroid.e_cbuy.R
import com.bumptech.glide.Glide

class GlideLoader(val context: Context) {

    fun loadUserImage(image: Any, imageView: ImageView) {
        try {
            // Load the user image in the ImageView
            Glide
                .with(context)
                .load(image) // URI of the image
                .centerCrop() // Scale type of the image
                .placeholder(R.drawable.ic_user_placeholder) // A default place holder if image is failed load.
                .into(imageView) // The view in which the image will be loaded.
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun loadProductImage(image: Any, imageView: ImageView) {
        try {
            // Load the user image in the ImageView
            Glide
                .with(context)
                .load(image) // URI of the image
                .centerCrop() // Scale type of the image
                .into(imageView) // The view in which the image will be loaded.
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}