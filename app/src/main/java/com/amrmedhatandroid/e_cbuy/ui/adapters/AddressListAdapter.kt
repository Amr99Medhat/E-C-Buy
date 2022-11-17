package com.amrmedhatandroid.e_cbuy.ui.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.amrmedhatandroid.e_cbuy.databinding.ItemAddressLayoutBinding
import com.amrmedhatandroid.e_cbuy.models.Address
import com.amrmedhatandroid.e_cbuy.ui.activities.AddEditAddressActivity
import com.amrmedhatandroid.e_cbuy.ui.activities.CheckoutActivity
import com.amrmedhatandroid.e_cbuy.utils.Constants

class AddressListAdapter(
    private val context: Context,
    private val list: ArrayList<Address>,
    private val selectAddress: Boolean
) : RecyclerView.Adapter<AddressListAdapter.AddressItemsListHolder>() {

    inner class AddressItemsListHolder(var itemAddressLayoutBinding: ItemAddressLayoutBinding) :
        RecyclerView.ViewHolder(itemAddressLayoutBinding.root) {
        fun setLabData(address: Address) {
            itemAddressLayoutBinding.tvAddressFullName.text = address.name
            itemAddressLayoutBinding.tvAddressType.text = address.type
            itemAddressLayoutBinding.tvAddressDetails.text =
                "${address.address}, ${address.zipCode}"
            itemAddressLayoutBinding.tvAddressMobileNumber.text = address.mobileNumber

            if (selectAddress) {
                itemAddressLayoutBinding.root.setOnClickListener {
                    val intent = Intent(context, CheckoutActivity::class.java)
                    intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS, address)
                    context.startActivity(intent)
                }
            }
        }
    }

    fun notifyEditItem(activity: Activity, position: Int) {
        val intent = Intent(context, AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS, list[position])
        activity.startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressItemsListHolder {
        val binding =
            ItemAddressLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressItemsListHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressItemsListHolder, position: Int) {
        holder.setLabData(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

}