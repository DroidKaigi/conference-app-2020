package io.github.droidkaigi.confsched2020.contributor.ui

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
import com.google.android.material.snackbar.Snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.GroupieViewHolder
import dagger.Component
import dagger.Module
import dagger.Provides
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import io.github.droidkaigi.confsched2020.App
import io.github.droidkaigi.confsched2020.contributor.R
import io.github.droidkaigi.confsched2020.contributor.databinding.FragmentContributorsBinding
import io.github.droidkaigi.confsched2020.contributor.ui.di.ContributorAssistedInjectModule
import io.github.droidkaigi.confsched2020.contributor.ui.item.ContributorItem
import io.github.droidkaigi.confsched2020.contributor.ui.viewmodel.ContributorsViewModel
import io.github.droidkaigi.confsched2020.di.AppComponent
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import io.github.droidkaigi.confsched2020.ext.isShow
import io.github.droidkaigi.confsched2020.ext.stringRes
import io.github.droidkaigi.confsched2020.model.AppError
import io.github.droidkaigi.confsched2020.model.Contributor
import io.github.droidkaigi.confsched2020.ui.transition.Stagger
import javax.inject.Inject
import javax.inject.Provider

class ContributorsFragment : Fragment(R.layout.fragment_contributors) {

    @Inject lateinit var contributorsFactory: Provider<ContributorsViewModel>
    private val contributorsViewModel by assistedViewModels {
        contributorsFactory.get()
    }
    @Inject lateinit var contributorItemFactory: ContributorItem.Factory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inject()
        val binding = FragmentContributorsBinding.bind(view)

        val groupAdapter = GroupAdapter<GroupieViewHolder<*>>()
        binding.contributorRecycler.adapter = groupAdapter
        binding.contributorRecycler.doOnApplyWindowInsets { recyclerView, insets, initialState ->
            // Set a bottom padding due to the system UI is enabled.
            recyclerView.updatePadding(
                bottom = insets.systemWindowInsetBottom + initialState.paddings.bottom
            )
        }
        // Because custom RecyclerView's animation, set custom SimpleItemAnimator implementation.
        //
        // see https://developer.android.com/reference/androidx/recyclerview/widget/SimpleItemAnimator.html#animateAdd(androidx.recyclerview.widget.RecyclerView.ViewHolder)
        // see https://github.com/android/animation-samples/blob/232709094f9c60e0ead9cf4873e0c1549a9a8505/Motion/app/src/main/java/com/example/android/motion/demo/stagger/StaggerActivity.kt#L61
        binding.contributorRecycler.itemAnimator = object : DefaultItemAnimator() {
            override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
                dispatchAddFinished(holder)
                dispatchAddStarting(holder)
                return false
            }
        }

        binding.progressBar.show()
        binding.retryButton.setOnClickListener {
            contributorsViewModel.onRetry()
        }

        // This is the transition for the stagger effect.
        val stagger = Stagger()
        contributorsViewModel.uiModel.observe(viewLifecycleOwner) { uiModel ->
            binding.progressBar.isShow = uiModel.isLoading

            // Delay the stagger effect until the list is updated.
            TransitionManager.beginDelayedTransition(binding.contributorRecycler, stagger)
            groupAdapter.update(uiModel.contributors.toItems())
            binding.retryButton.visibility =
                if (uiModel.error != null && uiModel.contributors.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            uiModel.error?.let {
                showErrorSnackbar(
                    binding.contributor,
                    it,
                    binding.retryButton.visibility != View.VISIBLE
                )
            }
        }
    }

    private fun List<Contributor>.toItems() =
        map {
            contributorItemFactory.create(it)
        }

    private fun showErrorSnackbar(view: View, appError: AppError, showRetryAction: Boolean) {
        Snackbar.make(
            view,
            appError.stringRes(),
            Snackbar.LENGTH_LONG
        ).apply {
            if (showRetryAction) {
                setAction(R.string.retry_label) {
                    contributorsViewModel.onRetry()
                }
            }
        }.show()
    }

    private fun inject() {
        val appComponent = (requireContext().applicationContext as App).appComponent
        val component = DaggerContributorComponent.factory()
            .create(appComponent, ContributorModule(this))
        component.inject(this)
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
