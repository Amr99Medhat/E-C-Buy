package com.amrmedhatandroid.e_cbuy.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.amrmedhatandroid.e_cbuy.R
import com.amrmedhatandroid.e_cbuy.databinding.FragmentSoldProductsBinding
import com.amrmedhatandroid.e_cbuy.firebase.FireStoreClass
import com.amrmedhatandroid.e_cbuy.models.SoldProduct
import com.amrmedhatandroid.e_cbuy.ui.adapters.SoldProductsListAdapter

class SoldProductsFragment : BaseFragment() {
    private var _binding: FragmentSoldProductsBinding? = null

    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSoldProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun getSoldProductsList() {
        showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().getSoldProductsList(this@SoldProductsFragment)
    }

    fun successSoldProductsList(soldProductsList: ArrayList<SoldProduct>) {
        hideProgressDialog()


        if (soldProductsList.size > 0) {
            binding.rvSoldProductsItemsList.visibility = View.VISIBLE
            binding.tvNoProductsSoldYet.visibility = View.GONE

            binding.rvSoldProductsItemsList.layoutManager = LinearLayoutManager(activity)
            binding.rvSoldProductsItemsList.setHasFixedSize(true)

            val soldProductsListAdapter =
                SoldProductsListAdapter(requireActivity(), soldProductsList)
            binding.rvSoldProductsItemsList.adapter = soldProductsListAdapter


        } else {
            binding.rvSoldProductsItemsList.visibility = View.GONE
            binding.tvNoProductsSoldYet.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        getSoldProductsList()
    }
}