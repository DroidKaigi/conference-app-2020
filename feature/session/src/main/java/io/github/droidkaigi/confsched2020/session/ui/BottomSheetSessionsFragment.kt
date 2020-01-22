package io.github.droidkaigi.confsched2020.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import dagger.android.support.DaggerFragment
import dev.chrisbanes.insetter.doOnApplyWindowInsets
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
import io.github.droidkaigi.confsched2020.util.autoCleared
import javax.inject.Inject
import javax.inject.Provider

class BottomSheetSessionsFragment : DaggerFragment() {

    private var binding: FragmentBottomSheetSessionsBinding by autoCleared()

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_bottom_sheet_sessions,
            container,
            false
        )
        return binding.apply { isEmptyFavoritePage = false }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

                binding.dividerShadow.isVisible =
                    binding.sessionRecycler.canScrollVertically(-1)
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
            TransitionManager.beginDelayedTransition(binding.sessionRecycler.parent as ViewGroup)
            binding.isCollapsed = when (uiModel.expandFilterState) {
                ExpandFilterState.COLLAPSED ->
                    true
                else ->
                    false
            }
        }

        sessionsViewModel.uiModel.observe(viewLifecycleOwner) { uiModel ->
            val page = args.page
            val sessions = when (page) {
                is SessionPage.Day -> uiModel.dayToSessionsMap[page].orEmpty()
                SessionPage.Favorite -> uiModel.favoritedSessions
            }
            val count = sessions.filter { it.shouldCountForFilter }.count()

            if (page == SessionPage.Favorite) {
                TransitionManager.beginDelayedTransition(
                    binding.sessionRecycler.parent as ViewGroup
                )
                binding.isEmptyFavoritePage = sessions.isEmpty()
            }

            // For Android Lint
            @Suppress("USELESS_CAST")
            binding.filteredSessionCount.text = getString(
                R.string.applicable_session,
                count as Int
            )
            binding.isFiltered = uiModel.filters.isFiltered()
            binding.filteredSessionCount.isVisible = uiModel.filters.isFiltered()
            val startFilterTextRes = if (uiModel.filters.isFiltered()) {
                R.string.filter_now
            } else {
                R.string.start_filter
            }
            binding.startFilter.text = getString(startFilterTextRes)
            groupAdapter.update(sessions.map {
                sessionItemFactory.create(it, sessionsViewModel)
            })
            uiModel.error?.let {
                systemViewModel.onError(it)
            }
        }
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
