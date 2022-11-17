package com.amrmedhatandroid.e_cbuy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Order(
    val user_id: String = "",
    val items: ArrayList<CartItem> = ArrayList(),
    val address: Address = Address(),
    val title: String = "",
    val image: String = "",
    var sub_total_amount: String = "",
    var shipping_charge: String = "",
    var total_amount: String = "",
    var id: String = ""
) : Parcelable