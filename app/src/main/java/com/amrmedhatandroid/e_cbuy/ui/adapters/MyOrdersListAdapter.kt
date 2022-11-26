package com.amrmedhatandroid.e_cbuy.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amrmedhatandroid.e_cbuy.databinding.ItemListLayoutBinding
import com.amrmedhatandroid.e_cbuy.models.Order
import com.amrmedhatandroid.e_cbuy.models.SoldProduct
import com.amrmedhatandroid.e_cbuy.ui.activities.MyOrderDetailsActivity
import com.amrmedhatandroid.e_cbuy.utils.Constants
import com.amrmedhatandroid.e_cbuy.utils.GlideLoader

open class MyOrdersListAdapter(
    private val context: Context,
    private val mOrders: ArrayList<Order>
) : RecyclerView.Adapter<MyOrdersListAdapter.MyOrdersViewHolder>() {

    inner class MyOrdersViewHolder(var itemContainerPatientBinding: ItemListLayoutBinding) :
        RecyclerView.ViewHolder(itemContainerPatientBinding.root) {
        fun setLabData(order: Order) {
            GlideLoader(context).loadProductImage(
                order.image,
                itemContainerPatientBinding.ivCartItemImage
            )
            itemContainerPatientBinding.tvCartItemTitle.text = order.title
            itemContainerPatientBinding.tvCartItemPrice.text = "$${order.total_amount}"
            itemContainerPatientBinding.ibDeleteCartItem.visibility = View.GONE
//            itemContainerPatientBinding.ibDeleteCartItem.setOnClickListener {
//                fragment.deleteProduct(product.product_id)
//            }
            itemContainerPatientBinding.root.setOnClickListener {
                val intent = Intent(context, MyOrderDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_MY_ORDER_DETAILS, order)
                context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrdersViewHolder {
        val binding =
            ItemListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyOrdersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyOrdersViewHolder, position: Int) {
        holder.setLabData(mOrders[position])

    }

    override fun getItemCount(): Int {
        return mOrders.size
    }
}