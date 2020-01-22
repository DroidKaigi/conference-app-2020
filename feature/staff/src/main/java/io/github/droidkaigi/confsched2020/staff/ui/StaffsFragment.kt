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
import com.dropbox.android.external.store4.MemoryPolicy
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreBuilder
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Component
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2020.App
import io.github.droidkaigi.confsched2020.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2020.data.api.response.StaffResponse
import io.github.droidkaigi.confsched2020.data.db.StaffDatabase
import io.github.droidkaigi.confsched2020.data.db.entity.StaffEntity
import io.github.droidkaigi.confsched2020.di.AppComponent
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import io.github.droidkaigi.confsched2020.model.Staff
import io.github.droidkaigi.confsched2020.model.StaffContents
import io.github.droidkaigi.confsched2020.staff.R
import io.github.droidkaigi.confsched2020.staff.databinding.FragmentStaffsBinding
import io.github.droidkaigi.confsched2020.staff.ui.di.StaffAssistedInjectModule
import io.github.droidkaigi.confsched2020.staff.ui.item.StaffItem
import io.github.droidkaigi.confsched2020.staff.ui.viewmodel.StaffsViewModel
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel
import io.github.droidkaigi.confsched2020.util.ProgressTimeLatch
import io.github.droidkaigi.confsched2020.util.autoCleared
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Provider

class StaffsFragment : Fragment() {

    private var binding: FragmentStaffsBinding by autoCleared()

    @Inject
    lateinit var staffsFactory: Provider<StaffsViewModel>
    private val staffsViewModel by assistedViewModels {
        staffsFactory.get()
    }

    @Inject
    lateinit var systemViewModelFactory: Provider<SystemViewModel>
    private val systemViewModel by assistedViewModels {
        systemViewModelFactory.get()
    }

    @Inject
    lateinit var staffItemFactory: StaffItem.Factory

    private var progressTimeLatch: ProgressTimeLatch by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_staffs,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        staffsViewModel.uiModel.observe(viewLifecycleOwner) { uiModel ->
            progressTimeLatch.loading = uiModel.isLoading
            groupAdapter.update(uiModel.staffContents.staffs.map {
                staffItemFactory.create(it)
            })

            uiModel.error?.let {
                systemViewModel.onError(it)
            }
        }
    }
}

@Module
class StaffModule(private val fragment: StaffsFragment) {
    @PageScope
    @Provides
    fun providesLifecycleOwnerLiveData(): LiveData<LifecycleOwner> {
        return fragment.viewLifecycleOwnerLiveData
    }

    // Add here for convenience
    @FlowPreview
    @Provides
    fun provideStaffContentsStore(
        api: DroidKaigiApi,
        staffDatabase: StaffDatabase
    ): Store<Unit, StaffContents> {
        return StoreBuilder.fromNonFlow<Unit, StaffResponse> { api.getStaffs() }
            .persister(
                reader = { readFromLocal(staffDatabase) },
                writer = { _: Unit, output: StaffResponse -> staffDatabase.save(output) }
            )
            .cachePolicy(MemoryPolicy.builder().build())
            .build()
    }
    private fun readFromLocal(staffDatabase: StaffDatabase) = staffDatabase
        .staffs()
        .map { StaffContents(it.map { staffEntity -> staffEntity.toStaff() }) }

    private fun StaffEntity.toStaff(): Staff = Staff(id, name, iconUrl, profileUrl)
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
