package com.example.calendar.views

import android.content.Context
import android.text.Layout
import android.text.StaticLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import 	android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.example.calendar.R

class DayView(context: Context,
              attrs: AttributeSet? = null,
              defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private var mTextHour: TextView = findViewById(R.id.tvDayItemHour)
    private var mDivideHour: LinearLayout = findViewById(R.id.hlDivideHour)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_item_day, this, true)
    }


    val hourTextWidth: Float
        get() {
            val param = mTextHour.getLayoutParams() as LinearLayout.LayoutParams
            val measureTextWidth = mTextHour.paint.measureText("12:00")
            return (Math.max(measureTextWidth, param.width)
                    + param.marginEnd
                    + param.marginStart)
        }

    val hourTextHeight: Float
        get() = StaticLayout(
            "12:00", mTextHour!!.getPaint(), hourTextWidth.toInt(),
            Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true
        ).getHeight()

    val separateHeight: Float
        get() = mSeparateHour!!.getLayoutParams().height

    fun setText(text: String) {
        mTextHour!!.setText(text)
    }

    fun setHourSeparatorAsInvisible() {
        mSeparateHour!!.setVisibility(INVISIBLE)

    }

    fun setHourSeparatorAsVisible() {
        mSeparateHour!!.setVisibility(VISIBLE)
    }

    fun setHourSeparatorIsVisible(b: Boolean) {
        if (b) {
            setHourSeparatorAsVisible()
        } else {
            setHourSeparatorAsInvisible()
        }

    }

}