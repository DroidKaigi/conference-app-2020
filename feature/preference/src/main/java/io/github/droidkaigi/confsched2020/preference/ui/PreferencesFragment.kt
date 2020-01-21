package io.github.droidkaigi.confsched2020.preference.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dagger.Component
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2020.App
import io.github.droidkaigi.confsched2020.di.AppComponent
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
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

        preferenceManager?.findPreference<SwitchPreferenceCompat>(SWITCH_DARK_THEME_KEY)?.also {
            preferenceViewModel.setNightMode(it.isChecked)
            it.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                preferenceViewModel.setNightMode(newValue as Boolean)
                return@OnPreferenceChangeListener true
            }
        }

        preferenceViewModel.uiModel.observe(viewLifecycleOwner) { uiModel ->
            AppCompatDelegate.setDefaultNightMode(
                if (uiModel.isNightMode) {
                    MODE_NIGHT_YES
                } else {
                    MODE_NIGHT_NO
                }
            )
            (activity as? AppCompatActivity)?.delegate?.applyDayNight()
        }
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
