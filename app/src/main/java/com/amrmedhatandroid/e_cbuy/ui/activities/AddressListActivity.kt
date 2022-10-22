package com.amrmedhatandroid.e_cbuy.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amrmedhatandroid.e_cbuy.R
import com.amrmedhatandroid.e_cbuy.databinding.ActivityAddressListBinding
import com.amrmedhatandroid.e_cbuy.firebase.FireStoreClass
import com.amrmedhatandroid.e_cbuy.models.Address
import com.amrmedhatandroid.e_cbuy.ui.adapters.AddressListAdapter
import com.amrmedhatandroid.e_cbuy.utils.SwipeToEditCallback

class AddressListActivity : BaseActivity() {
    private lateinit var mActivityAddressListBinding: ActivityAddressListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityAddressListBinding = ActivityAddressListBinding.inflate(layoutInflater)
        setContentView(mActivityAddressListBinding.root)
        setListeners()
    }

    /**
     * A function to handle user clicks.
     */
    private fun setListeners() {
        mActivityAddressListBinding.ivBack.setOnClickListener {
            // back to the previous screen.
            onBackPressed()
        }
        mActivityAddressListBinding.tvAddAddress.setOnClickListener {
            startActivity(Intent(this@AddressListActivity, AddEditAddressActivity::class.java))
        }
    }

    private fun getAddressList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getAddressesList(this@AddressListActivity)
    }

    fun successAddressListFormFireStore(addressList: ArrayList<Address>) {
        hideProgressDialog()

        if (addressList.size > 0) {
            mActivityAddressListBinding.rvAddressList.visibility = View.VISIBLE
            mActivityAddressListBinding.tvNoAddressFound.visibility = View.GONE
            mActivityAddressListBinding.rvAddressList.layoutManager =
                LinearLayoutManager(this@AddressListActivity)
            mActivityAddressListBinding.rvAddressList.setHasFixedSize(true)

            val addressAdapter = AddressListAdapter(this@AddressListActivity, addressList)
            mActivityAddressListBinding.rvAddressList.adapter = addressAdapter

            val editSwipeHandler = object : SwipeToEditCallback(this@AddressListActivity) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapter =
                        mActivityAddressListBinding.rvAddressList.adapter as AddressListAdapter
                    adapter.notifyEditItem(
                        this@AddressListActivity,
                        viewHolder.adapterPosition
                    )
                }
            }

            val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
            editItemTouchHelper.attachToRecyclerView(mActivityAddressListBinding.rvAddressList)
        } else {
            mActivityAddressListBinding.rvAddressList.visibility = View.GONE
            mActivityAddressListBinding.tvNoAddressFound.visibility = View.VISIBLE
        }
    }

    fun failedUpdateMyCart() {
        hideProgressDialog()
    }

    override fun onResume() {
        super.onResume()
        getAddressList()
    }
}