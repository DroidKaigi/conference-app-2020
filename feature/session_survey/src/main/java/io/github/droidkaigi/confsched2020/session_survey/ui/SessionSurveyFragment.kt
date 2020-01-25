package io.github.droidkaigi.confsched2020.session_survey.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.navArgs
import dagger.Module
import dagger.Provides
import dagger.android.support.DaggerFragment
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedActivityViewModels
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import io.github.droidkaigi.confsched2020.session_survey.R
import io.github.droidkaigi.confsched2020.session_survey.databinding.FragmentSessionSurveyBinding
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_session_survey,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSessionSurveyBinding.bind(view)
        val progressTimeLatch = ProgressTimeLatch { showProgress ->
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
        fun providesLifecycleOwnerLiveData(
            sessionSurveyFragment: SessionSurveyFragment
        ): LiveData<LifecycleOwner> {
            return sessionSurveyFragment.viewLifecycleOwnerLiveData
        }
    }
}
