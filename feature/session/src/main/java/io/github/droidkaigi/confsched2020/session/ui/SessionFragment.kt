package io.github.droidkaigi.confsched2020.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.observe
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import dagger.android.support.DaggerFragment
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.model.SessionContents
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.FragmentSessionBinding
import io.github.droidkaigi.confsched2020.session.ui.item.SessionItem
import io.github.droidkaigi.confsched2020.session.ui.viewmodel.SessionViewModel
import io.github.droidkaigi.confsched2020.util.ProgressTimeLatch
import me.tatarka.injectedvmprovider.InjectedViewModelProviders
import javax.inject.Inject
import javax.inject.Provider

class SessionFragment : DaggerFragment() {

    private lateinit var binding: FragmentSessionBinding

    @Inject lateinit var sessionViewModelProvider: Provider<SessionViewModel>
    private val sessionViewModel: SessionViewModel by lazy {
        InjectedViewModelProviders.of(requireActivity())[sessionViewModelProvider]
    }
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
        sessionViewModel.sessionContents.observe(viewLifecycleOwner) { sessions: SessionContents ->
            groupAdapter.update(sessions.sessions.map { SessionItem(it) })
        }
    }
}

@Module
abstract class SessionFragmentModule {
    @Module
    companion object {
        @PageScope
        @JvmStatic @Provides fun providesLifecycle(
            sessionFragment: SessionFragment
        ): Lifecycle {
            return sessionFragment.viewLifecycleOwner.lifecycle
        }
    }
}
