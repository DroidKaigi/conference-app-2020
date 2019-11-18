package io.github.droidkaigi.confsched2020.system.ui.viewmodel

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.droidkaigi.confsched2020.ext.getThemeColor
import io.github.droidkaigi.confsched2020.ext.toNonNullSingleEvent
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.system.R
import javax.inject.Inject

class SystemViewModel @Inject constructor(
    val activity: FragmentActivity
) : ViewModel() {
    private val mutableErrorLiveData = MutableLiveData<AppError?>()
    val errorLiveData: LiveData<AppError> get() = mutableErrorLiveData.toNonNullSingleEvent()
    fun onError(error: AppError) {
        mutableErrorLiveData.value = error
    }

    fun openUrl(url: String) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .enableUrlBarHiding()
            .setToolbarColor(activity.getThemeColor(R.attr.colorAccent))
            .build()

        // block to multiple launch a Activity
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // launch a Custom Tabs Activity
        customTabsIntent.launchUrl(activity, Uri.parse(url))
    }
}
