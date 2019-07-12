package com.example.calendar.eventFragment

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.MvpAppCompatFragment
import com.example.calendar.R
import com.example.calendar.customView.TimeZoneRecycleViewAdapter
import com.example.calendar.customView.TimeZoneModel
import com.example.calendar.helpers.ID_TIMEZONE_SELECT
import com.example.calendar.helpers.OnBackPressed
import com.example.calendar.inject.InjectApplication
import kotlinx.android.synthetic.main.view_timezone_list.view.*


class TimeZoneSelectFragment : MvpAppCompatFragment(), OnBackPressed {

    companion object {
        fun newInstance(
            id: Int
        ): TimeZoneSelectFragment {
            val args = Bundle()
            args.run {
                this.putInt(ID_TIMEZONE_SELECT, id)
            }

            val f = TimeZoneSelectFragment()
            f.arguments = args
            return f
        }
    }

    lateinit var v: View
    lateinit var exitViewModel: EventPatternViewModel

    var idResult = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        v = inflater.inflate(
            R.layout.view_timezone_list,
            container, false
        )

        exitViewModel = activity?.run {
            ViewModelProviders.of(this).get(EventPatternViewModel::class.java)
        } ?: throw Exception("Invalid scope to ViewModel")

        if (savedInstanceState == null) {
            idResult = arguments!!.getInt(ID_TIMEZONE_SELECT)
        } else {
            idResult = savedInstanceState.getInt(ID_TIMEZONE_SELECT)
        }

        val timezone = InjectApplication.inject.timezone
        val adapter = TimeZoneRecycleViewAdapter(timezone, { v: TimeZoneModel -> onSelectTimeZone(v)})
        val linerLayoutManager = LinearLayoutManager(context)
        val dividerItemDecoration = DividerItemDecoration(
            v.rvTimezone.context,
            linerLayoutManager.orientation
        )
        v.rvTimezone.run {
            this.adapter = adapter
            this.layoutManager = linerLayoutManager
            this.itemAnimator = DefaultItemAnimator()
            this.addItemDecoration(dividerItemDecoration)
        }

        // befor adapter
        initToolBar()
        return v
    }

    private fun initToolBar() {
        val toolbar = v.tbTimeZoneSelectFragment
        toolbar.setNavigationOnClickListener { onBackPressed() }

        toolbar.inflateMenu(R.menu.menu_search)
        val menu = toolbar.menu

        val adapter = v.rvTimezone.adapter as TimeZoneRecycleViewAdapter

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem!!.actionView as SearchView
        val searchManager = context!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity!!.componentName))

        searchView.maxWidth = Integer.MAX_VALUE
        searchView.setQueryHint(resources.getString(R.string.timezone_search_hint));
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                adapter.filter.filter(query)
                return false
            }
            override fun onQueryTextChange(query: String): Boolean {
                adapter.filter.filter(query)
                return false
            }
        })

        toolbar.setOnMenuItemClickListener { onItemSelected(it) }
    }

    private fun onItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_search -> false
            else -> true
        }


    private fun onSelectTimeZone(t : TimeZoneModel) {
        exitViewModel.timezone.postValue(Pair(idResult, t.zoneId))
        onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ID_TIMEZONE_SELECT, idResult)
    }

    override fun onBackPressed() {
        InjectApplication.inject.router.exit()
    }

}