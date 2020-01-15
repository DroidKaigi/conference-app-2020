package io.github.droidkaigi.confsched2020.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.ChipGroup
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerFragment
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedActivityViewModels
import io.github.droidkaigi.confsched2020.model.ExpandFilterState
import io.github.droidkaigi.confsched2020.model.SessionPage
import io.github.droidkaigi.confsched2020.model.defaultLang
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.FragmentSessionsBinding
import io.github.droidkaigi.confsched2020.session.ui.di.SessionAssistedInjectModule
import io.github.droidkaigi.confsched2020.session.ui.item.SessionItem
import io.github.droidkaigi.confsched2020.session.ui.viewmodel.SessionTabViewModel
import io.github.droidkaigi.confsched2020.session.ui.viewmodel.SessionsViewModel
import io.github.droidkaigi.confsched2020.ui.widget.FilterChip
import io.github.droidkaigi.confsched2020.ui.widget.onCheckedChanged
import io.github.droidkaigi.confsched2020.util.autoCleared
import javax.inject.Inject
import javax.inject.Provider

class SessionsFragment : DaggerFragment() {

    private var binding: FragmentSessionsBinding by autoCleared()
    private lateinit var overrideBackPressedCallback: OnBackPressedCallback

    private val sessionSheetBehavior: BottomSheetBehavior<*>
        get() {
            val layoutParams = binding.sessionsSheet.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = layoutParams.behavior
            return (behavior as BottomSheetBehavior)
        }

    @Inject lateinit var sessionsViewModelProvider: Provider<SessionsViewModel>
    private val sessionsViewModel: SessionsViewModel by assistedActivityViewModels {
        sessionsViewModelProvider.get()
    }

    @Inject
    lateinit var sessionTabViewModelProvider: Provider<SessionTabViewModel>
    private val sessionTabViewModel: SessionTabViewModel by assistedActivityViewModels({
        SessionPage.pages[args.tabIndex].title
    }) {
        sessionTabViewModelProvider.get()
    }

