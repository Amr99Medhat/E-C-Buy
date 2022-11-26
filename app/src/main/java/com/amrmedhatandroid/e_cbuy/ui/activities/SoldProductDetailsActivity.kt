package com.amrmedhatandroid.e_cbuy.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.amrmedhatandroid.e_cbuy.databinding.ActivitySoldProductDetailsBinding
import com.amrmedhatandroid.e_cbuy.models.SoldProduct
import com.amrmedhatandroid.e_cbuy.utils.Constants
import com.amrmedhatandroid.e_cbuy.utils.GlideLoader
import java.text.SimpleDateFormat
import java.util.*

class SoldProductDetailsActivity : AppCompatActivity() {
    private lateinit var mActivitySoldProductDetailsBinding: ActivitySoldProductDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivitySoldProductDetailsBinding =
            ActivitySoldProductDetailsBinding.inflate(layoutInflater)
        setContentView(mActivitySoldProductDetailsBinding.root)
        setListeners()

        var productDetails = SoldProduct()

        if (intent.hasExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS)) {
            productDetails = intent.getParcelableExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS)!!
        }
        setupUI(productDetails)

    }

    /**
     * A function to handle user clicks.
     */
    private fun setListeners() {
        mActivitySoldProductDetailsBinding.ivBack.setOnClickListener {
            // back to the previous screen.
            onBackPressed()
        }
    }

    private fun setupUI(productDetails: SoldProduct) {
        mActivitySoldProductDetailsBinding.tvSoldProductId.text = productDetails.order_id

        val dateFormat = "dd MMMM yyyy HH:mm"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calender = Calendar.getInstance()
        calender.timeInMillis = productDetails.order_date
        mActivitySoldProductDetailsBinding.tvSoldProductDate.text = formatter.format(calender.time)

        GlideLoader(this@SoldProductDetailsActivity).loadProductImage(
            productDetails.image,
            mActivitySoldProductDetailsBinding.ivSoldProductItemImage
        )
        mActivitySoldProductDetailsBinding.tvSoldProductItemTitle.text = productDetails.title
        mActivitySoldProductDetailsBinding.tvSoldProductItemPrice.text = "$${productDetails.price}"
        mActivitySoldProductDetailsBinding.tvSoldProductQuantity.text = productDetails.sold_quantity

        mActivitySoldProductDetailsBinding.tvSoldProductAddressType.text =
            productDetails.address.type
        mActivitySoldProductDetailsBinding.tvSoldProductFullName.text = productDetails.address.name
        mActivitySoldProductDetailsBinding.tvSoldProductAddress.text =
            "${productDetails.address.address} ${productDetails.address.zipCode}"
        mActivitySoldProductDetailsBinding.tvSoldProductAdditionalNote.text =
            productDetails.address.additionalNote

        if (productDetails.address.otherDetails.isNotEmpty()) {
            mActivitySoldProductDetailsBinding.tvSoldProductOtherDetails.visibility = View.VISIBLE
            mActivitySoldProductDetailsBinding.tvSoldProductOtherDetails.text =
                productDetails.address.otherDetails
        } else {
            mActivitySoldProductDetailsBinding.tvSoldProductOtherDetails.visibility = View.GONE
        }

        mActivitySoldProductDetailsBinding.tvSoldProductMobileNumber.text =
            productDetails.address.mobileNumber

        mActivitySoldProductDetailsBinding.tvSubTotal.text = productDetails.sub_total_amount
        mActivitySoldProductDetailsBinding.tvShippingCharge.text = productDetails.shipping_charge
        mActivitySoldProductDetailsBinding.tvTotalAmount.text = productDetails.total_amount


    }
}