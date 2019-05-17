package com.example.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.example.calendar.data.EventRecurrence
import com.example.calendar.data.EventRecurrenceRepository
import com.example.calendar.data.EventRoomDatabase
import com.example.calendar.data.ZoneDateTimeConverter
import com.example.calendar.helpers.toDateTimeUTC
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.test.view.*
import org.dmfs.rfc5545.recur.Freq
import org.dmfs.rfc5545.recur.RecurrenceRule
import org.threeten.bp.Duration
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.TemporalAdjusters
import java.util.Arrays.asList





class RecurrenceFragmentTest : MvpAppCompatFragment() {

    lateinit var repository: EventRecurrenceRepository
    lateinit var v: View
    private val compositeSubscription = CompositeDisposable();

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        v = inflater.inflate(
            R.layout.test,
            container, false
        )
        repository = EventRecurrenceRepository(EventRoomDatabase.getInstance(context!!).eventRecurrenceDao())
        insertEvents()

        return v
    }


    fun insertEvents() {
        // ruleStr is "FREQ=MONTHLY;BYMONTH=1,3,5,7;BYMONTHDAY=4,8,12;COUNT=20"
        // note that the months in this list are 1-based not 0-based
        //rule.setByRule(Parts.BYMONTH, 1, 3, 5, 7);
        //rule.setByRule(Parts.BYMONTHDAY, 4, 8, 12);

        val rule_daily = RecurrenceRule(Freq.DAILY)
        rule_daily.count = 5

        val rule_weekly = RecurrenceRule(Freq.WEEKLY)

        val now = ZonedDateTime.now(ZoneId.of("Z"))
        val end_month = now.plusMonths(2)

        val startRange = now.withDayOfMonth(1)
        val endRange = end_month.with(TemporalAdjusters.lastDayOfMonth())

        val rule_until = RecurrenceRule(Freq.WEEKLY)
        val until = now.plusMonths(1)
        rule_until.until = toDateTimeUTC(until)

        val events = arrayListOf<EventRecurrence>()
        events += EventRecurrence(
            name = "Test 1",
            note = "daily count 5, duration 40 minutes, start - now",
            startedAt = now,
            duration = Duration.ofMinutes(40),
            rrule = rule_daily.toString())

        events += EventRecurrence(
            name = "Test 2",
            note = "weekly endless, duration 20 minutes, start - now",
            startedAt = now,
            duration = Duration.ofMinutes(20),
            rrule = rule_weekly.toString())

        events += EventRecurrence(
            name = "Test 3",
            note = "weekly until now + 1 month, duration 10 minutes, start - now",
            startedAt = now,
            duration = Duration.ofMinutes(10),
            rrule = rule_until.toString())

        repository.dao.insert(events[0])
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                {
//                    v.insertOk.text = " Ok "
//                    queryFromTo(startRange, endRange)
//
//                }, {
//
//                })

        val ss = repository.fromToRec(startRange, endRange)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    v.events.text = " ${it.size} "

                }, {}
            )
//        compositeSubscription.add(s)
        compositeSubscription.add(ss)

    }

    fun queryFromTo(from: ZonedDateTime, to: ZonedDateTime) {
        val s = repository.fromTo(from, to)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    val s = ""
                    it.forEach {
                        s.plus("${it.nameEventRecurrence } + ${it.noteEventRecurrence} + ${it.startedAtInstance} \n")
                    }
                    v.events.text = s
                },
                {

                }
            )
        compositeSubscription.add(s)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeSubscription.clear()
    }

}