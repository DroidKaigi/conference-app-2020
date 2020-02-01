package io.github.droidkaigi.confsched2020.staff.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.GroupieViewHolder
import dagger.Component
import dagger.Module
import dagger.Provides
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import io.github.droidkaigi.confsched2020.App
import io.github.droidkaigi.confsched2020.di.AppComponent
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import io.github.droidkaigi.confsched2020.ext.isShow
import io.github.droidkaigi.confsched2020.staff.R
import io.github.droidkaigi.confsched2020.staff.databinding.FragmentStaffsBinding
import io.github.droidkaigi.confsched2020.staff.ui.di.StaffAssistedInjectModule
import io.github.droidkaigi.confsched2020.staff.ui.item.StaffItem
import io.github.droidkaigi.confsched2020.staff.ui.viewmodel.StaffsViewModel
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel
import io.github.droidkaigi.confsched2020.ui.transition.Stagger
import javax.inject.Inject
import javax.inject.Provider

class StaffsFragment : Fragment(R.layout.fragment_staffs) {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentStaffsBinding.bind(view)

        val appComponent = (requireContext().applicationContext as App).appComponent
        val component = DaggerStaffComponent.factory()
            .create(appComponent, StaffModule(this))
        component.inject(this)

        val groupAdapter = GroupAdapter<GroupieViewHolder<*>>()
        binding.staffRecycler.adapter = groupAdapter
        binding.staffRecycler.doOnApplyWindowInsets { recyclerView, insets, initialState ->
            // Set a bottom padding due to the system UI is enabled.
            recyclerView.updatePadding(
                bottom = insets.systemWindowInsetBottom + initialState.paddings.bottom
            )
        }
        // Because custom RecyclerView's animation, set custom SimpleItemAnimator implementation.
        //
        // see https://developer.android.com/reference/androidx/recyclerview/widget/SimpleItemAnimator.html#animateAdd(androidx.recyclerview.widget.RecyclerView.ViewHolder)
        // see https://github.com/android/animation-samples/blob/232709094f9c60e0ead9cf4873e0c1549a9a8505/Motion/app/src/main/java/com/example/android/motion/demo/stagger/StaggerActivity.kt#L61
        binding.staffRecycler.itemAnimator = object : DefaultItemAnimator() {
            override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
                dispatchAddFinished(holder)
                dispatchAddStarting(holder)
                return false
            }
        }

        binding.progressBar.show()

        // This is the transition for the stagger effect.
        val stagger = Stagger()
        staffsViewModel.uiModel.observe(viewLifecycleOwner) { uiModel ->
            binding.progressBar.isShow = uiModel.isLoading

            // Delay the stagger effect until the list is updated.
            TransitionManager.beginDelayedTransition(binding.staffRecycler, stagger)
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
