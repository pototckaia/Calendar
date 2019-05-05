package com.example.calendar.retrofit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.example.calendar.R
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit



class SampleFragment : MvpAppCompatFragment() {

    companion object {
        fun newInstance() : SampleFragment {
            return newInstance()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val v = inflater.inflate(
            R.layout.activity_main,
            container, false
        )



        return v
    }
}