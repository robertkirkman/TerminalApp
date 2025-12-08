/*
 * Copyright (C) 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.webview.terminal

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class WebViewManager
private constructor(private val sharedPref: SharedPreferences) {
    private val lock = Any()

    var webViewUrl: String
        get() =
            synchronized(lock) {
                val url = sharedPref.getString(URL_KEY, null)
                val defaultOption = "http://127.0.0.1:7681"
                return try {
                    url ?: defaultOption
                } catch (_: IllegalArgumentException) {
                    defaultOption
                }
            }
        set(value) =
            synchronized(lock) {
                sharedPref.edit { putString(URL_KEY, value) }
            }

    companion object {
        private const val PREFS_NAME = ".WEBVIEW"
        private const val URL_KEY = "url"

        @Volatile private var instance: WebViewManager? = null

        @Synchronized
        fun getInstance(context: Context): WebViewManager {
            // Use double-checked locking for thread safety.
            return instance
                ?: synchronized(this) {
                    instance
                        ?: run {
                            val sharedPref =
                                context.getSharedPreferences(
                                    context.packageName + PREFS_NAME,
                                    Context.MODE_PRIVATE,
                                )
                            WebViewManager(sharedPref).also { instance = it }
                        }
                }
        }
    }
}
