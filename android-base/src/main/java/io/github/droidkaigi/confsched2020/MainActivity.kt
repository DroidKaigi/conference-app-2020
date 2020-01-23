package io.github.droidkaigi.confsched2020

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import io.github.droidkaigi.confsched2020.about.ui.AboutFragment
import io.github.droidkaigi.confsched2020.about.ui.AboutFragmentModule
import io.github.droidkaigi.confsched2020.about.ui.di.AboutAssistedInjectModule
import io.github.droidkaigi.confsched2020.announcement.ui.AnnouncementFragment
import io.github.droidkaigi.confsched2020.announcement.ui.AnnouncementFragment.AnnouncementFragmentModule
import io.github.droidkaigi.confsched2020.announcement.ui.di.AnnouncementAssistedInjectModule
import io.github.droidkaigi.confsched2020.databinding.ActivityMainBinding
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedActivityViewModels
import io.github.droidkaigi.confsched2020.ext.getThemeColor
import io.github.droidkaigi.confsched2020.ext.stringRes
import io.github.droidkaigi.confsched2020.floormap.ui.FloorMapFragment
import io.github.droidkaigi.confsched2020.floormap.ui.FloorMapFragment.FloorMapFragmentModule
import io.github.droidkaigi.confsched2020.floormap.ui.di.FloorMapInjectModule
import io.github.droidkaigi.confsched2020.model.repository.SessionRepository
import io.github.droidkaigi.confsched2020.session.ui.MainSessionsFragment
import io.github.droidkaigi.confsched2020.session.ui.MainSessionsFragmentModule
import io.github.droidkaigi.confsched2020.session.ui.SearchSessionsFragment
import io.github.droidkaigi.confsched2020.session.ui.SearchSessionsFragmentModule
import io.github.droidkaigi.confsched2020.session.ui.SessionDetailFragment
import io.github.droidkaigi.confsched2020.session.ui.SessionDetailFragmentModule
import io.github.droidkaigi.confsched2020.session.ui.SpeakerFragment
import io.github.droidkaigi.confsched2020.session.ui.SpeakerFragmentModule
import io.github.droidkaigi.confsched2020.session.ui.di.SessionAssistedInjectModule
import io.github.droidkaigi.confsched2020.session_survey.ui.SessionSurveyFragment
import io.github.droidkaigi.confsched2020.session_survey.ui.SessionSurveyFragmentModule
import io.github.droidkaigi.confsched2020.session_survey.ui.di.SessionSurveyAssistedInjectModule
import io.github.droidkaigi.confsched2020.sponsor.ui.SponsorsFragment
import io.github.droidkaigi.confsched2020.sponsor.ui.SponsorsFragmentModule
import io.github.droidkaigi.confsched2020.sponsor.ui.di.SponsorsAssistedInjectModule
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel
import io.github.droidkaigi.confsched2020.ui.PageConfiguration
import io.github.droidkaigi.confsched2020.ui.widget.SystemUiManager
import timber.log.Timber
import timber.log.warn
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : DaggerAppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(
            this,
            R.layout.activity_main
        )
    }
    @Inject
    lateinit var systemViewModelProvider: Provider<SystemViewModel>
    private val systemViewModel: SystemViewModel by assistedActivityViewModels {
        systemViewModelProvider.get()
    }
    @Inject
    lateinit var sessionRepository: SessionRepository
    private val navController: NavController by lazy {
        Navigation.findNavController(this, R.id.root_nav_host_fragment)
    }

    private val statusBarColors: SystemUiManager by lazy {
        SystemUiManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        setupNavigation()
        setupStatusBarColors()

        binding.drawerLayout.doOnApplyWindowInsets { _, insets, _ ->
            binding.drawerLayout.setChildInsetsWorkAround(insets)
        }
        binding.contentContainer.doOnApplyWindowInsets { view, insets, initialState ->
            view.updatePadding(
                left = insets.systemWindowInsetLeft + initialState.paddings.left,
                right = insets.systemWindowInsetRight + initialState.paddings.right
            )
        }
        binding.toolbar.doOnApplyWindowInsets { view, insets, initialState ->
            view.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topMargin = insets.systemWindowInsetTop + initialState.margins.top
            }
            // Invalidate because option menu cannot be displayed after screen rotation
            invalidateOptionsMenu()
        }
        binding.navView.doOnApplyWindowInsets { view, insets, initialState ->
            view.apply {
                // On seascape mode only, nav bar is overlapped with DrawerLayout.
                // So set left padding and reset width.
                val leftSpace = insets.systemWindowInsetLeft + initialState.paddings.left
                updatePadding(left = leftSpace)
                updateLayoutParams {
                    if (getWidth() > 0) {
                        width = measuredWidth + leftSpace
                    }
                }
            }
        }

        systemViewModel.errorLiveData.observe(this) { appError ->
            Timber.warn(appError) { "AppError occurred" }
            Snackbar
                .make(
                    findViewById(R.id.root_nav_host_fragment),
                    appError.stringRes(),
                    Snackbar.LENGTH_LONG
                )
                .show()
        }
    }

    @SuppressLint("RestrictedApi")
    private fun DrawerLayout.setChildInsetsWorkAround(insets: WindowInsetsCompat) {
        // If we use fitSystemWindows, DrawerLayout will consume windowInsets.
        // But we want to apply window insets in other places. So we use the inner method for padding
        setChildInsets(
            insets.toWindowInsets(), insets.systemWindowInsetTop > 0
        )
    }

    private fun setupNavigation() {
        val appBarConfiguration = AppBarConfiguration(
            PageConfiguration.values().filter { it.isTopLevel }.map { it.id }.toSet(),
            binding.drawerLayout
        ) {
            onBackPressed()
            true
        }
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.navView.setNavigationItemSelectedListener { item ->
            handleNavigation(item.itemId)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            onDestinationChange(destination)
        }
    }

    private fun onDestinationChange(destination: NavDestination) {
        binding.navView.menu.findItem(destination.id)?.isChecked = true

        val config = PageConfiguration.getConfiguration(destination.id)
        if (!config.hasTitle) {
            supportActionBar?.title = ""
        }
        if (config.isShowLogoImage) {
            supportActionBar?.setLogo(R.drawable.ic_logo)
        } else {
            supportActionBar?.setLogo(null)
        }
        statusBarColors.isIndigoBackground = config.isIndigoBackground
        binding.isIndigoBackground = config.isIndigoBackground
        val iconTint = getThemeColor(
            if (config.isIndigoBackground) {
                R.attr.colorOnPrimary
            } else {
                R.attr.colorOnSurface
            }
        )
        binding.toolbar.navigationIcon = if (config.isTopLevel) {
            AppCompatResources.getDrawable(this, R.drawable.ic_menu_black_24dp)
        } else {
            AppCompatResources.getDrawable(this, R.drawable.ic_arrow_back_black_24dp)
        }.apply {
            this?.setTint(iconTint)
        }
    }

    private fun setupStatusBarColors() {
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                statusBarColors.drawerSlideOffset = slideOffset
            }
        })

        statusBarColors.systemUiVisibility.distinctUntilChanged().observe(this) { visibility ->
            window.decorView.systemUiVisibility = visibility
        }
        statusBarColors.statusBarColor.distinctUntilChanged().observe(this) { color ->
            window.statusBarColor = color
        }
    }

    private fun handleNavigation(@IdRes itemId: Int): Boolean {
        binding.drawerLayout.closeDrawers()

        return try {
            // ignore if current destination is selected
            if (navController.currentDestination?.id == itemId) return false
            val builder = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(R.id.main, false)
                .setEnterAnim(R.anim.fade_in)
                .setExitAnim(R.anim.fade_out)
                .setPopEnterAnim(R.anim.fade_in)
                .setPopExitAnim(R.anim.fade_out)
            val options = builder.build()
            navController.navigate(itemId, null, options)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}

@Module
abstract class MainActivityModule {
    @Binds
    abstract fun providesActivity(mainActivity: MainActivity): FragmentActivity

    @PageScope
    @ContributesAndroidInjector(
        modules = [MainSessionsFragmentModule::class, SessionAssistedInjectModule::class]
    )
    abstract fun contributeSessionsFragment(): MainSessionsFragment

    @PageScope
    @ContributesAndroidInjector(
        modules = [SessionDetailFragmentModule::class, SessionAssistedInjectModule::class]
    )
    abstract fun contributeSessionDetailFragment(): SessionDetailFragment

    @PageScope
    @ContributesAndroidInjector(
        modules = [SearchSessionsFragmentModule::class, SessionAssistedInjectModule::class]
    )
    abstract fun contributeSearchSessionsFragment(): SearchSessionsFragment

    @PageScope
    @ContributesAndroidInjector(
        modules = [SpeakerFragmentModule::class, SessionAssistedInjectModule::class]
    )
    abstract fun contributeSpeakerFragment(): SpeakerFragment

    @PageScope
    @ContributesAndroidInjector(
        modules = [SponsorsFragmentModule::class, SponsorsAssistedInjectModule::class]
    )
    abstract fun contributeSponsorsFragment(): SponsorsFragment

    @PageScope
    @ContributesAndroidInjector(
        modules = [AnnouncementFragmentModule::class, AnnouncementAssistedInjectModule::class]
    )
    abstract fun contributeAnnouncementFragment(): AnnouncementFragment

    @PageScope
    @ContributesAndroidInjector(
        modules = [AboutFragmentModule::class, AboutAssistedInjectModule::class]
    )
    abstract fun contributeAboutFragment(): AboutFragment

    @PageScope
    @ContributesAndroidInjector(
        modules = [FloorMapFragmentModule::class, FloorMapInjectModule::class]
    )
    abstract fun contributeFloorMapFragment(): FloorMapFragment

    @PageScope
    @ContributesAndroidInjector(
        modules = [SessionSurveyFragmentModule::class, SessionSurveyAssistedInjectModule::class]
    )
    abstract fun contributeSessionSurveyFragment(): SessionSurveyFragment

    @Module
    companion object

    @Module
    abstract class MainActivityBuilder {
        @ContributesAndroidInjector(modules = [MainActivityModule::class])
        abstract fun contributeMainActivity(): MainActivity
    }
}