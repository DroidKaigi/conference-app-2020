package io.github.droidkaigi.confsched2020.ui.widget

import android.content.res.Resources
import android.os.Build
import io.github.droidkaigi.confsched2020.widget.component.R

class BottomGestureSpace(private val resources: Resources) {
    val gestureSpaceSize by lazy {
        if (resources.isEdgeToEdgeEnabled())
            resources.getDimension(R.dimen.gesture_navigation_bottom_space).toInt()
        else 0
    }

    /**
     * judge gesture navigation is enabled
     * https://android.googlesource.com/platform/packages/apps/Settings.git/+/refs/heads/master/src/com/android/settings/gestures/SystemNavigationPreferenceController.java#97
     *
     * If configNavBarInteractionMode is equal to "2", it means gesture navigation
     * https://android.googlesource.com/platform/frameworks/base/+/refs/heads/android10-mainline-release/core/java/android/view/WindowManagerPolicyConstants.java#60
     * */
    private fun Resources.isEdgeToEdgeEnabled(): Boolean {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) return false
        val configNavBarInteractionMode = Resources.getSystem().getIdentifier(
            "config_navBarInteractionMode",
            "integer",
            "android"
        )
        return getInteger(configNavBarInteractionMode) == 2
    }
}
