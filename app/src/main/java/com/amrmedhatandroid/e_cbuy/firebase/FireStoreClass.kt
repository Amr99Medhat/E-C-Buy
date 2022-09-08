package com.amrmedhatandroid.e_cbuy.firebase

import android.app.Activity
import android.app.AlertDialog
import android.util.Log
import androidx.fragment.app.Fragment
import com.amrmedhatandroid.e_cbuy.models.Product
import com.amrmedhatandroid.e_cbuy.utils.Constants
import com.amrmedhatandroid.e_cbuy.models.User
import com.amrmedhatandroid.e_cbuy.ui.activities.*
import com.amrmedhatandroid.e_cbuy.ui.fragments.DashboardFragment
import com.amrmedhatandroid.e_cbuy.ui.fragments.ProductsFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        // Create a collection in FireStore and upload the user's data. If the collection name is exists there is no problem.
        mFireStore.collection(Constants.USERS).document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                //If the task was success will call this function from the activity.
                activity.registerSuccess()
            }
            .addOnFailureListener {
                //If the task was failed will call this function from the activity.
                activity.registerFailed(it.message.toString())
            }
    }

    fun getUserDetails(activity: Activity) {

        //Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
            // The document id to get the fields of user.
            .document(FirebaseAuthClass().getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                // Here we received the document snapshot which is converted into the user Data model object.
                val user = document.toObject(User::class.java)

                // Pass the user data to log-in activity
                when (activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccessfully(user!!)
                    }
                    is SettingsActivity -> {
                        activity.userDetailsSuccess(user!!)
                    }
                }
            }
            .addOnFailureListener {
                when (activity) {
                    is LoginActivity -> {
                        activity.userLoggedInFailed(it.message.toString())
                    }
                    is SettingsActivity -> {
                        activity.userDetailsFailed(it.message.toString())
                    }
                }
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(FirebaseAuthClass().getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.updateUserProfileDataSuccess()
                    }
                }
            }
            .addOnFailureListener { error ->
                when (activity) {
                    is UserProfileActivity -> {
                        activity.updateUserProfileDataFailed(error.message.toString())
                    }
                }
            }
    }

    fun uploadProductDetails(activity: AddProductActivity, productInfo: Product) {
        mFireStore.collection(Constants.PRODUCTS)
            .document()
            .set(productInfo, SetOptions.merge())
            .addOnSuccessListener {
                // Here call a function of base activity for transferring the result to it.
                activity.productUploadSuccess()
            }
            .addOnFailureListener {
                activity.productUploadFailed()
            }
    }

    fun getProductsList(fragment: Fragment) {
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, FirebaseAuthClass().getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val productsList: ArrayList<Product> = ArrayList()
                for (p in document.documents) {
                    val product = p.toObject(Product::class.java)
                    product!!.product_id = p.id

                    productsList.add(product)
                }

                when (fragment) {
                    is ProductsFragment -> {
                        fragment.successProductsListFromFirestore(productsList)
                    }
                }
            }
            .addOnFailureListener {
                when (fragment) {
                    is ProductsFragment -> {
                        fragment.failedProductsListFromFirestore()
                    }
                }
            }
    }

    fun getProductDetails(activity: ProductDetailsActivity, productId: String) {
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .get()
            .addOnSuccessListener { document ->
                val product = document.toObject(Product::class.java)
                if (product != null) {
                    activity.getProductDetailsSuccess(product)
                }
            }
            .addOnFailureListener {
                activity.failedGetProductDetails()
            }
    }

    fun deleteProduct(fragment: ProductsFragment, productId: String) {
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {
                fragment.productDeleteSuccess()
            }
            .addOnFailureListener {
                fragment.failedProductDelete()
            }
    }

    fun getDashboardItemsList(fragment: DashboardFragment) {
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                Log.d("Amr", document.documents.toString())
                val productsList: ArrayList<Product> = ArrayList()

                for (p in document.documents) {
                    val product = p.toObject(Product::class.java)
                    product!!.product_id = p.id

                    productsList.add(product)
                }
                fragment.successGetDashboardItemsList(productsList)
            }
            .addOnFailureListener {
                DashboardFragment().failedGetDashboardItemsList()
            }
    }

}