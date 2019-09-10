package io.github.droidkaigi.confsched2020.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.fragment.navArgs
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
import io.github.droidkaigi.confsched2020.session.ui.viewmodel.SessionDetailViewModel
import io.github.droidkaigi.confsched2020.util.ProgressTimeLatch
import javax.inject.Inject

class SessionDetailFragment : DaggerFragment() {

    private lateinit var binding: FragmentSessionBinding

    @Inject lateinit var sessionDetailViewModelFactory: SessionDetailViewModel.Factory
    private val navArgs: SessionDetailFragmentArgs by navArgs()
    private val sessionDetailViewModel by assistedViewModels {
        sessionDetailViewModelFactory.create(navArgs.sessionId)
    }

    @Inject lateinit var navController: NavController
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
        sessionDetailViewModel.uiModel.observe(viewLifecycleOwner) { uiModel: SessionDetailViewModel.UiModel ->
            progressTimeLatch.loading = uiModel.isLoading
            if (uiModel.session != null) {
                Toast.makeText(context, uiModel.session.toString(), Toast.LENGTH_LONG).show()
            }
            showError(uiModel.error)
        }
    }

    private fun showError(error: SessionDetailViewModel.UiModel.Error) {
        when (error) {
            is SessionDetailViewModel.UiModel.Error.FailLoadSessions -> {
                error.e.printStackTrace()
                Snackbar.make(requireView(), "Fail to load sessions", Snackbar.LENGTH_LONG).show()
            }
            is SessionDetailViewModel.UiModel.Error.FailFavorite -> {
                error.e.printStackTrace()
                Snackbar.make(requireView(), "Fail to toggle favorite", Snackbar.LENGTH_LONG).show()
            }
            SessionDetailViewModel.UiModel.Error.None -> Unit
        }
    }
}

@Module
abstract class SessionDetailFragmentModule {
    @Module
    companion object {
        @PageScope
        @JvmStatic @Provides fun providesLifeCycleLiveData(
            sessionDetailFragment: SessionDetailFragment
        ): LiveData<LifecycleOwner> {
            return sessionDetailFragment.viewLifecycleOwnerLiveData
        }
    }
}
