package io.github.droidkaigi.confsched2020.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import dagger.Module
import dagger.Provides
import dagger.android.support.DaggerFragment
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedActivityViewModels
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import io.github.droidkaigi.confsched2020.model.ServiceSession
import io.github.droidkaigi.confsched2020.model.SpeechSession
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.FragmentSearchSessionsBinding
import io.github.droidkaigi.confsched2020.session.ui.viewmodel.SearchSessionsViewModel
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel
import javax.inject.Inject
import javax.inject.Provider

class SearchSessionsFragment : DaggerFragment() {

    private lateinit var binding: FragmentSearchSessionsBinding

    @Inject lateinit var searchSessionsModelFactory: SearchSessionsViewModel.Factory
    private val searchSessionsViewModel by assistedViewModels {
        searchSessionsModelFactory.create()
    }

    @Inject lateinit var systemViewModelProvider: Provider<SystemViewModel>
    private val systemViewModel: SystemViewModel by assistedActivityViewModels {
        systemViewModelProvider.get()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search_sessions,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        searchSessionsViewModel.uiModel.observe(viewLifecycleOwner) { uiModel: SearchSessionsViewModel.UiModel ->
            binding.dummy.text =
                uiModel.searchResult.sessions.joinToString("\n・", "・") {
                    when (it) {
                        is SpeechSession -> it.title
                        is ServiceSession -> it.title
                    }.ja
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search_sessions, menu)
        val searchView = menu.findItem(R.id.search_view).actionView as SearchView
        searchView.isIconified = false
        searchView.queryHint = "検索する"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                searchSessionsViewModel.updateSearchQuery(s)
                return false
            }
        })
    }

    companion object {
        fun newInstance(): SearchSessionsFragment {
            return SearchSessionsFragment()
        }
    }
}

@Module
abstract class SearchSessionsFragmentModule {
    @Module
    companion object {
        @PageScope
        @JvmStatic @Provides fun providesLifeCycleLiveData(
            searchSessionsFragment: SearchSessionsFragment
        ): LiveData<LifecycleOwner> {
            return searchSessionsFragment.viewLifecycleOwnerLiveData
        }
    }
}