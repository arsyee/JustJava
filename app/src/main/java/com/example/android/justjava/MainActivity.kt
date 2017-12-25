package com.example.android.justjava

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast

import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        @JvmField val TAG = MainActivity::class.java.simpleName!!
    }

    enum class Toppings { WhippedCream, Chocolate }

    private var mQuantity = 0
    private var mToppingsList = ArrayList<Toppings>()
    private var mName : String = ""

    private lateinit var mEtNameHandler : Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mEtNameHandler = Handler()
        et_name.addTextChangedListener(object : TextWatcher {
            val updateName = UpdateName()

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mEtNameHandler.removeCallbacks(updateName)
                mEtNameHandler.postDelayed(updateName, 1000)
            }

            override fun afterTextChanged(p0: Editable?) { }
        })
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        calculateToppings()
        refreshViews()
    }

    fun submitOrder(view: View) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("justjava@mailinator.com"))
        // intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject, mName))
        intent.putExtra(Intent.EXTRA_TEXT, tv_order_summary.text.toString())
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
        val unitPrice = 5 + toppingsList.sumBy { when (it) {
                                                     Toppings.WhippedCream -> 1
                                                     Toppings.Chocolate -> 2
                                               } }
        return unitPrice * quantity
    }

    private fun display(quantity: Int) {
        tv_quantity.text = NumberFormat.getInstance().format(quantity.toLong())
    }

    private fun displayOrderSummary(name : String, quantity: Int, toppingsList: List<Toppings>) {
        val orderSummary: String = createOrderSummary(name, quantity, toppingsList)
        tv_order_summary.text = orderSummary
    }

    private fun createOrderSummary(name : String, quantity: Int, toppingsList: List<Toppings>): String {
        val orderSummary = StringBuilder("")
        val price = calculatePrice(quantity, toppingsList)
        val priceMessage: String
        when {
            price == 0 -> priceMessage = getString(R.string.free)
            price > 0 -> {
                val format = NumberFormat.getCurrencyInstance()
                format.currency = Currency.getInstance("USD")
                priceMessage = getString(R.string.total)+": " + format.format(price.toLong())
            }
            else -> priceMessage = "== ERROR ==" // dead code, negative not allowed any more
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
        val toppings = StringBuilder("")
        toppingsList.map { when (it) {
                               Toppings.WhippedCream -> getString(R.string.whipped_cream)
                               Toppings.Chocolate -> getString(R.string.chocolate)
                         } }.forEach { toppings.append("\t$it\n") }
        return toppings.toString()
    }

    private fun calculateToppings() {
        Log.d(TAG, "calculateToppings: whipped cream: "+cb_whipped_cream.isChecked)
        mToppingsList = ArrayList()
        if (cb_whipped_cream.isChecked) {
            mToppingsList.add(Toppings.WhippedCream)
        } else {
            mToppingsList.remove(Toppings.WhippedCream)
        }
        Log.d(TAG, "calculateToppings: chocolate: "+cb_chocolate.isChecked)
        if (cb_chocolate.isChecked) {
            mToppingsList.add(Toppings.Chocolate)
        } else {
            mToppingsList.remove(Toppings.Chocolate)
        }
        Log.d(TAG, "toppings: " + mToppingsList)
    }

    inner class UpdateName : Runnable {
        override fun run() {
            mName = et_name.text.toString()
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
