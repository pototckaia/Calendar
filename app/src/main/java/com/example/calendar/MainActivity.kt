package com.example.calendar

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.util.Date

class MainActivity : AppCompatActivity(), StartWindowFragment.clickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(
                    R.id.clMainContainer,
                    StartWindowFragment.newInstance()
                )
                .commit()
        }
    }

    override fun onClickAddNote(date: Date) {
        val bundle = Bundle()
        bundle.putString(TEXT_VIEW_KEY, date.toString())
        val fragment = NoteReviewFragment.newInstance()
        fragment.arguments = bundle
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.clMainContainer,
                fragment
            )
            .addToBackStack(null)
            .commit()
    }
}