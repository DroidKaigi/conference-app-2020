package io.github.droidkaigi.confsched2020.session.ui

import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.ChipGroup
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
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
import io.github.droidkaigi.confsched2020.util.DaggerFragment
import io.github.droidkaigi.confsched2020.util.autoCleared
import javax.inject.Inject
import javax.inject.Provider

class SessionsFragment : DaggerFragment(R.layout.fragment_sessions) {

    private var binding: FragmentSessionsBinding by autoCleared()
    private lateinit var overrideBackPressedCallback: OnBackPressedCallback

    private val sessionSheetBehavior: BottomSheetBehavior<*>
        get() {
            val layoutParams = binding.sessionsSheet.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = layoutParams.behavior
            return (behavior as BottomSheetBehavior)
        }

    @Inject
    lateinit var sessionsViewModelProvider: Provider<SessionsViewModel>
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSessionsBinding.bind(view)
        setHasOptionsMenu(true)

        initBottomSheetShapeAppearance()
        val initialPeekHeight = sessionSheetBehavior.peekHeight
        val gestureNavigationBottomSpace =
            if (isEdgeToEdgeEnabled())
                resources.getDimension(R.dimen.gesture_navigation_bottom_space).toInt()
            else 0

        binding.sessionsSheet.doOnApplyWindowInsets { _, insets, _ ->
            sessionSheetBehavior.peekHeight =
                insets.systemWindowInsetBottom + initialPeekHeight + gestureNavigationBottomSpace
        }
        binding.fragmentSessionsScrollView.doOnApplyWindowInsets { scrollView, insets, initialState ->
            // Set a bottom padding due to the system UI is enabled.
            scrollView.updatePadding(bottom = insets.systemWindowInsetBottom + initialPeekHeight + initialState.paddings.bottom)
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

    /**
     * judge gesture navigation is enabled
     * https://android.googlesource.com/platform/packages/apps/Settings.git/+/refs/heads/master/src/com/android/settings/gestures/SystemNavigationPreferenceController.java#97
     *
     * If configNavBarInteractionMode is equal to "2", it means gesture navigation
     * https://android.googlesource.com/platform/frameworks/base/+/refs/heads/android10-mainline-release/core/java/android/view/WindowManagerPolicyConstants.java#60
     * */
    private fun isEdgeToEdgeEnabled(): Boolean {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) return false
        val configNavBarInteractionMode = Resources.getSystem().getIdentifier(
            "config_navBarInteractionMode",
            "integer",
            "android"
        )
        return (context?.resources?.getInteger(configNavBarInteractionMode) == 2)
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
                    BottomSheetDaySessionsFragmentArgs(tab.day)
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

    /**
     * Override Widget.MaterialComponents.BottomSheet shapeAppearance
     * see: https://github.com/DroidKaigi/conference-app-2020/issues/104
     */
    private fun initBottomSheetShapeAppearance() {
        val shapeAppearanceModel =
            ShapeAppearanceModel.Builder()
                .setTopLeftCorner(
                    CornerFamily.ROUNDED,
                    resources.getDimension(R.dimen.bottom_sheet_corner_radius)
                )
                .build()
        /**
         * FrontLayer elevation is 1dp
         * https://material.io/components/backdrop/#anatomy
         */
        val materialShapeDrawable = MaterialShapeDrawable.createWithElevationOverlay(
            requireActivity(),
            resources.getDimension(R.dimen.bottom_sheet_elevation)
        ).apply {
            setShapeAppearanceModel(shapeAppearanceModel)
        }
        binding.sessionsSheet.background = materialShapeDrawable
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
