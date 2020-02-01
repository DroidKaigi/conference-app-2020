package io.github.droidkaigi.confsched2020.session.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.soywiz.klock.DateTime
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedActivityViewModels
import io.github.droidkaigi.confsched2020.model.SessionPage
import io.github.droidkaigi.confsched2020.model.defaultTimeZoneOffset
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.FragmentMainSessionsBinding
import io.github.droidkaigi.confsched2020.session.ui.MainSessionsFragmentDirections.Companion.actionSessionToSearchSessions
import io.github.droidkaigi.confsched2020.session.ui.item.SessionItem
import io.github.droidkaigi.confsched2020.session.ui.viewmodel.SessionsViewModel
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel
import javax.inject.Inject
import javax.inject.Provider

class MainSessionsFragment : Fragment(R.layout.fragment_main_sessions), HasAndroidInjector {

    @Inject
    lateinit var sessionsViewModelProvider: Provider<SessionsViewModel>
    private val sessionsViewModel: SessionsViewModel by assistedActivityViewModels {
        sessionsViewModelProvider.get()
    }
    @Inject
    lateinit var systemViewModelProvider: Provider<SystemViewModel>
    private val systemViewModel: SystemViewModel by assistedActivityViewModels {
        systemViewModelProvider.get()
    }

    @Inject
    lateinit var sessionItemFactory: SessionItem.Factory

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        val binding = FragmentMainSessionsBinding.bind(view)
        setupSessionPager(binding)
    }

    private fun setupSessionPager(binding: FragmentMainSessionsBinding) {
        val tabLayoutMediator = TabLayoutMediator(
            binding.sessionsTabLayout,
            binding.sessionsViewpager
        ) { tab, position ->
            tab.text = SessionPage.pages[position].title
        }
        // TODO: apply margin design
//        binding.sessionsViewpager.pageMargin =
//            resources.getDimensionPixelSize(R.dimen.session_pager_horizontal_padding)
        binding.sessionsProgressBar.show()
        sessionsViewModel.uiModel.observe(viewLifecycleOwner) { uiModel ->
            with(binding.sessionsProgressBar) { if (uiModel.isLoading) show() else hide() }
        }
        binding.sessionsViewpager.adapter = object : FragmentStateAdapter(
            this
        ) {
            override fun getItemCount(): Int = SessionPage.pages.size

            override fun createFragment(position: Int): Fragment {
                return SessionsFragment.newInstance(
                    SessionsFragmentArgs(position)
                )
            }
        }

        binding.sessionsTabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                    tab?.let {
                        sessionsViewModel.onTabReselected()
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) { // no-op
                }

                override fun onTabSelected(tab: TabLayout.Tab?) { // no-op
                }
            })

        val jstNow = DateTime.now().toOffset(defaultTimeZoneOffset())
        if (jstNow.yearInt == 2020 && jstNow.month1 == 2 && jstNow.dayOfMonth == 21) {
            binding.sessionsViewpager.currentItem = 1
        }

        tabLayoutMediator.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sessions, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.session_search -> {
                findNavController().navigate(actionSessionToSearchSessions())
                return false
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

@Module
abstract class MainSessionsFragmentModule {
    @ContributesAndroidInjector(modules = [SessionsFragmentModule::class])
    abstract fun contributeSessionPageFragment(): SessionsFragment

    companion object {
        @PageScope
        @Provides
        fun providesLifecycleOwnerLiveData(
            mainSessionsFragment: MainSessionsFragment
        ): LiveData<LifecycleOwner> {
            return mainSessionsFragment.viewLifecycleOwnerLiveData
        }
    }
}
