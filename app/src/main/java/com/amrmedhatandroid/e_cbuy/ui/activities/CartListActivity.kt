package com.amrmedhatandroid.e_cbuy.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.amrmedhatandroid.e_cbuy.R
import com.amrmedhatandroid.e_cbuy.databinding.ActivityCartListBinding
import com.amrmedhatandroid.e_cbuy.firebase.FireStoreClass
import com.amrmedhatandroid.e_cbuy.models.CartItem
import com.amrmedhatandroid.e_cbuy.models.Product
import com.amrmedhatandroid.e_cbuy.ui.adapters.CartItemsListAdapter
import com.amrmedhatandroid.e_cbuy.utils.Constants

class CartListActivity : BaseActivity() {
    private lateinit var mActivityCartListBinding: ActivityCartListBinding
    private lateinit var mProductsList: ArrayList<Product>
    private lateinit var mCartListItems: ArrayList<CartItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityCartListBinding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(mActivityCartListBinding.root)
        setListeners()
    }

    /**
     * A function to handle user clicks.
     */
    private fun setListeners() {
        mActivityCartListBinding.ivBack.setOnClickListener {
            // back to the previous screen.
            onBackPressed()
        }
        mActivityCartListBinding.btnCheckout.setOnClickListener {
            val intent = Intent(this@CartListActivity, AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, true)
            startActivity(intent)
        }
    }

    private fun getCartItemsList() {
        // showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().getCartList(this@CartListActivity)
    }

    fun itemUpdateSuccess() {
        hideProgressDialog()
        getCartItemsList()
    }

    fun successCartItemsList(cartList: ArrayList<CartItem>) {
        hideProgressDialog()

        for (product in mProductsList) {
            for (cartItem in cartList) {
                if (product.product_id == cartItem.product_id)
                    cartItem.stock_quantity = product.stock_quantity

                if (product.stock_quantity.toInt() == 0) {
                    cartItem.cart_quantity = product.stock_quantity
                }
            }
        }

        mCartListItems = cartList

        if (mCartListItems.size > 0) {
            mActivityCartListBinding.rvCartItemsList.visibility = View.VISIBLE
            mActivityCartListBinding.llCheckout.visibility = View.VISIBLE
            mActivityCartListBinding.tvNoCartItemFound.visibility = View.GONE


            mActivityCartListBinding.rvCartItemsList.layoutManager =
                LinearLayoutManager(this@CartListActivity)
            mActivityCartListBinding.rvCartItemsList.setHasFixedSize(true)
            val cartListAdapter = CartItemsListAdapter(this@CartListActivity, mCartListItems, true)
            mActivityCartListBinding.rvCartItemsList.adapter = cartListAdapter
            var subTotal = 0.0

            for (item in mCartListItems) {
                val availabilityQuantity = item.stock_quantity.toInt()
                if (availabilityQuantity > 0) {
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()
                    subTotal += (price * quantity)
                }
            }
            mActivityCartListBinding.tvSubTotal.text = "$${subTotal}"
            mActivityCartListBinding.tvShippingCharge.text =
                "$10.0" // TODO change shipping charge logic

            if (subTotal > 0) {
                mActivityCartListBinding.llCheckout.visibility = View.VISIBLE

                val total = subTotal + 10 // TODO change logic here
                mActivityCartListBinding.tvTotalAmount.text = "$${total}"
            } else {
                mActivityCartListBinding.llCheckout.visibility = View.GONE

            }
        } else {
            mActivityCartListBinding.rvCartItemsList.visibility = View.GONE
            mActivityCartListBinding.llCheckout.visibility = View.GONE
            mActivityCartListBinding.tvNoCartItemFound.visibility = View.VISIBLE
        }
    }

    fun failedCartItemsList() {
        hideProgressDialog()
    }

    private fun getProductList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getAllProductsList(this@CartListActivity)
    }

    fun successProductsListFromFireStore(productList: ArrayList<Product>) {
        hideProgressDialog()
        mProductsList = productList

        getCartItemsList()
    }

    fun failedGetAllProductsList() {
        hideProgressDialog()
    }

    fun itemRemovedSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this@CartListActivity,
            resources.getString(R.string.msg_item_removed_successfully),
            Toast.LENGTH_SHORT
        ).show()

        getCartItemsList()
    }

    fun failedRemoveItemFromCart() {
        hideProgressDialog()
    }

    fun failedUpdateMyCart() {
        hideProgressDialog()
    }

    override fun onResume() {
        super.onResume()
        //getCartItemsList()
        getProductList()
    }
}