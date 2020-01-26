package io.github.droidkaigi.confsched2020.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import dagger.android.support.DaggerFragment
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedActivityViewModels
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.Speaker
import io.github.droidkaigi.confsched2020.model.SpeechSession
import io.github.droidkaigi.confsched2020.model.defaultLang
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.FragmentSessionDetailBinding
import io.github.droidkaigi.confsched2020.session.ui.SessionDetailFragmentDirections.Companion.actionSessionToChrome
import io.github.droidkaigi.confsched2020.session.ui.SessionDetailFragmentDirections.Companion.actionSessionToSpeaker
import io.github.droidkaigi.confsched2020.session.ui.item.SessionDetailDescriptionItem
import io.github.droidkaigi.confsched2020.session.ui.item.SessionDetailMaterialItem
import io.github.droidkaigi.confsched2020.session.ui.item.SessionDetailSpeakerItem
import io.github.droidkaigi.confsched2020.session.ui.item.SessionDetailSpeakerSubtitleItem
import io.github.droidkaigi.confsched2020.session.ui.item.SessionDetailTargetItem
import io.github.droidkaigi.confsched2020.session.ui.item.SessionDetailTitleItem
import io.github.droidkaigi.confsched2020.session.ui.item.SessionItem
import io.github.droidkaigi.confsched2020.session.ui.viewmodel.SessionDetailViewModel
import io.github.droidkaigi.confsched2020.session.ui.widget.SessionDetailItemDecoration
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel
import io.github.droidkaigi.confsched2020.util.ProgressTimeLatch
import javax.inject.Inject
import javax.inject.Provider

class SessionDetailFragment : DaggerFragment() {

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

    companion object {
        const val TRANSITION_NAME_SUFFIX = "detail"
    }

    @Inject
    lateinit var sessionDetailTitleItemFactory: SessionDetailTitleItem.Factory

    @Inject
    lateinit var sessionDetailDescriptionItemFactory: SessionDetailDescriptionItem.Factory

    @Inject
    lateinit var sessionDetailTargetItemFactory: SessionDetailTargetItem.Factory

    @Inject
    lateinit var sessionDetailSpeakerSubtitleItemFactory: SessionDetailSpeakerSubtitleItem.Factory

    @Inject
    lateinit var sessionDetailSpeakerItemFactory: SessionDetailSpeakerItem.Factory

    @Inject
    lateinit var sessionDetailMaterialItemFactory: SessionDetailMaterialItem.Factory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_session_detail,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSessionDetailBinding.bind(view)
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
        val itemAnimator = binding.sessionDetailRecycler.itemAnimator
        if (itemAnimator is SimpleItemAnimator) {
            itemAnimator.supportsChangeAnimations = false
        }

        val progressTimeLatch = ProgressTimeLatch { showProgress ->
            binding.progressBar.isVisible = showProgress
        }.apply {
            loading = true
        }

        sessionDetailViewModel.uiModel
            .observe(viewLifecycleOwner) { uiModel: SessionDetailViewModel.UiModel ->
                uiModel.error?.let { systemViewModel.onError(it) }
                progressTimeLatch.loading = uiModel.isLoading
                uiModel.session
                    ?.let { session ->
                        setupSessionViews(
                            binding,
                            adapter,
                            session,
                            uiModel.showEllipsis
                        )
                    }
            }

        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.session_share -> {
                    // do something
                }
                R.id.session_calendar -> {
                    val session = binding.session ?: return@setOnMenuItemClickListener true
                    systemViewModel.sendEventToCalendar(
                        activity = requireActivity(),
                        title = session.title.getByLang(defaultLang()),
                        location = session.room.name.getByLang(defaultLang()),
                        startDateTime = session.startTime,
                        endDateTime = session.endTime
                    )
                }
                else -> {
                    handleNavigation(menuItem.itemId)
                }
            }
            return@setOnMenuItemClickListener true
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

    private fun setupSessionViews(
        binding: FragmentSessionDetailBinding,
        adapter: GroupAdapter<ViewHolder<*>>,
        session: Session,
        showEllipsis: Boolean
    ) {
        val items = mutableListOf<Group>()
        items += sessionDetailTitleItemFactory.create(session)
        items += sessionDetailDescriptionItemFactory.create(
            session,
            showEllipsis
        ) { sessionDetailViewModel.expandDescription() }
        if (session.hasIntendedAudience)
            items += sessionDetailTargetItemFactory.create(session)
        if (session.hasSpeaker) {
            items += sessionDetailSpeakerSubtitleItemFactory.create()
            var firstSpeaker = true
            (session as? SpeechSession)?.speakers.orEmpty().indices.forEach { index ->
                val speaker: Speaker =
                    (session as? SpeechSession)?.speakers?.getOrNull(index)
                        ?: return@forEach
                items +=
                    sessionDetailSpeakerItemFactory.create(
                        speaker,
                        firstSpeaker
                    ) { extras ->
                        findNavController()
                            .navigate(
                                actionSessionToSpeaker(
                                    speaker.id,
                                    TRANSITION_NAME_SUFFIX
                                ),
                                extras
                            )
                    }
                firstSpeaker = false
            }
        }
        items += sessionDetailMaterialItemFactory.create(
            session,
            object : SessionDetailMaterialItem.Listener {
                override fun onClickMovie(movieUrl: String) {
                    findNavController().navigate(actionSessionToChrome(movieUrl))
                }

                override fun onClickSlide(slideUrl: String) {
                    findNavController().navigate(actionSessionToChrome(slideUrl))
                }
            }
        )
        adapter.update(items)
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
        @JvmStatic @Provides
        fun providesLifecycleOwnerLiveData(
            sessionDetailFragment: SessionDetailFragment
        ): LiveData<LifecycleOwner> {
            return sessionDetailFragment.viewLifecycleOwnerLiveData
        }
    }
}
