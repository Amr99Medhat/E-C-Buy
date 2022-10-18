package com.amrmedhatandroid.e_cbuy.ui.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.amrmedhatandroid.e_cbuy.R
import com.amrmedhatandroid.e_cbuy.databinding.ActivityAddEditAddressBinding
import com.amrmedhatandroid.e_cbuy.firebase.FireStoreClass
import com.amrmedhatandroid.e_cbuy.firebase.FirebaseAuthClass
import com.amrmedhatandroid.e_cbuy.models.Address
import com.amrmedhatandroid.e_cbuy.utils.Constants

class AddEditAddressActivity : BaseActivity() {
    private lateinit var mActivityAddEditAddressBinding: ActivityAddEditAddressBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityAddEditAddressBinding = ActivityAddEditAddressBinding.inflate(layoutInflater)
        setContentView(mActivityAddEditAddressBinding.root)
        setListeners()

    }

    /**
     * A function to handle user clicks.
     */
    private fun setListeners() {
        mActivityAddEditAddressBinding.ivBack.setOnClickListener {
            // back to the previous screen.
            onBackPressed()
        }
        mActivityAddEditAddressBinding.btnSubmit.setOnClickListener {
            saveAddressToFireStore()
        }
        mActivityAddEditAddressBinding.rgType.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_other) {
                mActivityAddEditAddressBinding.tilOtherDetails.visibility = View.VISIBLE
            } else {
                mActivityAddEditAddressBinding.tilOtherDetails.visibility = View.GONE
            }
        }
    }

    private fun saveAddressToFireStore() {

        if (validateAddressDetails()) {

            //Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            val addressType: String = when {
                mActivityAddEditAddressBinding.rbHome.isChecked -> {
                    Constants.HOME
                }

                mActivityAddEditAddressBinding.rbOffice.isChecked -> {
                    Constants.OFFICE
                }
                else -> {
                    Constants.OTHER
                }
            }
            val addressModel = Address(
                FirebaseAuthClass().getCurrentUserID(),
                mActivityAddEditAddressBinding.etFullName.text.toString().trim { it <= ' ' },
                mActivityAddEditAddressBinding.etPhoneNumber.text.toString().trim { it <= ' ' },
                mActivityAddEditAddressBinding.etAddress.text.toString().trim { it <= ' ' },
                mActivityAddEditAddressBinding.etZipCode.text.toString().trim { it <= ' ' },
                mActivityAddEditAddressBinding.etAdditionalNote.text.toString().trim { it <= ' ' },
                addressType,
                mActivityAddEditAddressBinding.etOtherDetails.text.toString().trim { it <= ' ' }
            )
            FireStoreClass().addAddress(this, addressModel)
        }
    }

    fun addUpdateAddressSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this@AddEditAddressActivity,
            resources.getString(R.string.your_address_was_added_successfully),
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    fun failedAddAddress() {
        hideProgressDialog()
    }

    private fun validateAddressDetails(): Boolean {
        return when {
            TextUtils.isEmpty(
                mActivityAddEditAddressBinding.etFullName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_full_name),
                    true
                )
                false
            }
            TextUtils.isEmpty(
                mActivityAddEditAddressBinding.etPhoneNumber.text.toString()
                    .trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_phone_number),
                    true
                )
                false
            }
            TextUtils.isEmpty(
                mActivityAddEditAddressBinding.etAddress.text.toString()
                    .trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_address),
                    true
                )
                false
            }
            TextUtils.isEmpty(
                mActivityAddEditAddressBinding.etZipCode.text.toString()
                    .trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_zip_code),
                    true
                )
                false
            }
            mActivityAddEditAddressBinding.rbOther.isChecked && TextUtils.isEmpty(
                mActivityAddEditAddressBinding.etOtherDetails.text.toString()
                    .trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_other_details),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }
}