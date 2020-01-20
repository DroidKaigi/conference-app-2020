package io.github.droidkaigi.confsched2020.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.util.Patterns
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.withStyledAttributes
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import io.github.droidkaigi.confsched2020.ext.getThemeColor
import io.github.droidkaigi.confsched2020.widget.component.R
import timber.log.Timber
import timber.log.debug

@Navigator.Name("chrome")
class ChromeCustomTabsNavigator(private val context: Context) : Navigator<ChromeCustomTabsNavigator.Destination>() {

    override fun createDestination() = Destination(this)

    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ): NavDestination? {
        val url = args?.getString("url")
            ?: throw IllegalStateException("Destination ${destination.id} does not have an url.")

        if (destination.webUrl && url.isInvalidWebUrl()) {
            throw IllegalArgumentException("Url($url) is a invalid web URL.")
        }

        val builder = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .enableUrlBarHiding()
            .setToolbarColor(context.getThemeColor(R.attr.colorAccent))

        val intent = builder.build()
        try {
            intent.launchUrl(context, Uri.parse(url))
        } catch (e: ActivityNotFoundException) {
            Timber.debug(e) { "Fail ChromeCustomTabsNavigator. launchUrl()" }
        }
        return null // Do not add to the back stack, managed by Chrome Custom Tabs
    }

    private fun String.isInvalidWebUrl(): Boolean {
        return Patterns.WEB_URL.matcher(this).matches().not()
    }

    override fun popBackStack() = true // Managed by Chrome Custom Tabs

    @NavDestination.ClassType(Activity::class)
    class Destination(navigator: Navigator<out NavDestination>) : NavDestination(navigator) {

        var webUrl: Boolean = true

        override fun onInflate(context: Context, attrs: AttributeSet) {
            super.onInflate(context, attrs)

            context.withStyledAttributes(attrs, R.styleable.ChromeCustomTabsNavigator, 0, 0) {
                webUrl = getBoolean(R.styleable.ChromeCustomTabsNavigator_webUrl, true)
            }
        }
    }
}