    @Inject
    lateinit var sessionItemFactory: SessionItem.Factory
    private val args: SessionsFragmentArgs by lazy {
        SessionsFragmentArgs.fromBundle(arguments ?: Bundle())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            setupSessionsFragment()
        }
        overrideBackPressedCallback =
            requireActivity().onBackPressedDispatcher.addCallback(this, false) {
                sessionTabViewModel.setExpand(ExpandFilterState.EXPANDED)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_sessions,
            container,
            false
        )
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val initialPeekHeight = sessionSheetBehavior.peekHeight
        binding.sessionsSheet.doOnApplyWindowInsets { _, insets, _ ->
            sessionSheetBehavior.peekHeight = insets.systemWindowInsetBottom + initialPeekHeight
        }
        sessionSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                sessionTabViewModel.setExpand(
                    when (newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            ExpandFilterState.COLLAPSED
                        }
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            ExpandFilterState.EXPANDED
                        }
                        else -> {
                            ExpandFilterState.CHANGING
                        }
                    }
                )
            }
        })
        binding.filterReset.setOnClickListener {
            sessionsViewModel.resetFilter()
        }

        sessionTabViewModel.uiModel.observe(viewLifecycleOwner) { uiModel ->
            when (uiModel.expandFilterState) {
                ExpandFilterState.EXPANDED -> {
                    sessionSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    overrideBackPressedCallback.isEnabled = false
                }
                ExpandFilterState.COLLAPSED -> {
                    sessionSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    overrideBackPressedCallback.isEnabled = true
                }
                ExpandFilterState.CHANGING -> Unit
            }
        }
        sessionsViewModel.uiModel.observe(viewLifecycleOwner) { uiModel: SessionsViewModel.UiModel ->
            binding.roomFilters.setupFilter(
                allFilterSet = uiModel.allFilters.rooms,
                currentFilterSet = uiModel.filters.rooms,
                filterName = { it.name.getByLang(defaultLang()) }
            ) { checked, room ->
                sessionsViewModel.filterChanged(room, checked)
            }
            binding.languageFilters.setupFilter(
                allFilterSet = uiModel.allFilters.langs,
                currentFilterSet = uiModel.filters.langs,
                filterName = { it.name }
            ) { checked, language ->
                sessionsViewModel.filterChanged(language, checked)
            }
            binding.categoryFilters.setupFilter(
                allFilterSet = uiModel.allFilters.categories,
                currentFilterSet = uiModel.filters.categories,
                filterName = { it.name.getByLang(defaultLang()) }
            ) { checked, category ->
                sessionsViewModel.filterChanged(category, checked)
            }
            binding.audienceCategoryFilters.setupFilter(
                allFilterSet = uiModel.allFilters.audienceCategories,
                currentFilterSet = uiModel.filters.audienceCategories,
                filterName = { it.name }
            ) { checked, audienceCategory ->
                sessionsViewModel.filterChanged(audienceCategory, checked)
            }
            binding.languageSupportFilters.setupFilter(
                allFilterSet = uiModel.allFilters.langSupports,
                currentFilterSet = uiModel.filters.langSupports,
                filterName = { it.name }
            ) { checked, level ->
                sessionsViewModel.filterChanged(level, checked)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // override back button iff front layer collapsed (filter is shown)
        overrideBackPressedCallback.isEnabled =
            sessionSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun onPause() {
        super.onPause()
        overrideBackPressedCallback.isEnabled = false
    }

    private inline fun <reified T> ChipGroup.setupFilter(
        allFilterSet: Set<T>,
        currentFilterSet: Set<T>,
        filterName: (T) -> String,
        crossinline onCheckChanged: (Boolean, T) -> Unit
    ) {
        // judge should we inflate chip?
        val shouldInflateChip = childCount == 0 || children.withIndex().any { (index, view) ->
            view.getTag(R.id.tag_filter) != allFilterSet.elementAtOrNull(index)
        }
        val filterToView = if (shouldInflateChip) {
            // filter data changed, so we should inflate it
            removeAllViews()
            allFilterSet.map { filter ->
                val chip =
                    layoutInflater.inflate(
                        R.layout.layout_chip,
                        this,
                        false
                    ) as FilterChip
                chip.onCheckedChangeListener = null
                chip.text = filterName(filter)
                chip.setTag(R.id.tag_filter, filter)
                addView(chip)
                filter to chip
            }.toMap()
        } else {
            // use existing view
            children.map { it.getTag(R.id.tag_filter) as T to it as FilterChip }.toMap()
        }

        // bind chip data
        filterToView.forEach { (filter, chip) ->
            val shouldChecked = currentFilterSet.contains(filter)
            if (chip.isChecked != shouldChecked) {
                chip.isChecked = shouldChecked
            }
            chip.onCheckedChanged { _, checked ->
                onCheckChanged(checked, filter)
            }
        }
    }

    private fun setupSessionsFragment() {
        val tab = SessionPage.pages[args.tabIndex]
        val fragment: Fragment = when (tab) {
            is SessionPage.Day -> {
                BottomSheetDaySessionsFragment.newInstance(
                    BottomSheetDaySessionsFragmentArgs
                        .Builder(tab.day)
                        .build()
                )
            }
            SessionPage.Favorite -> {
                BottomSheetFavoriteSessionsFragment.newInstance()
            }
        }

        childFragmentManager
            .beginTransaction()
            .replace(R.id.sessions_sheet, fragment, tab.title)
            .disallowAddToBackStack()
            .commit()
    }

    companion object {
        fun newInstance(args: SessionsFragmentArgs): SessionsFragment {
            return SessionsFragment().apply {
                arguments = args.toBundle()
            }
        }
    }
}

@Module
abstract class SessionsFragmentModule {
    @ContributesAndroidInjector(
        modules = [SessionAssistedInjectModule::class]
    )
    abstract fun contributeBottomSheetDaySessionsFragment(): BottomSheetDaySessionsFragment

    @ContributesAndroidInjector(
        modules = [SessionAssistedInjectModule::class]
    )
    abstract fun contributeBottomSheetFavoriteSessionsFragment(): BottomSheetFavoriteSessionsFragment

    @Module
    companion object {
        @PageScope
        @JvmStatic
        @Provides
        fun providesLifecycleOwnerLiveData(
            mainSessionsFragment: MainSessionsFragment
        ): LiveData<LifecycleOwner> {
            return mainSessionsFragment.viewLifecycleOwnerLiveData
        }
    }
}
