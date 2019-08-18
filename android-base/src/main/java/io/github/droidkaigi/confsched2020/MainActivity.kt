package io.github.droidkaigi.confsched2020

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import io.github.droidkaigi.confsched2020.data.repository.SessionRepository
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.session.ui.SessionDetailFragment
import io.github.droidkaigi.confsched2020.session.ui.SessionDetailFragmentModule
import io.github.droidkaigi.confsched2020.session.ui.SessionsFragmentModule
import io.github.droidkaigi.confsched2020.session.ui.SessionsFragment
import io.github.droidkaigi.confsched2020.session.ui.di.SessionAssistedInjectModule
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    @Inject lateinit var sessionRepository: SessionRepository
    @Inject lateinit var navController: dagger.Lazy<NavController>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.staff_test).setOnClickListener {
            navController.get().navigate(R.id.staffs)
        }
    }
}

@Module
abstract class MainActivityModule {
    @Binds
    abstract fun providesActivity(mainActivity: MainActivity): FragmentActivity

    @PageScope
    @ContributesAndroidInjector(
        modules = [SessionsFragmentModule::class, SessionAssistedInjectModule::class]
    )
    abstract fun contributeSessionsFragment(): SessionsFragment

    @PageScope
    @ContributesAndroidInjector(
        modules = [SessionDetailFragmentModule::class, SessionAssistedInjectModule::class]
    )
    abstract fun contributeSessionDetailFragment(): SessionDetailFragment

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideNavController(mainActivity: MainActivity): NavController {
            return Navigation
                .findNavController(mainActivity, R.id.root_nav_host_fragment)
        }
    }

    @Module
    abstract class MainActivityBuilder {
        @ContributesAndroidInjector(modules = [MainActivityModule::class])
        abstract fun contributeMainActivity(): MainActivity
    }
}