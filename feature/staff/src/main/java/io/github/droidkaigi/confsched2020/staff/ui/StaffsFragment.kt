package io.github.droidkaigi.confsched2020.staff.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Component
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2020.App
import io.github.droidkaigi.confsched2020.di.AppComponent
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import io.github.droidkaigi.confsched2020.model.LoadState
import io.github.droidkaigi.confsched2020.staff.R
import io.github.droidkaigi.confsched2020.staff.databinding.FragmentStaffsBinding
import io.github.droidkaigi.confsched2020.staff.ui.di.StaffAssistedInjectModule
import io.github.droidkaigi.confsched2020.staff.ui.item.StaffItem
import io.github.droidkaigi.confsched2020.staff.ui.viewmodel.StaffsViewModel
import io.github.droidkaigi.confsched2020.util.ProgressTimeLatch
import io.github.droidkaigi.confsched2020.util.autoCleared
import javax.inject.Inject
import javax.inject.Provider

class StaffsFragment : Fragment(R.layout.fragment_staffs) {

    private var binding: FragmentStaffsBinding by autoCleared()

    @Inject lateinit var staffsFactory: Provider<StaffsViewModel>
    private val staffsViewModel by assistedViewModels {
        staffsFactory.get()
    }

    @Inject lateinit var staffItemFactory: StaffItem.Factory

    private var progressTimeLatch: ProgressTimeLatch by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentStaffsBinding.bind(view)

        val appComponent = (requireContext().applicationContext as App).appComponent
        val component = DaggerStaffComponent.factory()
            .create(appComponent, StaffModule(this))
        component.inject(this)

        val groupAdapter = GroupAdapter<ViewHolder<*>>()
        binding.staffRecycler.adapter = groupAdapter

        progressTimeLatch = ProgressTimeLatch { showProgress ->
            binding.progressBar.isVisible = showProgress
        }.apply {
            loading = true
        }
        staffsViewModel.staffContentsLoadState.observe(viewLifecycleOwner) { state ->
            progressTimeLatch.loading = state.isLoading
            when (state) {
                is LoadState.Loaded -> {
                    groupAdapter.update(state.value.staffs.map {
                        staffItemFactory.create(it)
                    })
                }
                LoadState.Loading -> Unit
                is LoadState.Error -> {
                    state.e.printStackTrace()
                }
            }
        }
    }
}

@Module
class StaffModule(private val fragment: StaffsFragment) {
    @PageScope @Provides
    fun providesLifecycleOwnerLiveData(): LiveData<LifecycleOwner> {
        return fragment.viewLifecycleOwnerLiveData
    }
}

@PageScope
@Component(
    modules = [
        StaffModule::class,
        StaffAssistedInjectModule::class
    ],
    dependencies = [AppComponent::class]
)
interface StaffComponent {
    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent, staffModule: StaffModule): StaffComponent
    }

    fun inject(fragment: StaffsFragment)
}
