package io.github.droidkaigi.confsched2020.session_survey.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.navArgs
import com.wada811.databinding.dataBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import dagger.android.support.DaggerFragment
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedActivityViewModels
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import io.github.droidkaigi.confsched2020.session_survey.databinding.FragmentSessionSurveyBinding
import io.github.droidkaigi.confsched2020.session_survey.R
import io.github.droidkaigi.confsched2020.session_survey.ui.viewmodel.SessionSurveyViewModel
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel
import io.github.droidkaigi.confsched2020.util.ProgressTimeLatch
import javax.inject.Inject
import javax.inject.Provider

class SessionSurveyFragment : DaggerFragment() {

    @Inject
    lateinit var sessionSurveyModelFactory: SessionSurveyViewModel.Factory
    private val sessionSurveyViewModel: SessionSurveyViewModel by assistedViewModels {
        sessionSurveyModelFactory.create(navArgs.sessionId)
    }

    @Inject
    lateinit var systemViewModelProvider: Provider<SystemViewModel>
    private val systemViewModel: SystemViewModel by assistedActivityViewModels {
        systemViewModelProvider.get()
    }

    private val navArgs: SessionSurveyFragmentArgs by navArgs()
    private val binding: FragmentSessionSurveyBinding by dataBinding(R.layout.fragment_session_survey)

    private lateinit var progressTimeLatch: ProgressTimeLatch

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        progressTimeLatch = ProgressTimeLatch { showProgress ->
            binding.progressBar.isVisible = showProgress
        }.apply {
            loading = true
        }

        // TODO: Add SessionSurveyUI
    }
}

@Module
abstract class SessionSurveyFragmentModule {

    @Module
    companion object {

        @PageScope
        @JvmStatic
        @Provides
        fun providesLifeCycleLiveData(
            sessionSurveyFragment: SessionSurveyFragment
        ): LiveData<LifecycleOwner> {
            return sessionSurveyFragment.viewLifecycleOwnerLiveData
        }
    }
}