/*
 * Copyright (C) 2024 The Android Open Source Project
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

import android.app.AlertDialog
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class SettingsItemAdapter(private val dataSet: List<SettingsItem>) :
    RecyclerView.Adapter<SettingsItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: MaterialCardView = view.findViewById(R.id.settings_list_item_card)
        val icon: ImageView = view.findViewById(R.id.settings_list_item_icon)
        val title: TextView = view.findViewById(R.id.settings_list_item_title)
        val subTitle: TextView = view.findViewById(R.id.settings_list_item_sub_title)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.settings_list_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.icon.setImageResource(dataSet[position].icon)
        viewHolder.title.text = dataSet[position].title
        viewHolder.subTitle.text = dataSet[position].subTitle

        viewHolder.card.setOnClickListener { view ->
            val type = dataSet[position].settingsItemEnum
            if (type == SettingsItemEnum.WebViewSettingsItem) {
                val webViewManager = WebViewManager.getInstance(view.context)
                val currentUrl = webViewManager.webViewUrl
                val originalSelection = currentUrl
                var newSelection = originalSelection

                val inputUrl = EditText(view.context)
                inputUrl.inputType = InputType.TYPE_CLASS_TEXT
                inputUrl.setText(currentUrl)

                AlertDialog.Builder(view.context)
                    .setTitle(R.string.settings_webview_url_title)
                    .setPositiveButton(android.R.string.ok) { dialog, which ->
                        newSelection = inputUrl.text.toString()
                        if (originalSelection != newSelection) {
                            webViewManager.webViewUrl = newSelection
                            Toast.makeText(
                                    view.context,
                                    R.string.settings_graphics_acceleration_toast_reboot_required,
                                    Toast.LENGTH_SHORT,
                                )
                                .show()
                        }
                    }
                    .setNegativeButton(android.R.string.cancel) { dialog, which ->
                        dialog.dismiss()
                    }
                    .setView(inputUrl)
                    .create()
                    .show()
                return@setOnClickListener
            }
        }
    }

    override fun getItemCount() = dataSet.size
}
