/*
 * Copyright 2019 Oliver Rhyme G. Añasco
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.snorlax.snorlax.ui.home.attendance


import android.os.Bundle
import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.snorlax.snorlax.R
import com.snorlax.snorlax.utils.TimeUtils
import com.snorlax.snorlax.utils.TimeUtils.getTodayDateUTC
import com.snorlax.snorlax.utils.TimeUtils.timeToPosition
import com.snorlax.snorlax.utils.adapter.viewpager.AttendancePageAdapter
import com.snorlax.snorlax.viewmodel.AttendanceViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_attendance.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class AttendanceFragment : Fragment() {

    private lateinit var viewModel: AttendanceViewModel

    private val disposables = CompositeDisposable()

    private val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())

//    private val datePicker: MaterialDatePicker<Long> by lazy {
//        MaterialDatePicker.Builder.datePicker()
//            .setTitleText(R.string.label_which_day)
//            .setCalendarConstraints(
//                CalendarConstraints.Builder()
//                    .setOpenAt(getTodayDateUTC().time)
//                    .setStart(0)
//                    .setEnd(getTodayDateUTC().time)
//                    .setValidator(object : CalendarConstraints.DateValidator {
//                        var date: Long = 0
//                        override fun isValid(date: Long): Boolean {
//                            this.date = date
//                            return date <= getTodayDateUTC().time && date >= 0
//                        }
//
//                        override fun writeToParcel(dest: Parcel?, flags: Int) {
//                            dest?.writeInt(if (date <= getTodayDateUTC().time) 1 else 0)
//                        }
//
//                        override fun describeContents() = 0
//                    })
//                    .build()
//            )
//            .setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
//            .setSelection(viewModel.selectedTimeObservable.value)
//            .build()
//    }


//    private val adapter = AttendanceAdaptor(this, SortedList(Attendance::class.java, callback))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this)[AttendanceViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_attendance, container, false)



        rootView.picker_container.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.label_which_day)
                .setCalendarConstraints(
                    CalendarConstraints.Builder()
                        .setOpenAt(
                            when (viewModel.selectedTimeObservable.value) {
                                null -> getTodayDateUTC().time
                                else -> viewModel.selectedTimeObservable.value!!
                            }
                        )
                        .setStart(0)
                        .setEnd(getTodayDateUTC().time)
                        .setValidator(object : CalendarConstraints.DateValidator {
                            var date: Long = 0
                            override fun isValid(date: Long): Boolean {
                                this.date = date
                                return date <= getTodayDateUTC().time && date >= 0
                            }

                            override fun writeToParcel(dest: Parcel, flags: Int) {
                                dest.writeInt(if (date <= getTodayDateUTC().time) 1 else 0)
                            }

                            override fun describeContents() = 0
                        })
                        .build()
                )
                .setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
                .setSelection(viewModel.selectedTimeObservable.value)
                .build()


            picker.showNow(childFragmentManager, "DatePicker")
            picker.addOnPositiveButtonClickListener { viewModel.selectedTimeObservable.onNext(it) }

//            showDatePickerDialog()
        }

//        rootView.attendance_list.layoutManager = LinearLayoutManager(context!!)


        rootView.btn_export.setOnClickListener { viewModel.exportAttendance() }

//        rootView.attendance_list.adapter = AttendanceAdaptor()

        rootView.label_relative_time.text = viewModel.getRelativeDateString(getTodayDateUTC())
        rootView.label_date.text = dateFormat.format(getTodayDateUTC())

        rootView.attendance_pager.adapter = AttendancePageAdapter(requireActivity(), viewModel)

        rootView.attendance_pager.offscreenPageLimit = 1
        val relativeTime = viewModel.selectedTimeObservable
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe {
                //                rootView.label_relative_time.text = viewModel.getRelativeDateString(it)
//                rootView.label_date.text = dateFormat.format(Date(it))
                rootView.attendance_pager.setCurrentItem(timeToPosition(it), true)
                rootView.label_relative_time.text =
                    viewModel.getRelativeDateString(Date(it))
                rootView.label_date.text = dateFormat.format(Date(it))
            }


//        val firebase = viewModel.selectedTimeObservable
////            .flatMap { viewModel.getAttendance(Date(it)) }
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe {
////                (attendance_list.adapter as AttendanceAdaptor).updateData(it)
//                rootView.attendance_pager.setCurrentItem(timeToPosition(it), false)
//            }
        rootView.attendance_pager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            var previousItem = rootView.attendance_pager.currentItem
            override fun onPageSelected(position: Int) {
                if (previousItem != position) {
                    viewModel.selectedTimeObservable.onNext(TimeUtils.positionToTime(position).time)
                    previousItem = position
                }
            }

        })

//        val today = getTodayDateUTC().time
//        rootView.attendance_pager.setCurrentItem(timeToPosition(today), false)
//        rootView.label_relative_time.text =
//            viewModel.getRelativeDateString(Date(today))
//        rootView.label_date.text = dateFormat.format(Date(today))


        disposables.addAll(relativeTime)

        return rootView

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.selectedTimeObservable.onNext(getTodayDateUTC().time)

//        attendance_pager.setCurrentItem(timeToPosition(getTodayDate().time), false)
    }

//    private fun showDatePickerDialog() {
//        if (!datePicker.isVisible) {
//            datePicker.showNow(childFragmentManager, "DatePicker")
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

}
