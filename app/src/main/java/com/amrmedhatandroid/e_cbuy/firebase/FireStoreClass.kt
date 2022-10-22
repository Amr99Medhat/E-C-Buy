package com.amrmedhatandroid.e_cbuy.firebase

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import com.amrmedhatandroid.e_cbuy.models.Address
import com.amrmedhatandroid.e_cbuy.models.CartItem
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

    fun addCartItems(activity: ProductDetailsActivity, addToCart: CartItem) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document()
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }
            .addOnFailureListener {
                activity.failedAddToCart()
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

    fun getCartList(activity: Activity) {
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, FirebaseAuthClass().getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val list: ArrayList<CartItem> = ArrayList()
                for (i in document.documents) {
                    val cartItem = i.toObject(CartItem::class.java)!!
                    cartItem.id = i.id
                    list.add(cartItem)
                }

                when (activity) {
                    is CartListActivity -> {
                        activity.successCartItemsList(list)
                    }
                }

            }
            .addOnFailureListener {
                when (activity) {
                    is CartListActivity -> {
                        activity.failedCartItemsList()
                    }
                }
            }
    }

    fun checkIfItemExistInCart(activity: ProductDetailsActivity, productId: String) {
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, FirebaseAuthClass().getCurrentUserID())
            .whereEqualTo(Constants.PRODUCT_ID, productId)
            .get()
            .addOnSuccessListener { document ->
                if (document.documents.size > 0) {
                    activity.productExistInCart()
                } else {
                    activity.failedCheckIfItemExistInCart()
                }
            }
            .addOnFailureListener {
                activity.failedCheckIfItemExistInCart()
            }
    }

    fun getAllProductsList(activity: CartListActivity) {
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                val productsList: ArrayList<Product> = ArrayList()
                for (p in document.documents) {
                    val product = p.toObject(Product::class.java)
                    product!!.product_id = p.id
                    productsList.add(product)
                }
                activity.successProductsListFromFireStore(productsList)
            }
            .addOnFailureListener {
                activity.failedGetAllProductsList()
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

    fun removeItemFromCart(context: Context, cartId: String) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cartId)
            .delete()
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener {
                when (context) {
                    is CartListActivity -> {
                        context.failedRemoveItemFromCart()
                    }
                }
            }
    }

    fun updateMyCart(context: Context, cartId: String, itemHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cartId)
            .update(itemHashMap)
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener {
                when (context) {
                    is CartListActivity -> {
                        context.failedUpdateMyCart()
                    }
                }
            }
    }

    fun getAddressesList(activity: AddressListActivity) {
        mFireStore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, FirebaseAuthClass().getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val addressList: ArrayList<Address> = ArrayList()

                for (a in document.documents) {
                    val address = a.toObject(Address::class.java)!!
                    address.id = a.id
                    addressList.add(address)
                }
                activity.successAddressListFormFireStore(addressList)
            }.addOnFailureListener {
                activity.failedUpdateMyCart()
            }
    }

    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {
        mFireStore.collection(Constants.ADDRESSES)
            .document()
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }.addOnFailureListener {
                activity.failedAddAddress()
            }
    }

    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String) {
        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener {
                activity.failedAddAddress()
            }
    }

}