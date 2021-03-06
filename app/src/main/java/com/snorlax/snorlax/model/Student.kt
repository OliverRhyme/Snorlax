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

package com.snorlax.snorlax.model

import com.snorlax.snorlax.utils.capitalizeWords
import java.util.*

data class Student(
    val name: Map<String, String> = mapOf(FIRST_NAME_VAL to "", LAST_NAME_VAL to ""),
    val lrn: String = "",
    val imageUrl: String? = null
) {
    companion object {
        const val FIRST_NAME_VAL = "first"
        const val LAST_NAME_VAL = "last"
        const val NAME_VAL = "name"
    }

    val displayName: String
        get() = "${name.getValue(LAST_NAME_VAL).toUpperCase(Locale.getDefault())}, ${name.getValue(
            FIRST_NAME_VAL
        ).capitalizeWords()}"

//    fun getDisplayName() : String {
//        return "${name.getValue("last").toUpperCase(Locale.getDefault())}, ${name.getValue("first")}"
//    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherStudent = other as Student? ?: return false
        return lrn.contentEquals(otherStudent.lrn) &&
                name == otherStudent.name &&
                imageUrl.equals(otherStudent.imageUrl)
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 69 * result + lrn.hashCode()
        result = 69 * result + (imageUrl?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return displayName
    }
}
