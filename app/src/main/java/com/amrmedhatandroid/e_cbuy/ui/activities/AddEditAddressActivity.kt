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
    private var mAddressDetails: Address? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityAddEditAddressBinding = ActivityAddEditAddressBinding.inflate(layoutInflater)
        setContentView(mActivityAddEditAddressBinding.root)

        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)) {
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)
        }

        if (mAddressDetails != null) {
            if (mAddressDetails!!.id.isNotEmpty()) {
                mActivityAddEditAddressBinding.tvTitle.text =
                    resources.getString(R.string.title_edit_address)
                mActivityAddEditAddressBinding.btnSubmit.text =
                    resources.getString(R.string.btn_lbl_update)

                mActivityAddEditAddressBinding.etFullName.setText(mAddressDetails?.name)
                mActivityAddEditAddressBinding.etPhoneNumber.setText(mAddressDetails?.mobileNumber)
                mActivityAddEditAddressBinding.etAddress.setText(mAddressDetails?.address)
                mActivityAddEditAddressBinding.etZipCode.setText(mAddressDetails?.zipCode)
                mActivityAddEditAddressBinding.etAdditionalNote.setText(mAddressDetails?.additionalNote)


                when (mAddressDetails?.type) {
                    Constants.HOME -> {
                        mActivityAddEditAddressBinding.rbHome.isChecked = true
                    }
                    Constants.OFFICE -> {
                        mActivityAddEditAddressBinding.rbOffice.isChecked = true
                    }
                    else -> {
                        mActivityAddEditAddressBinding.rbOther.isChecked = true
                        mActivityAddEditAddressBinding.tilOtherDetails.visibility = View.VISIBLE
                        mActivityAddEditAddressBinding.etOtherDetails.setText(mAddressDetails?.otherDetails)
                    }
                }
            }
        }

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
            val addressModel = Address(FirebaseAuthClass().getCurrentUserID(),
                mActivityAddEditAddressBinding.etFullName.text.toString().trim { it <= ' ' },
                mActivityAddEditAddressBinding.etPhoneNumber.text.toString().trim { it <= ' ' },
                mActivityAddEditAddressBinding.etAddress.text.toString().trim { it <= ' ' },
                mActivityAddEditAddressBinding.etZipCode.text.toString().trim { it <= ' ' },
                mActivityAddEditAddressBinding.etAdditionalNote.text.toString().trim { it <= ' ' },
                addressType,
                mActivityAddEditAddressBinding.etOtherDetails.text.toString().trim { it <= ' ' })

            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
                FireStoreClass().updateAddress(
                    this@AddEditAddressActivity,
                    addressModel,
                    mAddressDetails!!.id
                )
            } else {
                FireStoreClass().addAddress(this, addressModel)
            }
        }
    }

    fun addUpdateAddressSuccess() {
        hideProgressDialog()

        val notifySuccessMessage: String =
            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
                resources.getString(R.string.msg_your_address_updated_successfully)
            } else {
                resources.getString(R.string.your_address_was_added_successfully)
            }

        Toast.makeText(
            this@AddEditAddressActivity,
            notifySuccessMessage,
            Toast.LENGTH_SHORT
        ).show()
        setResult(RESULT_OK)
        finish()
    }

    fun failedAddAddress() {
        hideProgressDialog()
    }

    private fun validateAddressDetails(): Boolean {
        return when {
            TextUtils.isEmpty(mActivityAddEditAddressBinding.etFullName.text.toString()
                .trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_full_name), true
                )
                false
            }
            TextUtils.isEmpty(mActivityAddEditAddressBinding.etPhoneNumber.text.toString()
                .trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_phone_number), true
                )
                false
            }
            TextUtils.isEmpty(mActivityAddEditAddressBinding.etAddress.text.toString()
                .trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_address), true
                )
                false
            }
            TextUtils.isEmpty(mActivityAddEditAddressBinding.etZipCode.text.toString()
                .trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_zip_code), true
                )
                false
            }
            mActivityAddEditAddressBinding.rbOther.isChecked && TextUtils.isEmpty(
                mActivityAddEditAddressBinding.etOtherDetails.text.toString()
                    .trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_other_details), true
                )
                false
            }
            else -> {
                true
            }
        }
    }
}