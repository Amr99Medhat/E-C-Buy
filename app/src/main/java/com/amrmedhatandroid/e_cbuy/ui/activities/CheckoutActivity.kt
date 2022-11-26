package com.amrmedhatandroid.e_cbuy.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.amrmedhatandroid.e_cbuy.R
import com.amrmedhatandroid.e_cbuy.databinding.ActivityCheckoutBinding
import com.amrmedhatandroid.e_cbuy.firebase.FireStoreClass
import com.amrmedhatandroid.e_cbuy.firebase.FirebaseAuthClass
import com.amrmedhatandroid.e_cbuy.models.Address
import com.amrmedhatandroid.e_cbuy.models.CartItem
import com.amrmedhatandroid.e_cbuy.models.Order
import com.amrmedhatandroid.e_cbuy.models.Product
import com.amrmedhatandroid.e_cbuy.ui.adapters.CartItemsListAdapter
import com.amrmedhatandroid.e_cbuy.utils.Constants

class CheckoutActivity : BaseActivity() {

    private var mAddressDetails: Address? = null
    private lateinit var mProductList: ArrayList<Product>
    private lateinit var mCartItemsList: ArrayList<CartItem>
    private var mSubTotal: Double = 0.0
    private var mTotalAmount: Double = 0.0
    private lateinit var mOrderDetails: Order

    private lateinit var mActivityCheckoutBinding: ActivityCheckoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityCheckoutBinding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(mActivityCheckoutBinding.root)
        setListeners()

        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            mAddressDetails = intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)
        }

        if (mAddressDetails != null) {
            mActivityCheckoutBinding.tvCheckoutAddressType.text = mAddressDetails?.type
            mActivityCheckoutBinding.tvCheckoutFullName.text = mAddressDetails?.name
            mActivityCheckoutBinding.tvCheckoutAddress.text =
                "${mAddressDetails?.address}, ${mAddressDetails?.zipCode}"
            mActivityCheckoutBinding.tvCheckoutAdditionalNote.text = mAddressDetails?.additionalNote

            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                mActivityCheckoutBinding.tvCheckoutOtherDetails.text = mAddressDetails?.otherDetails
            }

            mActivityCheckoutBinding.tvCheckoutMobileNumber.text = mAddressDetails?.mobileNumber
        }

        getProductsList()
    }

    /**
     * A function to handle user clicks.
     */
    private fun setListeners() {
        mActivityCheckoutBinding.ivBack.setOnClickListener {
            // back to the previous screen.
            onBackPressed()
        }
        mActivityCheckoutBinding.btnPlaceOrder.setOnClickListener {
            placeAnOrder()
        }
    }

    private fun placeAnOrder() {
        showProgressDialog(resources.getString(R.string.please_wait))

        if (mAddressDetails != null) {
            mOrderDetails = Order(
                FirebaseAuthClass().getCurrentUserID(),
                mCartItemsList,
                mAddressDetails!!,
                "My order ${System.currentTimeMillis()}",
                mCartItemsList[0].image,
                mSubTotal.toString(),
                "10.0",
                mTotalAmount.toString(),
                System.currentTimeMillis()
            )

            FireStoreClass().placeAnOrder(this@CheckoutActivity, mOrderDetails)
        }

    }

    fun allDetailsUpdatedSuccessfully() {
        hideProgressDialog()
        Toast.makeText(
            this@CheckoutActivity,
            "Your order was placed successfully",
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(this@CheckoutActivity, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun orderPlacedSuccess() {
        FireStoreClass().updateAllDetails(this@CheckoutActivity, mCartItemsList, mOrderDetails)
    }

    private fun getProductsList() {
        showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().getAllProductsList(this@CheckoutActivity)
    }

    fun successProductsListFromFireStore(productList: ArrayList<Product>) {
        mProductList = productList
        getCartItemsList()
    }

    private fun getCartItemsList() {
        FireStoreClass().getCartList(this@CheckoutActivity)
    }

    fun successCartItemsList(cartList: ArrayList<CartItem>) {
        hideProgressDialog()


        for (product in mProductList) {
            for (cartItem in cartList) {
                if (product.product_id == cartItem.product_id) {
                    cartItem.stock_quantity = product.stock_quantity
                }
            }
        }
        mCartItemsList = cartList

        mActivityCheckoutBinding.rvCartListItems.layoutManager =
            LinearLayoutManager(this@CheckoutActivity)
        mActivityCheckoutBinding.rvCartListItems.setHasFixedSize(true)

        val cartListAdapter = CartItemsListAdapter(this@CheckoutActivity, mCartItemsList, false)
        mActivityCheckoutBinding.rvCartListItems.adapter = cartListAdapter

        for (item in mCartItemsList) {
            val availableQuantity = item.stock_quantity.toInt()

            if (availableQuantity > 0) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()
                mSubTotal += (price * quantity)
            }
        }

        mActivityCheckoutBinding.tvSubTotal.text = "$${mSubTotal}"
        mActivityCheckoutBinding.tvShippingCharge.text = "$10.0"

        if (mSubTotal > 0) {
            mActivityCheckoutBinding.llCheckoutPlaceOrder.visibility = View.VISIBLE

            mTotalAmount = mSubTotal + 10.0
            mActivityCheckoutBinding.tvTotalAmount.text = "$${mTotalAmount}"
        } else {
            mActivityCheckoutBinding.llCheckoutPlaceOrder.visibility = View.GONE
        }

    }

    fun failedGetAllProductsList() {
        hideProgressDialog()
    }

    fun failedCartItemsList() {
        hideProgressDialog()
    }
}