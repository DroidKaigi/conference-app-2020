package io.github.droidkaigi.confsched2020.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import dagger.android.support.DaggerFragment
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedActivityViewModels
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.FragmentBottomSheetFavoriteSessionsBinding
import io.github.droidkaigi.confsched2020.session.ui.item.SessionItem
import io.github.droidkaigi.confsched2020.session.ui.viewmodel.SessionsViewModel
import javax.inject.Inject
import javax.inject.Provider

class BottomSheetFavoriteSessionsFragment : DaggerFragment() {

    private lateinit var binding: FragmentBottomSheetFavoriteSessionsBinding

    @Inject
    lateinit var sessionsViewModelProvider: Provider<SessionsViewModel>
    private val sessionsViewModel: SessionsViewModel by assistedActivityViewModels {
        sessionsViewModelProvider.get()
    }

    @Inject
    lateinit var sessionItemFactory: SessionItem.Factory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_bottom_sheet_favorite_sessions,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val groupAdapter = GroupAdapter<ViewHolder<*>>()
        binding.sessionRecycler.adapter = groupAdapter

        sessionsViewModel.uiModel.observe(viewLifecycleOwner) { uiModel: SessionsViewModel.UiModel ->
            // TODO: support favorite list
            val sessions = uiModel.favoritedSessions
            groupAdapter.update(sessions.map {
                sessionItemFactory.create(it, sessionsViewModel)
            })
        }
    }

    companion object {
        fun newInstance(): BottomSheetFavoriteSessionsFragment {
            return BottomSheetFavoriteSessionsFragment()
        }
    }
}

@Module
abstract class BottomSheetFavoriteSessionsFragmentModule {
    @Module
    companion object {
        @PageScope
        @JvmStatic
        @Provides
        fun providesLifecycleOwnerLiveData(
            mainBottomSheetFavoriteSessionsFragment: BottomSheetFavoriteSessionsFragment
        ): LiveData<LifecycleOwner> {
            return mainBottomSheetFavoriteSessionsFragment.viewLifecycleOwnerLiveData
        }
    }
}
