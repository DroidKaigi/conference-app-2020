package io.github.droidkaigi.confsched2020.ui.widget

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.droidkaigi.confsched2020.ext.isNightMode

class SystemUiManager(
    val context: Context
) {
    private val _systemUiVisibility = MutableLiveData(0)
    val systemUiVisibility: LiveData<Int> = _systemUiVisibility

    private val _statusBarColor =
        MutableLiveData(COLOR_STATUS_BAR_INVISIBLE)
    val statusBarColor: LiveData<Int> = _statusBarColor

    private val _navigationBarColor =
        MutableLiveData(COLOR_NAVIGATION_BAR_INVISIBLE)
    val navigationBarColor: LiveData<Int> = _navigationBarColor

    var drawerSlideOffset: Float = 0f
        set(value) {
            if (field != value) {
                field = value
                updateColors()
            }
        }
    private val drawerIsOpened: Boolean
        get() = drawerSlideOffset > 0f

    var isIndigoBackground: Boolean? = null
        set(value) {
            if (field != value) {
                field = value
                updateColors()
            }
        }

    private fun updateColors() {
        if (23 <= Build.VERSION.SDK_INT) {
            updateColorsM()
        } else {
            updateColorsPreM()
        }
    }

    @TargetApi(23)
    private fun updateColorsM() {
        // Matrix: icon color / bar visibility
        // | indigo background | Drawer closed   | Drawer opened |
        // | ----------------- | --------------- | ------------- |
        // | Yes               | White/Invisible | White/Visible   |
        // | No                | Black/Invisible | Black/Invisible |

        // Icon color: change based on theme with View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        _systemUiVisibility.value = if (isIndigoBackground == true || context.isNightMode()) {
            0
        } else {
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        // Status bar color
        _statusBarColor.value =
            if ((isIndigoBackground == true || context.isNightMode()) && drawerIsOpened) {
                COLOR_STATUS_BAR_VISIBLE
            } else {
                COLOR_STATUS_BAR_INVISIBLE
            }

        // Navigation bar color
        _navigationBarColor.value =
            if (context.isNightMode()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    COLOR_NAVIGATION_BAR_INVISIBLE
                } else {
                    COLOR_NAVIGATION_BAR_VISIBLE
                }
            } else {
                COLOR_NAVIGATION_BAR_INVISIBLE
            }
    }

    private fun updateColorsPreM() {
        // Matrix: icon color / bar visibility
        // | indigo background | Drawer closed   | Drawer opened |
        // | ----------------- | --------------- | ------------- |
        // | Yes               | White/Invisible | White/Visible |
        // | No                | White/Visible   | White/Visible |

        // Icon color: can not change. Always white
        _systemUiVisibility.value = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        // Status bar color
        _statusBarColor.value =
            if (!(isIndigoBackground == true || context.isNightMode()) || drawerIsOpened) {
                COLOR_STATUS_BAR_VISIBLE
            } else {
                COLOR_STATUS_BAR_INVISIBLE
            }

        // Navigation bar color
        _navigationBarColor.value =
            if (context.isNightMode()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    COLOR_NAVIGATION_BAR_INVISIBLE
                } else {
                    COLOR_NAVIGATION_BAR_VISIBLE
                }
            } else {
                COLOR_NAVIGATION_BAR_INVISIBLE
            }
    }

    companion object {
        private const val COLOR_STATUS_BAR_INVISIBLE = Color.TRANSPARENT
        private const val COLOR_STATUS_BAR_VISIBLE = 0x8a000000.toInt()
        private const val COLOR_NAVIGATION_BAR_INVISIBLE = Color.TRANSPARENT
        private const val COLOR_NAVIGATION_BAR_VISIBLE = 0x4DFFFFFF
    }
}
