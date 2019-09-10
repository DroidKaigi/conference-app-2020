package io.github.droidkaigi.confsched2020.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import dagger.android.support.DaggerFragment
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.FragmentSessionBinding
import io.github.droidkaigi.confsched2020.session.ui.item.SessionItem
import io.github.droidkaigi.confsched2020.session.ui.viewmodel.SessionsViewModel
import io.github.droidkaigi.confsched2020.session.ui.viewmodel.SessionsViewModel.UiModel
import io.github.droidkaigi.confsched2020.util.ProgressTimeLatch
import javax.inject.Inject
import javax.inject.Provider

class SessionsFragment : DaggerFragment() {

    private lateinit var binding: FragmentSessionBinding

    @Inject lateinit var sessionsFactory: Provider<SessionsViewModel>
    private val sessionsViewModel: SessionsViewModel by assistedViewModels {
        sessionsFactory.get()
    }

    @Inject lateinit var sessionItemFactory: SessionItem.Factory

    private lateinit var progressTimeLatch: ProgressTimeLatch

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_session,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val groupAdapter = GroupAdapter<ViewHolder<*>>()
        binding.sessionRecycler.adapter = groupAdapter

        progressTimeLatch = ProgressTimeLatch { showProgress ->
            binding.progressBar.isVisible = showProgress
        }.apply {
            loading = true
        }
        sessionsViewModel.uiModel.observe(viewLifecycleOwner) { uiModel ->
            progressTimeLatch.loading = uiModel.isLoading
            groupAdapter.update(uiModel.sessionContents?.sessions.orEmpty().map {
                sessionItemFactory.create(it, sessionsViewModel)
            })
            showError(uiModel.error)
        }
    }

    private fun showError(error: UiModel.Error) {
        when (error) {
            is UiModel.Error.FailLoadSessions ->{
                error.e.printStackTrace()
                Snackbar.make(requireView(), "Fail to load sessions", Snackbar.LENGTH_LONG).show()
            }
            is UiModel.Error.FailFavorite ->{
                error.e.printStackTrace()
                Snackbar.make(requireView(), "Fail to toggle favorite", Snackbar.LENGTH_LONG).show()
            }
            is UiModel.Error.None -> Unit
        }
    }
}

@Module
abstract class SessionsFragmentModule {
    @Module
    companion object {
        @PageScope
        @JvmStatic @Provides fun providesLifecycleOwnerLiveData(
            sessionsFragment: SessionsFragment
        ): LiveData<LifecycleOwner> {
            return sessionsFragment.viewLifecycleOwnerLiveData
        }
    }
}
