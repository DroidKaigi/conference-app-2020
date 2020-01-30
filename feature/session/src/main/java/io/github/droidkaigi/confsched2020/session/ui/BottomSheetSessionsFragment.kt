package io.github.droidkaigi.confsched2020.session.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import io.github.droidkaigi.confsched2020.di.Injectable
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedActivityViewModels
import io.github.droidkaigi.confsched2020.model.ExpandFilterState
import io.github.droidkaigi.confsched2020.model.SessionPage
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.FragmentBottomSheetSessionsBinding
import io.github.droidkaigi.confsched2020.session.ui.item.SessionItem
import io.github.droidkaigi.confsched2020.session.ui.viewmodel.SessionTabViewModel
import io.github.droidkaigi.confsched2020.session.ui.viewmodel.SessionsViewModel
import io.github.droidkaigi.confsched2020.session.ui.widget.SessionsItemDecoration
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel
import javax.inject.Inject
import javax.inject.Provider

class BottomSheetSessionsFragment : Fragment(R.layout.fragment_bottom_sheet_sessions), Injectable {

    @Inject
    lateinit var sessionsViewModelProvider: Provider<SessionsViewModel>
    private val sessionsViewModel: SessionsViewModel by assistedActivityViewModels {
        sessionsViewModelProvider.get()
    }
    @Inject
    lateinit var sessionTabViewModelProvider: Provider<SessionTabViewModel>
    private val sessionTabViewModel: SessionTabViewModel by assistedActivityViewModels({
        args.page.title
    }) {
        sessionTabViewModelProvider.get()
    }

    @Inject
    lateinit var systemViewModelProvider: Provider<SystemViewModel>
    private val systemViewModel: SystemViewModel by assistedActivityViewModels {
        systemViewModelProvider.get()
    }

    @Inject
    lateinit var sessionItemFactory: SessionItem.Factory
    private val args: BottomSheetSessionsFragmentArgs by lazy {
        BottomSheetSessionsFragmentArgs.fromBundle(arguments ?: Bundle())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentBottomSheetSessionsBinding.bind(view)
        binding.isEmptyFavoritePage = false
        val groupAdapter = GroupAdapter<ViewHolder<*>>()
        binding.sessionRecycler.adapter = groupAdapter
        binding.sessionRecycler.addItemDecoration(
            SessionsItemDecoration(
                groupAdapter,
                requireContext()
            )
        )
        binding.sessionRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val isVisibleShadow =
                    binding.sessionRecycler.canScrollVertically(-1)

                binding.divider.isVisible = !isVisibleShadow
                binding.dividerShadow.isVisible = isVisibleShadow
            }
        })
        binding.startFilter.setOnClickListener {
            sessionTabViewModel.toggleExpand()
        }
        binding.expandLess.setOnClickListener {
            sessionTabViewModel.toggleExpand()
        }
        binding.sessionRecycler.doOnApplyWindowInsets { sessionRecycler, insets, initialState ->
            sessionRecycler.updatePadding(
                bottom = insets.systemWindowInsetBottom + initialState.paddings.bottom
            )
        }

        sessionTabViewModel.uiModel.observe(viewLifecycleOwner) { uiModel ->
            val shouldBeCollapsed = when (uiModel.expandFilterState) {
                ExpandFilterState.COLLAPSED ->
                    true
                else ->
                    false
            }
            if (binding.isCollapsed != shouldBeCollapsed) {
                TransitionManager.beginDelayedTransition(
                    binding.sessionRecycler.parent as ViewGroup
                )
                binding.isCollapsed = shouldBeCollapsed
            }
        }

        sessionsViewModel.uiModel.observe(viewLifecycleOwner) { uiModel ->
            val page = args.page
            val sessions = when (page) {
                is SessionPage.Day -> uiModel.dayToSessionsMap[page].orEmpty()
                SessionPage.Event -> uiModel.events
                SessionPage.Favorite -> uiModel.favoritedSessions
            }
            val count = sessions.filter { it.shouldCountForFilter }.count()

            if (page == SessionPage.Favorite) {
                TransitionManager.beginDelayedTransition(
                    binding.sessionRecycler.parent as ViewGroup
                )
                binding.isEmptyFavoritePage = sessions.isEmpty()
            }

            if (page == SessionPage.Event) {
                binding.isEventPage = true
            }

            // For Android Lint
            @Suppress("USELESS_CAST")
            binding.filteredSessionCount.text = getString(
                R.string.applicable_session,
                count as Int
            )
            binding.isFiltered = uiModel.filters.isFiltered()
            binding.filteredSessionCount.isVisible = uiModel.filters.isFiltered()
            groupAdapter.update(sessions.map {
                sessionItemFactory.create(it, sessionsViewModel)
            })
            uiModel.error?.let {
                systemViewModel.onError(it)
            }

            val position = uiModel.shouldScrollSessionPosition[page]
            if (position != null) {
                binding.sessionRecycler.smoothScrollToPositionWithLayoutManager(position)
                sessionsViewModel.onScrolled()
            }
        }
    }

    private fun RecyclerView.smoothScrollToPositionWithLayoutManager(position: Int) {
        (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 0)
    }

    companion object {
        fun newInstance(
            args: BottomSheetSessionsFragmentArgs
        ): BottomSheetSessionsFragment {
            return BottomSheetSessionsFragment().apply {
                arguments = args.toBundle()
            }
        }
    }
}

@Module
abstract class BottomSheetSessionsFragmentModule {
    @Module
    companion object {
        @PageScope
        @JvmStatic
        @Provides
        fun providesLifecycleOwnerLiveData(
            mainBottomSheetSessionsFragment: BottomSheetSessionsFragment
        ): LiveData<LifecycleOwner> {
            return mainBottomSheetSessionsFragment.viewLifecycleOwnerLiveData
        }
    }
}
