package com.example.calendar

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.view_item_hour_day_calendar.view.*
import android.text.Layout
import android.text.StaticLayout
import android.support.v4.view.MarginLayoutParamsCompat.getMarginStart
import android.support.v4.view.MarginLayoutParamsCompat.getMarginEnd
import android.widget.LinearLayout



class HourItemView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    private var view: View = LayoutInflater.from(context).inflate(
        R.layout.view_item_hour_day_calendar, this, true)

    fun setHour(hour: String) {
        view.tvHour.text = hour;
    }

    fun getSeparateHeight(): Int {
        return view.vDividerHour.layoutParams.height
    }

//    fun getHourTextWidth(): Float {
//        val param = mTextHour.getLayoutParams() as LinearLayout.LayoutParams
//        val measureTextWidth = mTextHour.getPaint().measureText("12:00")
//        return (Math.max(measureTextWidth, param.width.toFloat())
//                + param.marginEnd.toFloat()
//                + param.marginStart.toFloat())
//    }
//
//    fun getHourTextHeight(): Float {
//        return StaticLayout(
//            "12:00", mTextHour.getPaint(), getHourTextWidth().toInt(),
//            Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true
//        ).height.toFloat()
//    }

// Create custom attributes to read.
// To do that, just create a attrs.xml file in the res/values folder.

// <?xml version="1.0" encoding="utf-8"?>
// <resources>
//     <declare-styleable name="custom_component_attributes">
//         <attr name="custom_component_title" format="reference" />
//     </declare-styleable>
// </resources>
// attrs?.let {
//     val typedArray = context.obtainStyledAttributes(it, 
//         R.styleable.custom_component_attributes, 0, 0)
//     val title = resources.getText(typedArray
//             .getResourceId(R.styleable
//             .custom_component_attributes_custom_component_title,           
//             R.string.component_one))

//     my_title.text = title
//     my_edit.hint = 
//         "${resources.getString(R.string.hint_text)} $title"

//     typedArray.recycle()
// }
}