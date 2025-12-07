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

import android.os.Bundle
import android.view.SurfaceView
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.button.MaterialButton

class DisplayActivity : BaseActivity() {
    private lateinit var mainView: DisplaySurfaceView
    private lateinit var cursorView: SurfaceView
    private lateinit var pipButton: Button
    private lateinit var fullscreenButton: MaterialButton
    private lateinit var keyboardButton: MaterialButton
    private lateinit var modifierKeysButton: MaterialButton
    private lateinit var modifierKeysContainer: ViewGroup
    private lateinit var mouseLockButton: MaterialButton
    private lateinit var displayFlow: androidx.constraintlayout.helper.widget.Flow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)
        initializeViews()
    }

    private fun initializeViews() {
        mainView = findViewById(R.id.surface_view)
        cursorView = findViewById(R.id.cursor_surface_view)
        pipButton = findViewById(R.id.pip_button)
        fullscreenButton = findViewById(R.id.fullscreen_button)
        keyboardButton = findViewById(R.id.keyboard_button)
        modifierKeysButton = findViewById(R.id.modifier_keys_button)
        modifierKeysContainer = findViewById(R.id.display_activity_modifier_keys_container)
        mouseLockButton = findViewById(R.id.mouse_lock_button)
        displayFlow = findViewById(R.id.display_flow)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
    }
}
