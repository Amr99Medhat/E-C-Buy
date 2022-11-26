package com.amrmedhatandroid.e_cbuy.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amrmedhatandroid.e_cbuy.databinding.ItemListLayoutBinding
import com.amrmedhatandroid.e_cbuy.models.SoldProduct
import com.amrmedhatandroid.e_cbuy.ui.activities.MyOrderDetailsActivity
import com.amrmedhatandroid.e_cbuy.ui.activities.SoldProductDetailsActivity
import com.amrmedhatandroid.e_cbuy.utils.Constants
import com.amrmedhatandroid.e_cbuy.utils.GlideLoader

class SoldProductsListAdapter(
    private val context: Context,
    private val list: ArrayList<SoldProduct>
) : RecyclerView.Adapter<SoldProductsListAdapter.MySoldProductsViewHolder>() {

    inner class MySoldProductsViewHolder(var itemContainerPatientBinding: ItemListLayoutBinding) :
        RecyclerView.ViewHolder(itemContainerPatientBinding.root) {
        fun setLabData(soldProduct: SoldProduct) {
            GlideLoader(context).loadProductImage(
                soldProduct.image,
                itemContainerPatientBinding.ivCartItemImage
            )
            itemContainerPatientBinding.tvCartItemTitle.text = soldProduct.title
            itemContainerPatientBinding.tvCartItemPrice.text = "$${soldProduct.price}"
            itemContainerPatientBinding.ibDeleteCartItem.visibility = View.GONE
//            itemContainerPatientBinding.ibDeleteCartItem.setOnClickListener {
//                fragment.deleteProduct(product.product_id)
//            }
            itemContainerPatientBinding.root.setOnClickListener {
                val intent = Intent(context, SoldProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS, soldProduct)
                context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SoldProductsListAdapter.MySoldProductsViewHolder {
        val binding =
            ItemListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MySoldProductsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SoldProductsListAdapter.MySoldProductsViewHolder,
        position: Int
    ) {
        holder.setLabData(list[position])

    }

    override fun getItemCount(): Int {
        return list.size
    }
}