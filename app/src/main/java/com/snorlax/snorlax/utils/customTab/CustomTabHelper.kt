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

package com.snorlax.snorlax.utils.customTab

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.text.TextUtils
import androidx.browser.customtabs.CustomTabsService

class CustomTabHelper {

    companion object {
        var sPackageNameToUse: String? = null
        const val STABLE_PACKAGE = "com.android.chrome"
        const val BETA_PACKAGE = "com.chrome.beta"
        const val DEV_PACKAGE = "com.chrome.dev"
        const val LOCAL_PACKAGE = "com.google.android.apps.chrome"
    }

    fun getPackageNameToUse(context: Context, url: String): String? {

        sPackageNameToUse?.let {
            return it
        }

        val pm = context.packageManager

        val activityIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        val defaultViewHandlerInfo = pm.resolveActivity(activityIntent, 0)
        var defaultViewHandlerPackageName: String? = null

        defaultViewHandlerInfo?.let {
            defaultViewHandlerPackageName = it.activityInfo.packageName
        }

        val resolvedActivityList = pm.queryIntentActivities(activityIntent, 0)
        val packagesSupportingCustomTabs = ArrayList<String>()
        for (info in resolvedActivityList) {
            val serviceIntent = Intent()
            serviceIntent.action = CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION
            serviceIntent.setPackage(info.activityInfo.packageName)

            pm.resolveService(serviceIntent, 0)?.let {
                packagesSupportingCustomTabs.add(info.activityInfo.packageName)
            }
        }

        when {
            packagesSupportingCustomTabs.isEmpty() -> sPackageNameToUse = null
            packagesSupportingCustomTabs.size == 1 -> sPackageNameToUse =
                packagesSupportingCustomTabs[0]
            !TextUtils.isEmpty(defaultViewHandlerPackageName)
                    && !hasSpecializedHandlerIntents(context, activityIntent)
                    && packagesSupportingCustomTabs.contains(defaultViewHandlerPackageName) ->
                sPackageNameToUse = defaultViewHandlerPackageName
            packagesSupportingCustomTabs.contains(STABLE_PACKAGE) -> sPackageNameToUse =
                STABLE_PACKAGE
            packagesSupportingCustomTabs.contains(BETA_PACKAGE) -> sPackageNameToUse =
                BETA_PACKAGE
            packagesSupportingCustomTabs.contains(DEV_PACKAGE) -> sPackageNameToUse =
                DEV_PACKAGE
            packagesSupportingCustomTabs.contains(LOCAL_PACKAGE) -> sPackageNameToUse =
                LOCAL_PACKAGE
        }
        return sPackageNameToUse
    }

//    fun hasSpecificApp(context: Context, intent: Intent): Boolean {
//        try {
//            val pm = context.packageManager
//            val handlers = pm.queryIntentActivities(
//                intent,
//                PackageManager.GET_RESOLVED_FILTER
//            )
//            if (han)
//
//
//        } catch (e: java.lang.RuntimeException) {}
//    }

    fun hasSpecializedHandlerIntents(context: Context, intent: Intent): Boolean {
        try {
            val pm = context.packageManager
            val handlers = pm.queryIntentActivities(
                intent,
                PackageManager.GET_RESOLVED_FILTER)
            if (handlers.size == 0) {
                return false
            }
            for (resolveInfo in handlers) {
                val filter = resolveInfo.filter ?: continue
                if (filter.countDataAuthorities() == 0/* || filter.countDataPaths() == 0*/) continue
                if (resolveInfo.activityInfo == null) continue
                return true
            }
        } catch (e: RuntimeException) {
        }
        return false
    }
}