package io.github.droidkaigi.confsched2020.session.ui

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.core.content.getSystemService
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.GroupieViewHolder
import dagger.Module
import dagger.Provides
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import io.github.droidkaigi.confsched2020.di.Injectable
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
import java.util.Locale
import javax.inject.Inject
import javax.inject.Provider

class SearchSessionsFragment : Fragment(R.layout.fragment_search_sessions), Injectable {

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

    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        view?.let {
            val imm =
                context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSearchSessionsBinding.bind(view)
        val groupAdapter = GroupAdapter<GroupieViewHolder<*>>()
        binding.searchSessionRecycler.adapter = groupAdapter
        context?.let {
            binding.searchSessionRecycler.addItemDecoration(
                SearchItemDecoration(
                    it,
                    getGroupId = { position ->
                        when (val item = groupAdapter.getItem(position)) {
                            is SpeakerItem -> item.speaker.name[0].toUpperCase().toLong()
                            is SessionItem -> {
                                val title = item.title().getByLang(defaultLang())
                                title[0].toUpperCase().toLong()
                            }
                            else -> SearchItemDecoration.EMPTY_ID
                        }
                    },
                    getInitial = { position ->
                        when (val item = groupAdapter.getItem(position)) {
                            is SpeakerItem -> item.speaker.name[0].toUpperCase().toString()
                            is SessionItem -> {
                                val title = item.title().getByLang(defaultLang())
                                title[0].toUpperCase().toString()
                            }
                            else -> SearchItemDecoration.DEFAULT_INITIAL
                        }
                    }
                )
            )
        }
        binding.searchSessionRecycler.doOnApplyWindowInsets { searchSessionRecycler,
            insets,
            initialState ->
            searchSessionRecycler.updatePadding(
                bottom = insets.systemWindowInsetBottom + initialState.paddings.bottom
            )
        }

        searchSessionsViewModel.uiModel.observe(viewLifecycleOwner) { uiModel ->
            groupAdapter.clear()

            if (uiModel.searchResult.speakers.isNotEmpty()) {
                val title = resources.getString(R.string.speaker)
                groupAdapter.add(sectionHeaderItemFactory.create(title))
                groupAdapter.addAll(
                    uiModel.searchResult.speakers.map {
                        speakerItemFactory.create(it, uiModel.searchResult.query)
                    }.sortedBy {
                        it.speaker.name.toUpperCase(Locale.getDefault())
                    }
                )
            }

            if (uiModel.searchResult.sessions.isNotEmpty()) {
                val title = resources.getString(R.string.session)
                groupAdapter.add(sectionHeaderItemFactory.create(title))
                groupAdapter.addAll(
                    uiModel.searchResult.sessions.map {
                        sessionItemFactory.create(it, sessionsViewModel, uiModel.searchResult.query)
                    }.sortedBy {
                        it.title().getByLang(defaultLang())
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity()
        val intent = activity.intent
        if (intent.action == Intent.ACTION_SEARCH &&
            intent.hasExtra(RecognizerIntent.EXTRA_RESULTS)
        ) {
            val query: String = requireNotNull(intent.getStringExtra(SearchManager.QUERY))
            val searchView: SearchView? =
                menu?.findItem(R.id.search_view)?.actionView as SearchView?
            searchView?.setQuery(query, true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search_sessions, menu)
        this.menu = menu
        val searchView = menu.findItem(R.id.search_view).actionView as SearchView
        val context = requireContext()
        context.getSystemService<SearchManager>()?.let { searchManager ->
            searchView.setSearchableInfo(
                searchManager.getSearchableInfo(requireActivity().componentName)
            )
        }
        (searchView.findViewById(AppcompatRId.search_button) as ImageView).setColorFilter(
            AppCompatResources.getColorStateList(
                context,
                R.color.search_icon
            ).defaultColor
        )
        (searchView.findViewById(AppcompatRId.search_close_btn) as ImageView).setColorFilter(
            AppCompatResources.getColorStateList(
                context,
                R.color.search_close_icon
            ).defaultColor
        )
        val searchVoiceButton = searchView.findViewById(AppcompatRId.search_voice_btn) as ImageView
        searchVoiceButton.setImageResource(R.drawable.ic_keyboard_voice_24px)
        searchVoiceButton.setColorFilter(
            AppCompatResources.getColorStateList(
                context,
                R.color.search_voice_icon
            ).defaultColor
        )
        searchView.isIconified = false
        searchView.clearFocus()
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
        searchView.setOnCloseListener {
            searchView.setQuery("", true)
            true
        }
        searchView.maxWidth = Int.MAX_VALUE
    }

    companion object {
        fun newInstance(): SearchSessionsFragment {
            return SearchSessionsFragment()
        }
    }
}

@Module
abstract class SearchSessionsFragmentModule {
    companion object {
        @PageScope
        @Provides
        fun providesLifecycleOwnerLiveData(
            searchSessionsFragment: SearchSessionsFragment
        ): LiveData<LifecycleOwner> {
            return searchSessionsFragment.viewLifecycleOwnerLiveData
        }
    }
}
