package com.amrmedhatandroid.e_cbuy.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.amrmedhatandroid.e_cbuy.R
import com.amrmedhatandroid.e_cbuy.databinding.ActivityProductDetailsBinding
import com.amrmedhatandroid.e_cbuy.firebase.FireStoreClass
import com.amrmedhatandroid.e_cbuy.firebase.FirebaseAuthClass
import com.amrmedhatandroid.e_cbuy.models.CartItem
import com.amrmedhatandroid.e_cbuy.models.Product
import com.amrmedhatandroid.e_cbuy.utils.Constants
import com.amrmedhatandroid.e_cbuy.utils.GlideLoader

class ProductDetailsActivity : BaseActivity() {
    private lateinit var mActivityProductDetailsBinding: ActivityProductDetailsBinding
    private var mProductId: String = ""
    private lateinit var mProductDetails: Product
    private var mProductOwnerId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityProductDetailsBinding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(mActivityProductDetailsBinding.root)
        setListeners()

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        }

        //var productOwnerId = ""
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            mProductOwnerId = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }

//        if (FirebaseAuthClass().getCurrentUserID() == productOwnerId) {
//            mActivityProductDetailsBinding.btnAddToCart.visibility = View.GONE
//            mActivityProductDetailsBinding.btnGoToCart.visibility = View.GONE
//
//        } else {
//            mActivityProductDetailsBinding.btnAddToCart.visibility = View.VISIBLE
//        }

        if (FirebaseAuthClass().getCurrentUserID() != mProductOwnerId) {
            mActivityProductDetailsBinding.btnAddToCart.visibility = View.VISIBLE

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
        mActivityProductDetailsBinding.btnAddToCart.setOnClickListener {
            addToCart()
        }
        mActivityProductDetailsBinding.btnGoToCart.setOnClickListener {
            startActivity(Intent(this@ProductDetailsActivity, CartListActivity::class.java))
        }
    }

    private fun getProductDetails() {
        //Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getProductDetails(this@ProductDetailsActivity, mProductId)
    }

    fun productExistInCart() {
        hideProgressDialog()
        mActivityProductDetailsBinding.btnAddToCart.visibility = View.GONE
        mActivityProductDetailsBinding.btnGoToCart.visibility = View.VISIBLE
        FireStoreClass().getProductDetails(this@ProductDetailsActivity, mProductId)
    }

    fun getProductDetailsSuccess(product: Product) {
        mProductDetails = product
        //hideProgressDialog()
        GlideLoader(this@ProductDetailsActivity).loadProductImage(
            product.image,
            mActivityProductDetailsBinding.ivProductDetailsImage
        )

        mActivityProductDetailsBinding.tvProductDetailsTitle.text = product.title
        mActivityProductDetailsBinding.tvProductDetailsPrice.text = "$${product.price}"
        mActivityProductDetailsBinding.tvProductDetailsDescription.text = product.description
        mActivityProductDetailsBinding.tvProductDetailsAvailableQuantity.text =
            product.stock_quantity

        if (product.stock_quantity.toInt() == 0) {
            hideProgressDialog()

            mActivityProductDetailsBinding.btnAddToCart.visibility = View.GONE
            mActivityProductDetailsBinding.tvProductDetailsAvailableQuantity.text =
                resources.getString(R.string.lbl_out_of_stock)
            mActivityProductDetailsBinding.tvProductDetailsAvailableQuantity.setTextColor(
                ContextCompat.getColor(this@ProductDetailsActivity, R.color.colorSnackBarError)
            )
        } else {
            if (FirebaseAuthClass().getCurrentUserID() == product.user_id) {
                hideProgressDialog()
            } else {
                FireStoreClass().checkIfItemExistInCart(this@ProductDetailsActivity, mProductId)
            }
        }
    }

    fun failedGetProductDetails() {
        hideProgressDialog()
    }

    fun failedCheckIfItemExistInCart() {
        hideProgressDialog()
    }

    fun addToCartSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@ProductDetailsActivity,
            resources.getString(R.string.success_message_item_added_to_cart),
            Toast.LENGTH_SHORT
        ).show()

        mActivityProductDetailsBinding.btnAddToCart.visibility = View.GONE
        mActivityProductDetailsBinding.btnGoToCart.visibility = View.VISIBLE
    }

    fun failedAddToCart() {
        hideProgressDialog()
    }

    private fun addToCart() {
        val cartItem = CartItem(
            FirebaseAuthClass().getCurrentUserID(),
            mProductOwnerId,
            mProductId,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            Constants.DEFAULT_CART_QUANTITY
        )

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addCartItems(this@ProductDetailsActivity, cartItem)
    }
}