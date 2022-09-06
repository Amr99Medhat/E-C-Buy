package com.amrmedhatandroid.e_cbuy.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.amrmedhatandroid.e_cbuy.R
import com.amrmedhatandroid.e_cbuy.database.PreferenceManager
import com.amrmedhatandroid.e_cbuy.databinding.ActivityAddProductBinding
import com.amrmedhatandroid.e_cbuy.firebase.FireStorageClass
import com.amrmedhatandroid.e_cbuy.firebase.FireStoreClass
import com.amrmedhatandroid.e_cbuy.firebase.FirebaseAuthClass
import com.amrmedhatandroid.e_cbuy.models.Product
import com.amrmedhatandroid.e_cbuy.utils.Constants
import com.amrmedhatandroid.e_cbuy.utils.GlideLoader
import java.io.FileNotFoundException

class AddProductActivity : BaseActivity() {
    lateinit var mActivityAddProductBinding: ActivityAddProductBinding
    lateinit var mPreferenceManager: PreferenceManager
    private var mSelectedImageFileUri: Uri? = null
    private var mProductImageURL: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityAddProductBinding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(mActivityAddProductBinding.root)
        mPreferenceManager = PreferenceManager(this@AddProductActivity)
        setListeners()
    }


    /**
     * A function to handle user clicks.
     */
    private fun setListeners() {
        mActivityAddProductBinding.ivBack.setOnClickListener {
            // back to the previous screen.
            onBackPressed()
        }
        mActivityAddProductBinding.ivAddUpdateProduct.setOnClickListener {
            checkPermissions()
        }
        mActivityAddProductBinding.btnSubmit.setOnClickListener {
            if (validateProductDetails()) {
                uploadProductImage()
            }
        }
    }

    private fun checkPermissions() {
        // Here we will check if the permission is already allowed or we need to request for it.
        // First of all we will check the READ_EXTERNAL_STORAGE permission and if it is not allowed so request it.
        if (ContextCompat.checkSelfPermission(
                this@AddProductActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            pickImage()
        } else {

            /* Request permissions to be granted to this application. Theses permissions must be requested
             in your manifest, they should not be granted to your app, and they should have protection level*/
            ActivityCompat.requestPermissions(
                this@AddProductActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.READ_STORAGE_PERMISSION_CODE
            )

        }
    }

    private fun uploadProductImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStorageClass().uploadImageToCloudStorage(
            this@AddProductActivity,
            mSelectedImageFileUri,
            Constants.PRODUCT_IMAGE
        )
    }

    fun productUploadSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@AddProductActivity, resources.getString(R.string.product_uploaded_success_message),
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    fun productUploadFailed() {
        hideProgressDialog()
        showErrorSnackBar(
            resources.getString(R.string.err_msg_error_while_uploading_the_product_details),
            true
        )
    }

    fun imageUploadSuccess(imageURL: String) {
        //hideProgressDialog()
        //showErrorSnackBar("Successfully. Image: $imageURL", false)

        mProductImageURL = imageURL

        uploadProductDetails()
    }

    private fun uploadProductDetails() {
        val username = mPreferenceManager.getString(Constants.LOGGED_IN_USERNAME)

        val product = Product(
            FirebaseAuthClass().getCurrentUserID(),
            username!!,
            mActivityAddProductBinding.etProductTitle.text.toString().trim { it <= ' ' },
            mActivityAddProductBinding.etProductPrice.text.toString().trim { it <= ' ' },
            mActivityAddProductBinding.etProductDescription.text.toString().trim { it <= ' ' },
            mActivityAddProductBinding.etProductQuantity.text.toString().trim { it <= ' ' },
            mProductImageURL
        )

        FireStoreClass().uploadProductDetails(this@AddProductActivity, product)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImage()
        } else {
            Toast.makeText(
                this@AddProductActivity,
                resources.getString(R.string.read_storage_permission_denied),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun pickImage() {
        Constants.pickImage(pickImage)
    }

    private val pickImage: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if (result.data != null) {
                    mActivityAddProductBinding.ivAddUpdateProduct.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_edit
                        )
                    )
                    mSelectedImageFileUri = result.data!!.data
                    try {
                        GlideLoader(this@AddProductActivity).loadUserImage(
                            mSelectedImageFileUri!!,
                            mActivityAddProductBinding.ivProductImage
                        )

                    } catch (e: FileNotFoundException) {
                        showErrorSnackBar(
                            resources.getString(R.string.image_selection_failed),
                            true
                        )
                    }
                }
            } else if (result.resultCode == RESULT_CANCELED) {
                // A log printed when user close or cancel the image selection.
                Log.d("Request Cancelled", "Image Selection Cancelled")
            }
        }

    private fun validateProductDetails(): Boolean {
        return when {
            mSelectedImageFileUri == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_image), true)
                false
            }
            TextUtils.isEmpty(
                mActivityAddProductBinding.etProductTitle.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_title), true)
                false
            }
            TextUtils.isEmpty(
                mActivityAddProductBinding.etProductPrice.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_price), true)
                false
            }
            TextUtils.isEmpty(
                mActivityAddProductBinding.etProductDescription.text.toString()
                    .trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_select_product_description),
                    true
                )
                false
            }
            TextUtils.isEmpty(
                mActivityAddProductBinding.etProductQuantity.text.toString()
                    .trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_select_product_quantity),
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