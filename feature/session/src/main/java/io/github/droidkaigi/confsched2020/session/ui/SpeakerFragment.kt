package io.github.droidkaigi.confsched2020.session.ui

import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.FragmentSpeakerBinding
import io.github.droidkaigi.confsched2020.session.ui.item.SpeakerDetailItem
import io.github.droidkaigi.confsched2020.session.ui.item.SpeakerSessionItem
import io.github.droidkaigi.confsched2020.session.ui.viewmodel.SpeakerViewModel
import io.github.droidkaigi.confsched2020.util.AndroidRTransition
import io.github.droidkaigi.confsched2020.util.DaggerFragment
import io.github.droidkaigi.confsched2020.util.ProgressTimeLatch
import io.github.droidkaigi.confsched2020.util.autoCleared
import javax.inject.Inject

class SpeakerFragment : DaggerFragment(R.layout.fragment_speaker) {

    private var binding: FragmentSpeakerBinding by autoCleared()

    @Inject lateinit var speakerViewModelFactory: SpeakerViewModel.Factory
    private val speakerViewModel by assistedViewModels {
        speakerViewModelFactory.create(navArgs.speakerId)
    }

    @Inject lateinit var speakerDetailItemFactory: SpeakerDetailItem.Factory
    @Inject lateinit var speakerSessionItemFactory: SpeakerSessionItem.Factory

    private val navArgs: SpeakerFragmentArgs by navArgs()
    private var progressTimeLatch: ProgressTimeLatch by autoCleared()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(AndroidRTransition.move).apply {
                interpolator = AccelerateDecelerateInterpolator()
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()

        binding = FragmentSpeakerBinding.bind(view)

        progressTimeLatch = ProgressTimeLatch { showProgress ->
            binding.progressBar.isVisible = showProgress
        }.apply {
            loading = true
        }

        val groupAdapter = GroupAdapter<ViewHolder<*>>()
        binding.speakerRecycler.adapter = groupAdapter

        speakerViewModel.uiModel.distinctUntilChanged()
            .observe(viewLifecycleOwner) { uiModel: SpeakerViewModel.UiModel ->
                progressTimeLatch.loading = uiModel.isLoading
                val speaker = uiModel.speaker ?: return@observe
                val sessions = uiModel.sessions.takeIf { it.isNotEmpty() } ?: return@observe

                groupAdapter.update(
                    listOf(speakerDetailItemFactory.create(speaker, navArgs.transitionNameSuffix) {
                        startPostponedEnterTransition()
                    }) + sessions.map { speakerSessionItemFactory.create(it) }
                )
            }
    }
}

@Module
abstract class SpeakerFragmentModule {
    @Module
    companion object {
        @PageScope
        @JvmStatic @Provides fun providesLifecycleOwnerLiveData(
            speakerFragment: SpeakerFragment
        ): LiveData<LifecycleOwner> {
            return speakerFragment.viewLifecycleOwnerLiveData
        }
    }
}
