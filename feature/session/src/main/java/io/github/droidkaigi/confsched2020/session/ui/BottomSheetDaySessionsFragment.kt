package io.github.droidkaigi.confsched2020.session.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import androidx.transition.TransitionManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
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
import io.github.droidkaigi.confsched2020.util.DaggerFragment
import io.github.droidkaigi.confsched2020.util.autoCleared
import javax.inject.Inject
import javax.inject.Provider

class BottomSheetDaySessionsFragment : DaggerFragment(R.layout.fragment_bottom_sheet_sessions) {

    private var binding: FragmentBottomSheetSessionsBinding by autoCleared()

    @Inject
    lateinit var sessionsViewModelProvider: Provider<SessionsViewModel>
    private val sessionsViewModel: SessionsViewModel by assistedActivityViewModels {
        sessionsViewModelProvider.get()
    }
    @Inject
    lateinit var sessionTabViewModelProvider: Provider<SessionTabViewModel>
    private val sessionTabViewModel: SessionTabViewModel by assistedActivityViewModels({
        SessionPage.dayOfNumber(args.day).title
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
    private val args: BottomSheetDaySessionsFragmentArgs by lazy {
        BottomSheetDaySessionsFragmentArgs.fromBundle(arguments ?: Bundle())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentBottomSheetSessionsBinding.bind(view)

        val groupAdapter = GroupAdapter<ViewHolder<*>>()
        binding.sessionRecycler.adapter = groupAdapter
        binding.sessionRecycler.addItemDecoration(
            SessionsItemDecoration(
                groupAdapter,
                requireContext()
            )
        )
        binding.startFilter.setOnClickListener {
            sessionTabViewModel.toggleExpand()
        }
        binding.expandLess.setOnClickListener {
            sessionTabViewModel.toggleExpand()
        }
        binding.sessionRecycler.doOnApplyWindowInsets { sessionRecycler, insets, initialState ->
            sessionRecycler.updatePadding(bottom = insets.systemWindowInsetBottom + initialState.paddings.bottom)
        }

        sessionTabViewModel.uiModel.observe(viewLifecycleOwner) { uiModel ->
            TransitionManager.beginDelayedTransition(binding.sessionRecycler.parent as ViewGroup)
            binding.sessionRecycler.isVisible = when (uiModel.expandFilterState) {
                ExpandFilterState.EXPANDED, ExpandFilterState.CHANGING ->
                    true
                else ->
                    false
            }
            binding.startFilter.visibility = when (uiModel.expandFilterState) {
                ExpandFilterState.EXPANDED, ExpandFilterState.CHANGING ->
                    View.VISIBLE
                else ->
                    View.INVISIBLE
            }
            binding.expandLess.isVisible = when (uiModel.expandFilterState) {
                ExpandFilterState.COLLAPSED ->
                    true
                else ->
                    false
            }
        }

        sessionsViewModel.uiModel.observe(viewLifecycleOwner) { uiModel: SessionsViewModel.UiModel ->
            // TODO: support favorite list
            val page = SessionPage.dayOfNumber(args.day) as? SessionPage.Day ?: return@observe
            val sessions = uiModel.dayToSessionsMap[page].orEmpty()
            val count = sessions.filter { it.shouldCountForFilter }.count()
            // For Android Lint
            @Suppress("USELESS_CAST")
            binding.filteredSessionCount.text = getString(
                R.string.applicable_session,
                count as Int
            )
            binding.filteredSessionCount.isVisible = uiModel.filters.isFiltered()
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
            args: BottomSheetDaySessionsFragmentArgs
        ): BottomSheetDaySessionsFragment {
            return BottomSheetDaySessionsFragment().apply {
                arguments = args.toBundle()
            }
        }
    }
}

@Module
abstract class BottomSheetDaySessionsFragmentModule {
    @Module
    companion object {
        @PageScope
        @JvmStatic
        @Provides
        fun providesLifecycleOwnerLiveData(
            mainBottomSheetDaySessionsFragment: BottomSheetDaySessionsFragment
        ): LiveData<LifecycleOwner> {
            return mainBottomSheetDaySessionsFragment.viewLifecycleOwnerLiveData
        }
    }
}
