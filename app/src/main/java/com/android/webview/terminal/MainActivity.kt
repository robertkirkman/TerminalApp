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

import android.content.Intent
import android.content.res.Configuration
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.ConditionVariable
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.RelativeLayout
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity :
    BaseActivity(),
    AccessibilityManager.AccessibilityStateChangeListener {
    var displayMenu: Button? = null
    var tabAddButton: Button? = null
    val bootCompleted = ConditionVariable()
    lateinit var modifierKeysController: ModifierKeysController
    private lateinit var tabScrollView: HorizontalScrollView
    private lateinit var executorService: ExecutorService
    private lateinit var accessibilityManager: AccessibilityManager
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var terminalTabAdapter: TerminalTabAdapter
    private val terminalViewModel: TerminalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeUi()

        accessibilityManager =
            getSystemService(AccessibilityManager::class.java)
        accessibilityManager.addAccessibilityStateChangeListener(this)

        executorService =
            Executors.newSingleThreadExecutor(TerminalThreadFactory(applicationContext))
    }

    private fun initializeUi() {
        setContentView(R.layout.activity_headless)
        tabLayout = findViewById(R.id.tab_layout)
        displayMenu = findViewById(R.id.display_button)
        tabAddButton = findViewById(R.id.tab_add_button)
        tabScrollView = findViewById(R.id.tab_scrollview)
        val modifierKeysContainerView =
            findViewById<RelativeLayout>(R.id.modifier_keys_container) as ViewGroup

        findViewById<Button>(R.id.settings_button).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            this.startActivity(intent)
        }

        displayMenu?.also {
            it.visibility = View.VISIBLE
            it.isEnabled = false
            it.setOnClickListener {
                val intent = Intent(this, DisplayActivity::class.java)
                intent.flags = intent.flags or Intent.FLAG_ACTIVITY_CLEAR_TASK
                this.startActivity(intent)
            }
        }

        modifierKeysController = ModifierKeysController(this, modifierKeysContainerView)

        terminalTabAdapter = TerminalTabAdapter(this)
        viewPager = findViewById(R.id.pager)
        viewPager.adapter = terminalTabAdapter
        viewPager.isUserInputEnabled = false
        viewPager.offscreenPageLimit = 2

        TabLayoutMediator(tabLayout, viewPager, false, false) { _: TabLayout.Tab?, _: Int -> }
            .attach()

        tabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.position?.let {
                        terminalViewModel.selectedTabViewId = terminalTabAdapter.tabs[it].id
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            }
        )

        addTerminalTab()

        tabAddButton?.setOnClickListener { addTerminalTab() }
    }

    private fun addTerminalTab() {
        val tab = tabLayout.newTab()
        tab.setCustomView(R.layout.tabitem_terminal)
        viewPager.offscreenPageLimit += 1
        val tabId = terminalTabAdapter.addTab()
        terminalViewModel.selectedTabViewId = tabId
        terminalViewModel.terminalTabs[tabId] = tab
        tab.customView!!
            .findViewById<Button>(R.id.tab_close_button)
            .setOnClickListener { _: View? -> closeTab(tab) }
        // Add and select the tab
        tabLayout.addTab(tab, true)
    }

    fun closeTab(tab: TabLayout.Tab) {
        if (terminalTabAdapter.tabs.size == 1) {
            finish()
        }
        viewPager.offscreenPageLimit -= 1
        terminalTabAdapter.deleteTab(tab.position)
        tabLayout.removeTab(tab)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        modifierKeysController.update()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_UNKNOWN) {
            if (event.action == KeyEvent.ACTION_UP) {
                ErrorActivity.start(this, Exception("Debug: KeyEvent.KEYCODE_UNKNOWN"))
            }
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onPause() {
        super.onPause()
        MediaScannerConnection.scanFile(
            this,
            arrayOf("/storage/emulated/0/Download"),
            null /* mimeTypes */,
            null, /* callback */
        )
    }

    override fun onDestroy() {
        executorService.shutdown()
        getSystemService(AccessibilityManager::class.java)
            .removeAccessibilityStateChangeListener(this)
        finishAndRemoveTask()
        super.onDestroy()
    }

    override fun onAccessibilityStateChanged(p0: Boolean) {
    }

    companion object {
        const val TAG: String = "WebViewTerminalApp"
    }
}
