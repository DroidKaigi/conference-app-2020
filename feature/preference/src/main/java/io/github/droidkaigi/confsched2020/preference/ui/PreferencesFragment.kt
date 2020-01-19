package io.github.droidkaigi.confsched2020.preference.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dagger.Component
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2020.App
import io.github.droidkaigi.confsched2020.di.AppComponent
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.preference.R

class PreferencesFragment : PreferenceFragmentCompat() {

    private val darkThemeSwitchChangeListener =
        Preference.OnPreferenceChangeListener { _, newValue ->
            AppCompatDelegate.setDefaultNightMode(
                if (newValue as Boolean) {
                    MODE_NIGHT_YES
                } else {
                    MODE_NIGHT_NO
                }
            )
            (activity as? AppCompatActivity)?.delegate?.applyDayNight()
            return@OnPreferenceChangeListener true
        }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting, rootKey)

        preferenceManager?.findPreference<SwitchPreferenceCompat>(SWITCH_DARK_THEME_KEY)?.also {
            val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            it.isChecked = currentNightMode == Configuration.UI_MODE_NIGHT_YES
            it.onPreferenceChangeListener = darkThemeSwitchChangeListener
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appComponent = (requireContext().applicationContext as App).appComponent
        val component = DaggerPreferenceComponent.factory()
            .create(appComponent, PreferenceModule(this))
        component.inject(this)
    }

    companion object {
        private const val SWITCH_DARK_THEME_KEY = "switchDarkTheme"
    }
}

@Module
class PreferenceModule(private val fragment: PreferencesFragment) {
    @PageScope @Provides
    fun providesLifecycleOwnerLiveData(): LiveData<LifecycleOwner> {
        return fragment.viewLifecycleOwnerLiveData
    }
}

@PageScope
@Component(
    modules = [
        PreferenceModule::class
    ],
    dependencies = [AppComponent::class]
)
interface PreferenceComponent {
    @Component.Factory
    interface Factory {
        fun create(
            appComponent: AppComponent,
            preferenceModule: PreferenceModule
        ): PreferenceComponent
    }

    fun inject(fragment: PreferencesFragment)
}
