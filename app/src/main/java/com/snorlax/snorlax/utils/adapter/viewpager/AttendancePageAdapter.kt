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

package com.snorlax.snorlax.utils.adapter.viewpager


import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.snorlax.snorlax.ui.home.attendance.AttendanceListFragment
import com.snorlax.snorlax.utils.TimeUtils.getTodayDateLocal
import com.snorlax.snorlax.utils.TimeUtils.positionToTime

class AttendancePageAdapter(
    fragment: Fragment
   /* private val callback: (date: Date) -> Observable<List<Attendance>>*/
) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = (getTodayDateLocal().time / 86_400_000).toInt() + 1

    override fun createFragment(position: Int): Fragment  {
        val date = positionToTime(position)
        return AttendanceListFragment(/*callback(positionToTime(position))*/).apply {
            arguments = Bundle().apply {
                putSerializable(AttendanceListFragment.ATTENDANCE_DATE, date)
            }
        }
    }

}

