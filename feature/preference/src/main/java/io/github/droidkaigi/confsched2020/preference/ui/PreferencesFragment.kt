package io.github.droidkaigi.confsched2020.preference.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceFragmentCompat
import dagger.Component
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2020.App
import io.github.droidkaigi.confsched2020.di.AppComponent
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.util.ProgressTimeLatch

class PreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//        setPreferencesFromResource()
    }

    private lateinit var progressTimeLatch: ProgressTimeLatch

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appComponent = (requireContext().applicationContext as App).appComponent
        val component = DaggerPreferenceComponent.builder()
            .appComponent(appComponent)
            .preferenceModule(PreferenceModule(this))
            .build()
        component.inject(this)
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
    @Component.Builder
    interface Builder {
        fun build(): PreferenceComponent
        fun appComponent(appComponent: AppComponent): Builder
        fun preferenceModule(preferenceModule: PreferenceModule): Builder
    }

    fun inject(fragment: PreferencesFragment)
}
