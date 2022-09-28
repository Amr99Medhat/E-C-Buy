package com.amrmedhatandroid.e_cbuy.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amrmedhatandroid.e_cbuy.R
import com.amrmedhatandroid.e_cbuy.databinding.ItemListLayoutBinding
import com.amrmedhatandroid.e_cbuy.models.Product
import com.amrmedhatandroid.e_cbuy.ui.activities.ProductDetailsActivity
import com.amrmedhatandroid.e_cbuy.ui.fragments.ProductsFragment
import com.amrmedhatandroid.e_cbuy.utils.Constants
import com.amrmedhatandroid.e_cbuy.utils.GlideLoader

open class MyProductsListAdapter(
    private val context: Context,
    private val mProducts: ArrayList<Product>,
    private val fragment: ProductsFragment
) : RecyclerView.Adapter<MyProductsListAdapter.MyProductsViewHolder>() {

    inner class MyProductsViewHolder(var itemContainerPatientBinding: ItemListLayoutBinding) :
        RecyclerView.ViewHolder(itemContainerPatientBinding.root) {
        fun setLabData(product: Product) {
            GlideLoader(context).loadProductImage(
                product.image,
                itemContainerPatientBinding.ivCartItemImage
            )
            itemContainerPatientBinding.tvCartItemTitle.text = product.title
            itemContainerPatientBinding.tvCartItemPrice.text = "$${product.price}"
            itemContainerPatientBinding.ibDeleteCartItem.setOnClickListener {
                fragment.deleteProduct(product.product_id)
            }
            itemContainerPatientBinding.root.setOnClickListener {
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.product_id)
                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, product.user_id)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProductsViewHolder {
        val binding =
            ItemListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyProductsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyProductsViewHolder, position: Int) {
        holder.setLabData(mProducts[position])
    }

    override fun getItemCount(): Int {
        return mProducts.size
    }
}