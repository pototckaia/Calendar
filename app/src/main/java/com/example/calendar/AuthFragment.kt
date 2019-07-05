package com.example.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatFragment
import com.example.calendar.server.Server
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class AuthFragment : MvpAppCompatFragment() {

    companion object {
        fun newInstance() : AuthFragment {
            return AuthFragment()
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

        val r = Server.server.api.getEventsOffset(count = 100, offset = 0)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Toast.makeText(context, "Ok!!", Toast.LENGTH_LONG).show()
                Log.d("OkHttp", "Size data ${it.data.size}")
            }, {
                Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                Log.d("OkHttp", it.toString())
            })


        return v
    }
}