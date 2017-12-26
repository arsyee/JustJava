package com.example.android.justjava

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import com.example.android.justjava.data.OrderDetails

import java.text.NumberFormat

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        @JvmField val TAG = MainActivity::class.java.simpleName!!
    }

    private lateinit var mOrderDetails : OrderDetails

    private lateinit var mEtNameHandler : Handler

    // Lifecycle handlers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mOrderDetails = OrderDetails(applicationContext)

        mEtNameHandler = Handler()
        et_name.addTextChangedListener(object : TextWatcher {
            val updateName = UpdateName()

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                b_order.isEnabled = false
                mEtNameHandler.removeCallbacks(updateName)
                mEtNameHandler.postDelayed(updateName, 1000)
            }

            override fun afterTextChanged(p0: Editable?) { }
        })
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        onWhippedCreamCheckboxClicked(cb_whipped_cream)
        onChocolateCheckboxClicked(cb_chocolate)
        refreshViews()
    }

    // Misc functions

    fun refreshViews() {
        tv_quantity.text = NumberFormat.getInstance().format(mOrderDetails.getQuantity().toLong())
        tv_order_summary.text = mOrderDetails.createOrderSummary()
        b_order.isEnabled = mOrderDetails.isOrderAvailable
    }

    private fun showToast(s: String) {
        val toast = Toast.makeText(this, s, Toast.LENGTH_LONG)
        toast.show()
    }

    // View event handlers

    @Suppress("UNUSED_PARAMETER")
    fun onOrderButtonClicked(view: View) {
        mOrderDetails.sendOrder()
        showToast(getString(R.string.toast_button_clicked))
    }

    inner class UpdateName : Runnable {
        override fun run() {
            mOrderDetails.mName = et_name.text.toString()
            refreshViews()
        }
    }

    fun onWhippedCreamCheckboxClicked(view: View) {
        if (view is CheckBox) mOrderDetails.setTopping(OrderDetails.Topping.WhippedCream, view.isChecked)
        refreshViews()
    }

    fun onChocolateCheckboxClicked(view: View) {
        if (view is CheckBox) mOrderDetails.setTopping(OrderDetails.Topping.Chocolate, view.isChecked)
        refreshViews()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onQuantityDownButtonClicked(view: View) {
        mOrderDetails.decrementQuantity()
        refreshViews()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onQuantityUpButtonClicked(view: View) {
        mOrderDetails.incrementQuantity()
        refreshViews()
    }

}
