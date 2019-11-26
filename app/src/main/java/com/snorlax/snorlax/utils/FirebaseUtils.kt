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

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import io.reactivex.Completable

fun FirebaseUser.updateAdminProfile(request: UserProfileChangeRequest): Completable =
    Completable.create { emitter ->
        this.updateProfile(request).addOnSuccessListener {
            emitter.onComplete()
        }.addOnFailureListener {
            emitter.onError(it)
        }
    }

fun DocumentReference.getLastPath() : String {
    return this.path.substring(this.path.indexOfLast {
        it == '/'
    })
}