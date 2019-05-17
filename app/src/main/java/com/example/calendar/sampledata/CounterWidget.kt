package com.example.calendar.sampledata

import com.arellomobile.mvp.MvpDelegate
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import android.widget.FrameLayout
import com.example.calendar.R

class CounterWidget(context: Context, attrs: AttributeSet) :
    FrameLayout(context, attrs), CounterView {

    private var mParentDelegate: MvpDelegate<*>? = null
    private var mMvpDelegate: MvpDelegate<CounterWidget>? = null

    @InjectPresenter
    lateinit var mCounterPresenter: CounterPresenter

    private val mCounterTextView: TextView

    val mvpDelegate: MvpDelegate<CounterWidget>?
        get() {
            if (mMvpDelegate != null) {
                return mMvpDelegate
            }

            mMvpDelegate = MvpDelegate(this)
            mMvpDelegate!!.setParentDelegate(mParentDelegate!!, id.toString())
            return mMvpDelegate
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_item_counter, this, true)
        mCounterTextView = findViewById<View>(R.id.count_text) as TextView

        val button = findViewById<View>(R.id.plus_button)
        button.setOnClickListener({ view -> mCounterPresenter.onPlusClick() })
    }

    fun init(parentDelegate: MvpDelegate<*>) {
        mParentDelegate = parentDelegate

        mvpDelegate!!.onCreate()
        mvpDelegate!!.onAttach()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        mvpDelegate!!.onSaveInstanceState()
        mvpDelegate!!.onDetach()
    }

    override fun showCount(count: Int) {
        mCounterTextView.text = count.toString()
    }
}