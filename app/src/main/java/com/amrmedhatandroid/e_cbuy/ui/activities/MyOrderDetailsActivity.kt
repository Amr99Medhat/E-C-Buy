package com.amrmedhatandroid.e_cbuy.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.amrmedhatandroid.e_cbuy.R
import com.amrmedhatandroid.e_cbuy.databinding.ActivityMyOrderDetailsBinding
import com.amrmedhatandroid.e_cbuy.models.Order
import com.amrmedhatandroid.e_cbuy.ui.adapters.CartItemsListAdapter
import com.amrmedhatandroid.e_cbuy.utils.Constants
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MyOrderDetailsActivity : AppCompatActivity() {
    private lateinit var mActivityMyOrderDetailsBinding: ActivityMyOrderDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityMyOrderDetailsBinding = ActivityMyOrderDetailsBinding.inflate(layoutInflater)
        setContentView(mActivityMyOrderDetailsBinding.root)
        setListeners()

        var myOrderDetails = Order()
        if (intent.hasExtra(Constants.EXTRA_MY_ORDER_DETAILS)) {
            myOrderDetails = intent.getParcelableExtra(Constants.EXTRA_MY_ORDER_DETAILS)!!
        }

        setupUI(myOrderDetails)

    }

    /**
     * A function to handle user clicks.
     */
    private fun setListeners() {
        mActivityMyOrderDetailsBinding.ivBack.setOnClickListener {
            // back to the previous screen.
            onBackPressed()
        }
    }

    private fun setupUI(orderDetails: Order) {
        mActivityMyOrderDetailsBinding.tvOrderId.text = orderDetails.title

        val dateFormat = "dd MMMM yyyy HH:mm"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calender = Calendar.getInstance()
        calender.timeInMillis = orderDetails.order_datetime
        val orderDateTime = formatter.format(calender.time)
        mActivityMyOrderDetailsBinding.tvOrderDate.text = orderDateTime

        val diffInMilliSeconds: Long = System.currentTimeMillis() - orderDetails.order_datetime
        val diffInHours: Long = TimeUnit.MICROSECONDS.toHours(diffInMilliSeconds)

        when {
            diffInHours < 1 -> {
                mActivityMyOrderDetailsBinding.tvStatusPending.text =
                    resources.getString(R.string.order_status_pending)
                mActivityMyOrderDetailsBinding.tvStatusPending.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorAccent
                    )
                )
            }

            diffInHours < 2 -> {
                mActivityMyOrderDetailsBinding.tvStatusPending.text =
                    resources.getString(R.string.order_status_in_process)
                mActivityMyOrderDetailsBinding.tvStatusPending.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusInProcess
                    )
                )
            }

            else -> {
                mActivityMyOrderDetailsBinding.tvStatusPending.text =
                    resources.getString(R.string.order_status_delivered)
                mActivityMyOrderDetailsBinding.tvStatusPending.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusDelivered
                    )
                )
            }
        }

        mActivityMyOrderDetailsBinding.rvMyOrderListItems.layoutManager =
            LinearLayoutManager(this@MyOrderDetailsActivity)
        mActivityMyOrderDetailsBinding.rvMyOrderListItems.setHasFixedSize(true)

        val cartListAdapter =
            CartItemsListAdapter(this@MyOrderDetailsActivity, orderDetails.items, false)
        mActivityMyOrderDetailsBinding.rvMyOrderListItems.adapter = cartListAdapter

        mActivityMyOrderDetailsBinding.tvOrderDetailsAddressType.text = orderDetails.address.type
        mActivityMyOrderDetailsBinding.tvOrderDetailsFullName.text = orderDetails.address.name
        mActivityMyOrderDetailsBinding.tvOrderDetailsAddress.text =
            "${orderDetails.address.address}, ${orderDetails.address.zipCode}"
        mActivityMyOrderDetailsBinding.tvOrderDetailsAdditionalNote.text =
            orderDetails.address.additionalNote

        if (orderDetails.address.otherDetails.isNotEmpty()) {
            mActivityMyOrderDetailsBinding.tvOrderDetailsOtherDetails.visibility = View.VISIBLE
            mActivityMyOrderDetailsBinding.tvOrderDetailsOtherDetails.text =
                orderDetails.address.otherDetails
        } else {
            mActivityMyOrderDetailsBinding.tvOrderDetailsOtherDetails.visibility = View.GONE
        }
        mActivityMyOrderDetailsBinding.tvOrderDetailsMobileNumber.text =
            orderDetails.address.mobileNumber

        mActivityMyOrderDetailsBinding.tvSubTotal.text = orderDetails.sub_total_amount
        mActivityMyOrderDetailsBinding.tvShippingCharge.text = orderDetails.shipping_charge
        mActivityMyOrderDetailsBinding.tvTotalAmount.text = orderDetails.total_amount
    }
}