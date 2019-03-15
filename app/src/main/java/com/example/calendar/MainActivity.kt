package com.example.calendar

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout
import com.example.calendar.adapters.MyAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var viewAdapter: RecyclerView.Adapter<*>
//    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myDataset= arrayOf("foo", "bar", "baz", "dog",
                                "cat","owl", "cheetah", "raccoon", "bird",
                                "snake", "lizard", "hamster", "bear", "lion",
                                "tiger", "horse", "frog", "fish", "shark",
                                "turtle",  "elephant", "cow", "beaver",
                                "bison", "porcupine", "rat", "mouse", "goose",
                                "deer",  "fox", "moose", "buffalo", "monkey",
                                "penguin", "parrot")

        my_recycler_view.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = MyAdapter(myDataset, this@MainActivity)
        }
//        viewManager = LinearLayoutManager(this)
//        viewAdapter = MyAdapter(myDataset)
//
//        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {
//            // use this setting to improve performance if you know that changes
//            // in content do not change the layout size of the RecyclerView
//            setHasFixedSize(true)
//            // use a linear layout manager
//            layoutManager = viewManager
//            // specify an viewAdapter (see also next example)
//            adapter = viewAdapter
//        }
    }
}