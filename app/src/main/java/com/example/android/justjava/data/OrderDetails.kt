package com.example.android.justjava.data

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.android.justjava.R
import com.example.android.justjava.R.string.toppings
import java.text.NumberFormat
import java.util.*

class OrderDetails(private val mContext: Context) {

    companion object {
        @JvmField val TAG = OrderDetails::class.java.simpleName!!
    }

    private val coffeePrice = 5
    enum class Topping(val textId : Int, val price : Int) {
        WhippedCream(R.string.whipped_cream, 1),
        Chocolate(R.string.chocolate, 2)
    }

    private var mQuantity = 0
    private val mToppingsList = HashMap<Topping, Boolean>()
    var mName : String = ""

    var isOrderAvailable : Boolean = false
        get() = mQuantity > 0 && mName.isNotEmpty()

    init {
        Topping.values().forEach {
            mToppingsList[it] = false
        }
    }

    private fun calculatePrice() : Int {
        val unitPrice = coffeePrice + mToppingsList.keys.
                                          filter { it -> mToppingsList[it]!! }.
                                          sumBy { it -> it.price }
        Log.d(TAG, "unitPrice: " + unitPrice)
        return unitPrice * mQuantity
    }

    fun createOrderSummary(): String {
        val orderSummary = StringBuilder("")
        val price = calculatePrice()
        val priceMessage: String
        when {
            price == 0 -> priceMessage = mContext.getString(R.string.free)
            price > 0 -> {
                val format = NumberFormat.getCurrencyInstance()
                format.currency = Currency.getInstance("USD")
                priceMessage = mContext.getString(R.string.total)+": " + format.format(price.toLong())
            }
            else -> priceMessage = "== ERROR ==" // dead code, negative not allowed any more
        }
        val quantityString = mContext.getString(R.string.quantity)
        val nameString = mContext.getString(R.string.name)
        val toppingsString = toppingsString()
        orderSummary.append("$nameString: $mName\n")
                .append("$quantityString: $quantityString\n")
                .append(toppingsString)
                .append("$priceMessage\n")
                .append(mContext.getString(R.string.thank_you))
        return orderSummary.toString()
    }

    private fun toppingsString(): String {
        val toppings = StringBuilder("")
        mToppingsList.keys.
                filter { it -> mToppingsList[it]!! }.
                map { it -> mContext.getString(it.textId) }.
                forEach { toppings.append("\t$it\n") }
        return toppings.toString()
    }

    fun sendOrder() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("justjava@mailinator.com"))
        // intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_SUBJECT, mContext.getString(R.string.subject, mName))
        intent.putExtra(Intent.EXTRA_TEXT, createOrderSummary())
        Log.d(TAG, "sending e-mail")
        if (intent.resolveActivity(mContext.packageManager) != null) {
            Log.d(TAG, "really sending e-mail")
            mContext.startActivity(intent)
        }
    }

    fun setTopping(topping : Topping, value : Boolean) {
        Log.d(TAG, "setTopping: " + topping.name + ": "+value)
        mToppingsList[topping] = value
        Log.d(TAG, "toppings: " + mToppingsList)
    }

    fun getQuantity() : Int {
        return mQuantity
    }

    fun incrementQuantity() {
        mQuantity++
    }

    fun decrementQuantity() {
        if (mQuantity > 1) {
            --mQuantity
        }
    }

}
