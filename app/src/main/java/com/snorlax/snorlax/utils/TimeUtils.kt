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

package com.snorlax.snorlax.utils

import java.util.*


fun Long.isNegative(): Boolean {
    return (this < 0L)
}

fun Long.countHowManyTime(unit: Long): Pair<Int, Int> {
    return Pair((this / unit).toInt(), (this % unit).toInt())
}

fun Long.addDate(howMuch: Int, unit: Long): Long {
    return this + (howMuch * unit)
}

fun getTodayDate(): Date {
    val date = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    date.set(Calendar.HOUR_OF_DAY, date.getActualMinimum(Calendar.HOUR_OF_DAY))
    date.set(Calendar.MINUTE, date.getActualMinimum(Calendar.MINUTE))
    date.set(Calendar.SECOND, date.getActualMinimum(Calendar.SECOND))
    date.set(Calendar.MILLISECOND, date.getActualMinimum(Calendar.MILLISECOND))

    return date.time
}

fun getTommorowDate(date: Date): Date {
    val today = Calendar.getInstance().apply {
        this.time = date
    }
    today.set(Calendar.SECOND, 86400)
    return today.time
}

fun Date.getDayTimeStamp() : Date {
    this.time = (this.time /100_00) * 100_000
    return this
}
