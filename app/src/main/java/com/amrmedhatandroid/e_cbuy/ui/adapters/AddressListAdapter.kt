package com.amrmedhatandroid.e_cbuy.ui.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amrmedhatandroid.e_cbuy.databinding.ItemAddressLayoutBinding
import com.amrmedhatandroid.e_cbuy.models.Address
import com.amrmedhatandroid.e_cbuy.ui.activities.AddEditAddressActivity
import com.amrmedhatandroid.e_cbuy.utils.Constants

class AddressListAdapter(
    private val context: Context,
    private val list: ArrayList<Address>
) : RecyclerView.Adapter<AddressListAdapter.AddressItemsListHolder>() {

    inner class AddressItemsListHolder(var itemAddressLayoutBinding: ItemAddressLayoutBinding) :
        RecyclerView.ViewHolder(itemAddressLayoutBinding.root) {
        fun setLabData(address: Address) {
            itemAddressLayoutBinding.tvAddressFullName.text = address.name
            itemAddressLayoutBinding.tvAddressType.text = address.type
            itemAddressLayoutBinding.tvAddressDetails.text =
                "${address.address}, ${address.zipCode}"
            itemAddressLayoutBinding.tvAddressMobileNumber.text = address.mobileNumber
        }
    }

    fun notifyEditItem(activity: Activity, position: Int) {
        val intent = Intent(context, AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS, list[position])
        activity.startActivity(intent)
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