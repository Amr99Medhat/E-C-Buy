package com.amrmedhatandroid.e_cbuy.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amrmedhatandroid.e_cbuy.database.PreferenceManager
import com.amrmedhatandroid.e_cbuy.databinding.ActivityMainBinding
import com.amrmedhatandroid.e_cbuy.utils.Constants

class MainActivity : AppCompatActivity() {
    lateinit var mActivityMainBinding: ActivityMainBinding
    lateinit var mPreferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mActivityMainBinding.root)
        mPreferenceManager = PreferenceManager(this@MainActivity)


        val name = mPreferenceManager.getString(Constants.LOGGED_IN_USERNAME)

        mActivityMainBinding.tvName.text = "Hello $name."
    }
}