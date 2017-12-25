package com.example.android.justjava

import android.content.Intent
import android.net.Uri
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
import com.example.android.justjava.R.string.toppings

import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    companion object {
        @JvmField val TAG = MainActivity.javaClass.simpleName
    }

    enum class Toppings { Whipped_Cream, Chocolate }

    private var mQuantity = 0
    private var mToppingsList = ArrayList<Toppings>()
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
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG, "onRestoreInstanceState: checkbox: "+mCbWhippedCream.isChecked)

        calculateToppings()
        refreshViews()
    }

    fun submitOrder(view: View) {
        var intent = Intent(Intent.ACTION_SEND)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("justjava@mailinator.com"))
        // intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject, mName))
        intent.putExtra(Intent.EXTRA_TEXT, mTvOrderSummary.text.toString())
        Log.d(TAG, "sending e-mail")
        if (intent.resolveActivity(packageManager) != null) {
            Log.d(TAG, "really sending e-mail")
            startActivity(intent)
        }
        showToast(getString(R.string.toast_button_clicked))
    }

    fun refreshViews() {
        display(mQuantity)
        displayOrderSummary(mName, mQuantity, mToppingsList)
    }

    private fun calculatePrice(quantity: Int, toppingsList: List<Toppings>) : Int {
        var unitPrice = 5
        for (topping in toppingsList) {
            unitPrice += when (topping) {
                Toppings.Whipped_Cream -> 1
                Toppings.Chocolate -> 2
            }
        }
        return unitPrice * quantity
    }

    private fun display(quantity: Int) {
        mTvQuantity.text = NumberFormat.getInstance().format(quantity.toLong())
    }

    private fun displayOrderSummary(name : String, quantity: Int, toppingsList: List<Toppings>) {
        val orderSummary: String = createOrderSummary(name, quantity, toppingsList)
        mTvOrderSummary.text = orderSummary
    }

    private fun createOrderSummary(name : String, quantity: Int, toppingsList: List<Toppings>): String {
        val orderSummary: StringBuilder = StringBuilder("")
        val price = calculatePrice(quantity, toppingsList)
        val priceMessage: String
        if (price == 0) {
            priceMessage = getString(R.string.free)
        } else if (price > 0) {
            val format = NumberFormat.getCurrencyInstance()
            format.currency = Currency.getInstance("USD")
            priceMessage = getString(R.string.total)+": " + format.format(price.toLong())
        } else {
            priceMessage = "== ERROR =="
            // dead code, negative not allowed any more
        }
        val quantityString = getText(R.string.quantity)
        val nameString = getText(R.string.name)
        val toppingsString = toppingsString(toppingsList)
        orderSummary.append("$nameString: $name\n")
                    .append("$quantityString: $quantity\n")
                    .append(toppingsString)
                    .append("$priceMessage\n")
                    .append(getString(R.string.thank_you))
        return orderSummary.toString()
    }

    private fun toppingsString(toppingsList: List<Toppings>): String {
        val toppings: StringBuilder = StringBuilder("")
        for (topping in toppingsList) {
            val toppingString = when (topping) {
                Toppings.Whipped_Cream -> getString(R.string.whipped_cream)
                Toppings.Chocolate -> getString(R.string.chocolate)
            }
            toppings.append("\t$toppingString\n")
        }
        return toppings.toString();
    }

    private fun calculateToppings() {
        Log.d(TAG, "calculateToppings: whipped cream: "+mCbWhippedCream.isChecked)
        mToppingsList = ArrayList()
        if (mCbWhippedCream.isChecked) {
            mToppingsList.add(Toppings.Whipped_Cream)
        } else {
            mToppingsList.remove(Toppings.Whipped_Cream)
        }
        Log.d(TAG, "calculateToppings: chocolate: "+mCbChocolate.isChecked)
        if (mCbChocolate.isChecked) {
            mToppingsList.add(Toppings.Chocolate)
        } else {
            mToppingsList.remove(Toppings.Chocolate)
        }
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
        if (mQuantity > 0) {
            mQuantity--
            refreshViews()
        }
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
