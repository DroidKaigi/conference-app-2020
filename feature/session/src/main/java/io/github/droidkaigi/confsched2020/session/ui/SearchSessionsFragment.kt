package io.github.droidkaigi.confsched2020.session.ui

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedActivityViewModels
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import io.github.droidkaigi.confsched2020.ext.requireValue
import io.github.droidkaigi.confsched2020.model.defaultLang
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.FragmentSearchSessionsBinding
import io.github.droidkaigi.confsched2020.session.ui.item.SectionHeaderItem
import io.github.droidkaigi.confsched2020.session.ui.item.SessionItem
import io.github.droidkaigi.confsched2020.session.ui.item.SpeakerItem
import io.github.droidkaigi.confsched2020.session.ui.viewmodel.SearchSessionsViewModel
import io.github.droidkaigi.confsched2020.session.ui.viewmodel.SessionsViewModel
import io.github.droidkaigi.confsched2020.session.ui.widget.SearchItemDecoration
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel
import io.github.droidkaigi.confsched2020.util.AppcompatRId
import io.github.droidkaigi.confsched2020.util.DaggerFragment
import io.github.droidkaigi.confsched2020.util.autoCleared
import java.util.Locale
import javax.inject.Inject
import javax.inject.Provider

class SearchSessionsFragment : DaggerFragment(R.layout.fragment_search_sessions) {

    private var binding: FragmentSearchSessionsBinding by autoCleared()

    @Inject lateinit var searchSessionsModelFactory: SearchSessionsViewModel.Factory
    private val searchSessionsViewModel by assistedViewModels {
        searchSessionsModelFactory.create()
    }

    @Inject lateinit var sessionsViewModelProvider: Provider<SessionsViewModel>
    private val sessionsViewModel: SessionsViewModel by assistedActivityViewModels {
        sessionsViewModelProvider.get()
    }

    @Inject lateinit var systemViewModelProvider: Provider<SystemViewModel>
    private val systemViewModel: SystemViewModel by assistedActivityViewModels {
        systemViewModelProvider.get()
    }

    @Inject
    lateinit var sessionItemFactory: SessionItem.Factory

    @Inject
    lateinit var speakerItemFactory: SpeakerItem.Factory

    @Inject
    lateinit var sectionHeaderItemFactory: SectionHeaderItem.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        view?.let {
            val imm =
                context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(it.windowToken, 0);
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSearchSessionsBinding.bind(view)

        val groupAdapter = GroupAdapter<ViewHolder<*>>()
        binding.searchSessionRecycler.adapter = groupAdapter
        context?.let {
            binding.searchSessionRecycler.addItemDecoration(SearchItemDecoration(
                it,
                getGroupId = { position ->
                    when (val item = groupAdapter.getItem(position)) {
                        is SpeakerItem -> item.speaker.name[0].toUpperCase().toLong()
                        is SessionItem -> item.title().getByLang(defaultLang())[0].toUpperCase().toLong()
                        else -> SearchItemDecoration.EMPTY_ID
                    }
                },
                getInitial = { position ->
                    when (val item = groupAdapter.getItem(position)) {
                        is SpeakerItem -> item.speaker.name[0].toUpperCase().toString()
                        is SessionItem -> item.title().getByLang(defaultLang())[0].toUpperCase().toString()
                        else -> SearchItemDecoration.DEFAULT_INITIAL
                    }
                }
            ))
        }
        binding.searchSessionRecycler.doOnApplyWindowInsets { searchSessionRecycler, insets, initialState ->
            searchSessionRecycler.updatePadding(bottom = insets.systemWindowInsetBottom + initialState.paddings.bottom)
        }

        searchSessionsViewModel.uiModel.observe(viewLifecycleOwner) { uiModel: SearchSessionsViewModel.UiModel ->
            groupAdapter.clear()

            if (uiModel.searchResult.speakers.isNotEmpty()) {
                groupAdapter.add(sectionHeaderItemFactory.create(resources.getString(R.string.speaker)))
                groupAdapter.addAll(uiModel.searchResult.speakers.map {
                    speakerItemFactory.create(it)
                }.sortedBy {
                    it.speaker.name.toUpperCase(Locale.getDefault())
                })
            }

            if (uiModel.searchResult.sessions.isNotEmpty()) {
                groupAdapter.add(sectionHeaderItemFactory.create(resources.getString(R.string.session)))
                groupAdapter.addAll(uiModel.searchResult.sessions.map {
                    sessionItemFactory.create(it, sessionsViewModel)
                }.sortedBy {
                    it.title().getByLang(defaultLang())
                })
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search_sessions, menu)
        val searchView = menu.findItem(R.id.search_view).actionView as SearchView
        (searchView.findViewById(AppcompatRId.search_button) as ImageView).setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.search_icon)
        )
        (searchView.findViewById(AppcompatRId.search_close_btn) as ImageView).setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.search_close_icon)
        )
        searchView.isIconified = false
        val searchResult = searchSessionsViewModel.uiModel.requireValue().searchResult
        if (!searchResult.isEmpty()) {
            searchView.setQuery(searchResult.query, false)
        }
        searchView.queryHint = resources.getString(R.string.query_hint)
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
        @JvmStatic @Provides fun providesLifecycleOwnerLiveData(
            searchSessionsFragment: SearchSessionsFragment
        ): LiveData<LifecycleOwner> {
            return searchSessionsFragment.viewLifecycleOwnerLiveData
        }
    }
}
