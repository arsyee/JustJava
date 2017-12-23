package com.example.android.justjava

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.android.justjava.R.string.*

import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        @JvmField val TAG = MainActivity.javaClass.simpleName
    }

    private var mQuantity = 0
    private var mToppingsList : String = ""
    private var mName : String = ""

    private lateinit var mTvOrderSummary : TextView
    private lateinit var mTvQuantity : TextView
    private lateinit var mCbWhippedCream : CheckBox
    private lateinit var mCbChocolate : CheckBox

    private lateinit var mEtName : EditText
    private lateinit var mEtNameHandler : Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mTvOrderSummary = findViewById<TextView>(R.id.tv_order_summary)
        mTvQuantity = findViewById<TextView>(R.id.tv_quantity)
        mCbWhippedCream = findViewById<CheckBox>(R.id.cb_whipped_cream)
        mCbChocolate = findViewById<CheckBox>(R.id.cb_chocolate)

        mEtName = findViewById<EditText>(R.id.et_name)
        mEtNameHandler = Handler()
        mEtName.addTextChangedListener(object : TextWatcher {
            val updateName = UpdateName()

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mEtNameHandler.removeCallbacks(updateName)
                mEtNameHandler.postDelayed(updateName, 1000)
            }

            override fun afterTextChanged(p0: Editable?) { }
        })

        // TODO: isChecked always returns false after onCreate, while display state is retained
        Log.d(TAG, "onCreate: checkbox: "+mCbWhippedCream.isChecked)
        calculateToppings()
        refreshViews()
    }

    fun submitOrder(view: View) {
        showToast(getString(R.string.toast_button_clicked))
    }

    fun refreshViews() {
        display(mQuantity)
        displayOrderSummary(mName, mQuantity, mToppingsList)
    }

    private fun calculatePrice(quantity: Int) = 5 * quantity

    private fun display(quantity: Int) {
        mTvQuantity.text = NumberFormat.getInstance().format(quantity.toLong())
    }

    private fun displayOrderSummary(name : String, quantity: Int, toppingsList: String) {
        val orderSummary: String = createOrderSummary(name, quantity, toppingsList)
        mTvOrderSummary.text = orderSummary
    }

    private fun createOrderSummary(name : String, quantity: Int, toppingsList: String): String {
        val orderSummary: StringBuilder = StringBuilder("")
        val price = calculatePrice(quantity)
        val priceMessage: String
        if (price == 0) {
            priceMessage = "Free"
        } else if (price > 0) {
            priceMessage = "Total: " + NumberFormat.getCurrencyInstance().format(price.toLong())
        } else {
            priceMessage = "You get: " + NumberFormat.getCurrencyInstance().format((-price).toLong())
        }
        val quantityString = getText(R.string.quantity)
        val nameString = getText(R.string.name)
        orderSummary.append("$nameString: $name\n")
                    .append("$quantityString: $quantity\n")
                    .append(toppingsList)
                    .append("$priceMessage\n")
                    .append("Thank you!")
        return orderSummary.toString()
    }

    private fun calculateToppings() {
        Log.d(TAG, "calculateToppings: checkbox: "+mCbWhippedCream.isChecked)
        val toppings: StringBuilder = StringBuilder("")
        val whippedCream = getString(whipped_cream)
        if (mCbWhippedCream.isChecked) toppings.append("\t$whippedCream\n")
        val chocolate = getString(R.string.chocolate)
        if (mCbChocolate.isChecked) toppings.append("\t$chocolate\n")
        mToppingsList = toppings.toString()
        Log.d(TAG, "toppings: " + mToppingsList)
    }

    inner class UpdateName : Runnable {
        override fun run() {
            mName = mEtName.text.toString()
            refreshViews()
        }
    }

    fun refreshToppings(view: View) {
        calculateToppings()
        refreshViews()
    }

    fun decrementQuantity(view: View) {
        mQuantity--
        refreshViews()
    }

    fun incrementQuantity(view: View) {
        mQuantity++
        refreshViews()
    }

    private fun showToast(s: String) {
        val toast = Toast.makeText(this, s, Toast.LENGTH_LONG)
        toast.show()
    }

}
