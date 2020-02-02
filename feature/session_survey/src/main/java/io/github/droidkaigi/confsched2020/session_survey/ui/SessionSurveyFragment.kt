package io.github.droidkaigi.confsched2020.session_survey.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.navArgs
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2020.di.Injectable
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedActivityViewModels
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import io.github.droidkaigi.confsched2020.session_survey.R
import io.github.droidkaigi.confsched2020.session_survey.databinding.FragmentSessionSurveyBinding
import io.github.droidkaigi.confsched2020.session_survey.ui.viewmodel.SessionSurveyViewModel
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel
import javax.inject.Inject
import javax.inject.Provider

class SessionSurveyFragment : Fragment(R.layout.fragment_session_survey), Injectable {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSessionSurveyBinding.bind(view)
        binding.progressBar.show()

        // TODO: Add SessionSurveyUI
    }
}

@Module
abstract class SessionSurveyFragmentModule {

    companion object {

        @PageScope
        @Provides
        fun providesLifecycleOwnerLiveData(
            sessionSurveyFragment: SessionSurveyFragment
        ): LiveData<LifecycleOwner> {
            return sessionSurveyFragment.viewLifecycleOwnerLiveData
        }
    }
}
