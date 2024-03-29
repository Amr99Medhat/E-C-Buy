package com.amrmedhatandroid.e_cbuy.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amrmedhatandroid.e_cbuy.databinding.ItemDashboardLayoutBinding
import com.amrmedhatandroid.e_cbuy.models.Product
import com.amrmedhatandroid.e_cbuy.utils.GlideLoader

class DashboardItemsListAdapter(
    private val context: Context,
    private val mProducts: ArrayList<Product>
) : RecyclerView.Adapter<DashboardItemsListAdapter.AllProductsViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class AllProductsViewHolder(var itemDashboardLayoutBinding: ItemDashboardLayoutBinding) :
        RecyclerView.ViewHolder(itemDashboardLayoutBinding.root) {
        fun setLabData(product: Product) {
            GlideLoader(context).loadProductImage(
                product.image,
                itemDashboardLayoutBinding.ivDashboardItemImage
            )
            itemDashboardLayoutBinding.tvDashboardItemTitle.text = product.title
            itemDashboardLayoutBinding.tvDashboardItemPrice.text = "$${product.price}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllProductsViewHolder {
        val binding =
            ItemDashboardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AllProductsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllProductsViewHolder, position: Int) {
        holder.setLabData(mProducts[position])
        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, mProducts[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return mProducts.size
    }

    interface OnClickListener {
        fun onClick(position: Int, product: Product)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }


}