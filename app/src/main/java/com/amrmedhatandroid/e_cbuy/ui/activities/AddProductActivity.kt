package com.amrmedhatandroid.e_cbuy.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.amrmedhatandroid.e_cbuy.databinding.ActivityAddProductBinding

class AddProductActivity : AppCompatActivity() {
    lateinit var mActivityAddProductBinding: ActivityAddProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityAddProductBinding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(mActivityAddProductBinding.root)
        setListeners()
    }


    /**
     * A function to handle user clicks.
     */
    private fun setListeners() {
        mActivityAddProductBinding.ivBack.setOnClickListener {
            // back to the previous screen.
            onBackPressed()
        }
    }
}