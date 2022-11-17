package com.amrmedhatandroid.e_cbuy.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amrmedhatandroid.e_cbuy.R
import com.amrmedhatandroid.e_cbuy.databinding.ActivityAddressListBinding
import com.amrmedhatandroid.e_cbuy.firebase.FireStoreClass
import com.amrmedhatandroid.e_cbuy.models.Address
import com.amrmedhatandroid.e_cbuy.ui.adapters.AddressListAdapter
import com.amrmedhatandroid.e_cbuy.utils.Constants
import com.amrmedhatandroid.e_cbuy.utils.SwipeToDeleteCallback
import com.amrmedhatandroid.e_cbuy.utils.SwipeToEditCallback

class AddressListActivity : BaseActivity() {
    private lateinit var mActivityAddressListBinding: ActivityAddressListBinding
    private var mSelectAddress: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityAddressListBinding = ActivityAddressListBinding.inflate(layoutInflater)
        setContentView(mActivityAddressListBinding.root)
        setListeners()
        getAddressList()

        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)) {
            mSelectAddress = intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS, false)
        }
        if (mSelectAddress) {
            mActivityAddressListBinding.tvTitle.text =
                resources.getString(R.string.title_select_address)
        }


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
            val intent = Intent(this@AddressListActivity, AddEditAddressActivity::class.java)
            startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
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

            val addressAdapter =
                AddressListAdapter(this@AddressListActivity, addressList, mSelectAddress)
            mActivityAddressListBinding.rvAddressList.adapter = addressAdapter

            if (!mSelectAddress) {
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

                val deleteSwipeHandler = object : SwipeToDeleteCallback(this@AddressListActivity) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        //Show the progress dialog.
                        showProgressDialog(resources.getString(R.string.please_wait))


                        FireStoreClass().deleteAddress(
                            this@AddressListActivity,
                            addressList[viewHolder.adapterPosition].id
                        )
                        addressList.clear()

                    }
                }

                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(mActivityAddressListBinding.rvAddressList)
            }


        } else {
            mActivityAddressListBinding.rvAddressList.visibility = View.GONE
            mActivityAddressListBinding.tvNoAddressFound.visibility = View.VISIBLE
        }
    }

    fun deleteAddressSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this@AddressListActivity,
            resources.getString(R.string.err_your_address_deleted_successfully),
            Toast.LENGTH_SHORT
        ).show()

        getAddressList()
    }

    fun failedDeleteAddress() {
        hideProgressDialog()
    }

    fun failedGetAddressesList() {
        hideProgressDialog()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            getAddressList()
        }
    }

}