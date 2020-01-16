package io.github.droidkaigi.confsched2020.session.ui

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import coil.api.load
import coil.transform.CircleCropTransformation
import dagger.Module
import dagger.Provides
import dagger.android.support.DaggerFragment
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import io.github.droidkaigi.confsched2020.ext.getThemeColor
import io.github.droidkaigi.confsched2020.model.defaultLang
import io.github.droidkaigi.confsched2020.model.defaultTimeZoneOffset
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.FragmentSpeakerBinding
import io.github.droidkaigi.confsched2020.session.ui.SpeakerFragmentDirections.actionSpeakerToSessionDetail
import io.github.droidkaigi.confsched2020.session.ui.viewmodel.SpeakerViewModel
import io.github.droidkaigi.confsched2020.util.ProgressTimeLatch
import javax.inject.Inject

class SpeakerFragment : DaggerFragment() {

    private lateinit var binding: FragmentSpeakerBinding

    @Inject lateinit var speakerViewModelFactory: SpeakerViewModel.Factory
    private val speakerViewModel by assistedViewModels {
        speakerViewModelFactory.create(navArgs.speakerId)
    }

    private val navArgs: SpeakerFragmentArgs by navArgs()
    private lateinit var progressTimeLatch: ProgressTimeLatch

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_speaker,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressTimeLatch = ProgressTimeLatch { showProgress ->
            binding.progressBar.isVisible = showProgress
        }.apply {
            loading = true
        }

        binding.speakerDescription.movementMethod = LinkMovementMethod.getInstance()

        val placeHolder = VectorDrawableCompat.create(
            requireContext().resources,
            R.drawable.ic_person_outline_black_32dp,
            null
        )?.apply {
            setTint(
                requireContext().getThemeColor(R.attr.colorOnBackground)
            )
        }

        speakerViewModel.uiModel.distinctUntilChanged()
            .observe(viewLifecycleOwner) { uiModel: SpeakerViewModel.UiModel ->
                progressTimeLatch.loading = uiModel.isLoading
                val speaker = uiModel.speaker ?: return@observe
                val session = uiModel.session ?: return@observe
                binding.speaker = speaker
                binding.speechSession = session
                binding.lang = defaultLang()
                binding.time.text = session.timeSummary(defaultLang(), defaultTimeZoneOffset())

                binding.speakerImage.load(speaker.imageUrl) {
                    crossfade(true)
                    placeholder(placeHolder)
                    transformations(CircleCropTransformation())
                    lifecycle(viewLifecycleOwner)
                }

                binding.speakerSessionName.setOnClickListener {
                    findNavController().navigate(actionSpeakerToSessionDetail(session.id))
                }
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