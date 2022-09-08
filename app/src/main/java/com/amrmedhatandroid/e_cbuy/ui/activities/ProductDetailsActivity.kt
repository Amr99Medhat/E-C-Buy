package com.amrmedhatandroid.e_cbuy.ui.activities

import android.os.Bundle
import android.util.Log
import com.amrmedhatandroid.e_cbuy.R
import com.amrmedhatandroid.e_cbuy.databinding.ActivityProductDetailsBinding
import com.amrmedhatandroid.e_cbuy.firebase.FireStoreClass
import com.amrmedhatandroid.e_cbuy.models.Product
import com.amrmedhatandroid.e_cbuy.utils.Constants
import com.amrmedhatandroid.e_cbuy.utils.GlideLoader

class ProductDetailsActivity : BaseActivity() {
    private lateinit var mActivityProductDetailsBinding: ActivityProductDetailsBinding
    private var mProductId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityProductDetailsBinding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(mActivityProductDetailsBinding.root)
        setListeners()

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            Log.d("Amr", mProductId)
        }

        getProductDetails()
    }

    /**
     * A function to handle user clicks.
     */
    private fun setListeners() {
        mActivityProductDetailsBinding.ivBack.setOnClickListener {
            // back to the previous screen.
            onBackPressed()
        }
    }

    private fun getProductDetails() {
        //Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getProductDetails(this@ProductDetailsActivity, mProductId)
    }

    fun getProductDetailsSuccess(product: Product) {
        hideProgressDialog()
        GlideLoader(this@ProductDetailsActivity).loadProductImage(
            product.image,
            mActivityProductDetailsBinding.ivProductDetailsImage
        )

        mActivityProductDetailsBinding.tvProductDetailsTitle.text = product.title
        mActivityProductDetailsBinding.tvProductDetailsPrice.text = "$${product.price}"
        mActivityProductDetailsBinding.tvProductDetailsDescription.text = product.description
        mActivityProductDetailsBinding.tvProductDetailsAvailableQuantity.text =
            product.stock_quantity
    }

    fun failedGetProductDetails() {
        hideProgressDialog()
    }
}