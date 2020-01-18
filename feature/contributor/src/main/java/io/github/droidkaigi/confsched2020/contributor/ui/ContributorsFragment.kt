package io.github.droidkaigi.confsched2020.contributor.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Component
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2020.App
import io.github.droidkaigi.confsched2020.contributor.R
import io.github.droidkaigi.confsched2020.contributor.databinding.FragmentContributorsBinding
import io.github.droidkaigi.confsched2020.contributor.ui.di.ContributorAssistedInjectModule
import io.github.droidkaigi.confsched2020.contributor.ui.viewmodel.ContributorsViewModel
import io.github.droidkaigi.confsched2020.di.AppComponent
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import io.github.droidkaigi.confsched2020.util.ProgressTimeLatch
import io.github.droidkaigi.confsched2020.util.autoCleared
import javax.inject.Inject
import javax.inject.Provider

class ContributorsFragment : Fragment(R.layout.fragment_contributors) {

    private var binding: FragmentContributorsBinding by autoCleared()

    @Inject lateinit var contributorsFactory: Provider<ContributorsViewModel>
    private val contributorsViewModel by assistedViewModels {
        contributorsFactory.get()
    }

    private var progressTimeLatch: ProgressTimeLatch by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentContributorsBinding.bind(view)

        val appComponent = (requireContext().applicationContext as App).appComponent
        val component = DaggerContributorComponent.factory()
            .create(appComponent, ContributorModule(this))
        component.inject(this)

        val groupAdapter = GroupAdapter<ViewHolder<*>>()
        binding.contributorRecycler.adapter = groupAdapter

        progressTimeLatch = ProgressTimeLatch { showProgress ->
            binding.progressBar.isVisible = showProgress
        }.apply {
            loading = true
        }
        // TODO Implement
//        contributorsViewModel.uiModel.observe(viewLifecycleOwner) { uiModel ->
//
//        }
    }
}

@Module
class ContributorModule(private val fragment: ContributorsFragment) {
    @PageScope @Provides
    fun providesLifecycleOwnerLiveData(): LiveData<LifecycleOwner> {
        return fragment.viewLifecycleOwnerLiveData
    }
}

@PageScope
@Component(
    modules = [
        ContributorModule::class,
        ContributorAssistedInjectModule::class
    ],
    dependencies = [AppComponent::class]
)
interface ContributorComponent {
    @Component.Factory
    interface Factory {
        fun create(
            appComponent: AppComponent,
            contributorModule: ContributorModule
        ): ContributorComponent
    }

    fun inject(fragment: ContributorsFragment)
}
