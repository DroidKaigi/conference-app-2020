package io.github.droidkaigi.confsched2020.session.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedActivityViewModels
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.Speaker
import io.github.droidkaigi.confsched2020.model.SpeechSession
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.FragmentSessionDetailWipBinding
import io.github.droidkaigi.confsched2020.session.ui.SessionDetailFragmentDirections.Companion.actionSessionToSpeaker
import io.github.droidkaigi.confsched2020.session.ui.SessionDetailFragmentDirections.Companion.actionSessionToSurvey
import io.github.droidkaigi.confsched2020.session.ui.item.SessionDetailDescriptionItem
import io.github.droidkaigi.confsched2020.session.ui.item.SessionDetailSpeakerItem
import io.github.droidkaigi.confsched2020.session.ui.item.SessionDetailSpeakerSubtitleItem
import io.github.droidkaigi.confsched2020.session.ui.item.SessionDetailTargetItem
import io.github.droidkaigi.confsched2020.session.ui.item.SessionDetailTitleItem
import io.github.droidkaigi.confsched2020.session.ui.item.SessionItem
import io.github.droidkaigi.confsched2020.session.ui.viewmodel.SessionDetailViewModel
import io.github.droidkaigi.confsched2020.session.ui.widget.SessionDetailItemDecoration
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel
import io.github.droidkaigi.confsched2020.util.DaggerFragment
import io.github.droidkaigi.confsched2020.util.ProgressTimeLatch
import io.github.droidkaigi.confsched2020.util.autoCleared
import javax.inject.Inject
import javax.inject.Provider

class SessionDetailFragment : DaggerFragment(R.layout.fragment_session_detail_wip) {

    private var binding: FragmentSessionDetailWipBinding by autoCleared()

    @Inject lateinit var systemViewModelFactory: Provider<SystemViewModel>
    private val systemViewModel by assistedActivityViewModels {
        systemViewModelFactory.get()
    }
    @Inject lateinit var sessionDetailViewModelFactory: SessionDetailViewModel.Factory
    private val sessionDetailViewModel by assistedViewModels {
        sessionDetailViewModelFactory.create(navArgs.sessionId)
    }

    private val navArgs: SessionDetailFragmentArgs by navArgs()
    @Inject lateinit var sessionItemFactory: SessionItem.Factory

    private var progressTimeLatch: ProgressTimeLatch by autoCleared()

    companion object {
        const val TRANSITION_NAME_SUFFIX = "detail"
    }

    @Inject
    lateinit var sessionDetailSpeakerSubtitleItemFactory: SessionDetailSpeakerSubtitleItem.Factory

    @Inject
    lateinit var sessionDetailDescriptionItemFactory: SessionDetailDescriptionItem.Factory

    @Inject
    lateinit var sessionDetailSpeakerItemFactory: SessionDetailSpeakerItem.Factory

    @Inject
    lateinit var sessionDetailTargetItemFactory: SessionDetailTargetItem.Factory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSessionDetailWipBinding.bind(view)

        progressTimeLatch = ProgressTimeLatch { showProgress ->
            binding.progressBar.isVisible = showProgress
        }.apply {
            loading = true
        }

        sessionDetailViewModel.uiModel
            .observe(viewLifecycleOwner) { uiModel: SessionDetailViewModel.UiModel ->
                uiModel.error?.let { systemViewModel.onError(it) }
                progressTimeLatch.loading = uiModel.isLoading
                uiModel.session
                    ?.let { session -> setupSessionViews(session) }
            }

        binding.bottomAppBar.setOnMenuItemClickListener {
            handleNavigation(it.itemId)
        }
    }

    private fun handleNavigation(@IdRes itemId: Int): Boolean {
        val navController = findNavController()
        return try {
            // ignore if current destination is selected
            if (navController.currentDestination?.id == itemId) return false
            val builder = NavOptions.Builder()
                .setEnterAnim(R.anim.fade_in)
                .setExitAnim(R.anim.fade_out)
                .setPopEnterAnim(R.anim.fade_in)
                .setPopExitAnim(R.anim.fade_out)
            val options = builder.build()
            navController.navigate(itemId, null, options)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    private fun setupSessionViews(session: Session) {
        if (binding.sessionDetailRecycler.adapter == null) {
            val adapter = GroupAdapter<ViewHolder<*>>()
            binding.sessionDetailRecycler.adapter = adapter
            binding.sessionDetailRecycler.layoutManager = LinearLayoutManager(context)
            context?.let {
                binding.sessionDetailRecycler.addItemDecoration(
                    SessionDetailItemDecoration(
                        adapter,
                        it
                    )
                )
            }
            adapter.add(SessionDetailTitleItem(session) {
                findNavController().navigate(actionSessionToSurvey(session.id))
            })
            adapter.add(sessionDetailDescriptionItemFactory.create(session))
            if (session.hasIntendedAudience)
                adapter.add(sessionDetailTargetItemFactory.create(session))
            if (session.hasSpeaker) {
                adapter.add(sessionDetailSpeakerSubtitleItemFactory.create())
                var firstSpeaker = true
                (session as? SpeechSession)?.speakers.orEmpty().indices.forEach { index ->
                    val speaker: Speaker =
                        (session as? SpeechSession)?.speakers?.getOrNull(index)
                            ?: return@forEach
                    adapter.add(sessionDetailSpeakerItemFactory.create(
                        speaker,
                        firstSpeaker
                    ) { extras ->
                        findNavController()
                            .navigate(
                                actionSessionToSpeaker(
                                    speaker.id,
                                    TRANSITION_NAME_SUFFIX
                                ), extras
                            )
                    }
                    )
                    firstSpeaker = false
                }
            }
        }
        binding.sessionFavorite.setOnClickListener {
            sessionDetailViewModel.favorite(session)
        }
        binding.session = session
    }
}

@Module
abstract class SessionDetailFragmentModule {
    @Module
    companion object {
        @PageScope
        @JvmStatic @Provides fun providesLifecycleOwnerLiveData(
            sessionDetailFragment: SessionDetailFragment
        ): LiveData<LifecycleOwner> {
            return sessionDetailFragment.viewLifecycleOwnerLiveData
        }
    }
}
