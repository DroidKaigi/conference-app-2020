package io.github.droidkaigi.confsched2020.preference.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dagger.Component
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2020.App
import io.github.droidkaigi.confsched2020.R as MainR
import io.github.droidkaigi.confsched2020.di.AppComponent
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import io.github.droidkaigi.confsched2020.model.NightMode
import io.github.droidkaigi.confsched2020.preference.R
import io.github.droidkaigi.confsched2020.preference.ui.di.PreferenceAssistedInjectModule
import io.github.droidkaigi.confsched2020.preference.ui.viewmodel.PreferenceViewModel
import javax.inject.Inject
import javax.inject.Provider

class PreferencesFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var preferenceModelFactory: Provider<PreferenceViewModel>
    private val preferenceViewModel by assistedViewModels {
        preferenceModelFactory.get()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appComponent = (requireContext().applicationContext as App).appComponent
        val component = DaggerPreferenceComponent.factory()
            .create(appComponent, PreferenceModule(this))
        component.inject(this)

        preferenceManager?.findPreference<ListPreference>(DARK_THEME_KEY)?.also {
            preferenceViewModel.setNightMode(it.value.toNightMode())
            it.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                preferenceViewModel.setNightMode((newValue as String).toNightMode())
                return@OnPreferenceChangeListener true
            }
        }

        preferenceViewModel.uiModel.observe(viewLifecycleOwner) { uiModel ->
            AppCompatDelegate.setDefaultNightMode(uiModel.nightMode.platformValue)
            (activity as? AppCompatActivity)?.delegate?.applyDayNight()
        }
    }

    // region temporary functions until appropriate structure have built
    private fun String.toNightMode() = when (this) {
        getString(MainR.string.pref_theme_value_dark) -> NightMode.YES
        getString(MainR.string.pref_theme_value_light) -> NightMode.NO
        getString(MainR.string.pref_theme_value_default) -> NightMode.SYSTEM
        else -> throw IllegalArgumentException("should not happen")
    }

    private val NightMode.platformValue: Int
        get() = when (this) {
            NightMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            NightMode.YES -> AppCompatDelegate.MODE_NIGHT_YES
            NightMode.NO -> AppCompatDelegate.MODE_NIGHT_NO
        }
    // endregion

    companion object {
        private const val DARK_THEME_KEY = "darkTheme"
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
        PreferenceModule::class,
        PreferenceAssistedInjectModule::class
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
