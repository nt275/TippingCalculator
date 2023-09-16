package com.example.tippingcalculator

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat

//for logging for this MainActivity class
private const val TAG = "MainActivity"
//the initial tip percent amount
private const val INITIAL_TIP_PERCENT = 15

/*
    This application is a tipping calculator that has a text view for the user to enter in the bill amount,
     then a slider to change the tip percentage. The tip amount and total bill amount get updated in real time based
     on the slider and base amount inputs.

     @author Nicholas Tylka
 */
class MainActivity : AppCompatActivity() {

    private lateinit var etBaseAmount: EditText
    private lateinit var sbTip: SeekBar
    private lateinit var tvPercentLabel: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalBill: TextView
    private lateinit var tvTipDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //get references to all textViews and seekbars
        etBaseAmount = findViewById(R.id.etBaseAmount)
        sbTip = findViewById(R.id.sbTip)
        tvPercentLabel = findViewById(R.id.tvPercentLabel)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalBill = findViewById(R.id.tvTotalBill)
        tvTipDescription = findViewById(R.id.tvTipDescription)

        //initialize the program with a 15% tip percentage value
        sbTip.progress = INITIAL_TIP_PERCENT
        tvPercentLabel.text = "$INITIAL_TIP_PERCENT"
        updateTipDescription(INITIAL_TIP_PERCENT)
        //change the tip percentage based on slider value
        sbTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                Log.i(TAG, "onProgressChanged $progress")
                tvPercentLabel.text = "$progress%"
                computeTipAndTotalBill()
                updateTipDescription(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        //compute the total bill and tip amount from the base bill amount
        etBaseAmount.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterTextChanged $s")
                computeTipAndTotalBill()
            }
        })
    }

    /*
        This function updates the description underneath the tip slider
     */
    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription = when (tipPercent) {
            in 0..9 -> "Poor"
            in 10..14 -> "Acceptable"
            in 15..19 -> "Good"
            in 20..24 -> "Great"
            else -> "Amazing"
        }
        tvTipDescription.text = tipDescription
        //update the color based on tipPercent using an RGB evaluator, and turning
        // that value into an int to set the text color for the description text view
        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / sbTip.max,
            ContextCompat.getColor(this, R.color.color_worst_tip),
                    ContextCompat.getColor(this, R.color.color_best_tip)
        ) as Int
        tvTipDescription.setTextColor(color)
    }

    /*
        This function computes the total and tip amounts based on the user changes to the total bill amount or tip percent amount slider.
     */
    private fun computeTipAndTotalBill() {
    // if the total bill amount is empty, then make the tip amount and total bill amount empty and return early
    if (etBaseAmount.text.isEmpty()) {
        tvTipAmount.text = ""
        tvTotalBill.text = ""
        return
    }
        //1. GET value of base amount and tip percent inputs
        val baseBillAmount = etBaseAmount.text.toString().toDouble()
        val tipPercent = sbTip.progress
        //2. THEN compute tip and total
        val tipAmount = baseBillAmount * tipPercent / 100
        val totalAmount = baseBillAmount + tipAmount
        //3. THEN update the UI
        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalBill.text = "%.2f".format(totalAmount)
    }
}