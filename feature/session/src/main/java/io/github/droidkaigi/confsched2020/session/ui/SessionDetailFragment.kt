package io.github.droidkaigi.confsched2020.session.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.transition.Hold
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.GroupieViewHolder
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2020.di.Injectable
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedActivityViewModels
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import io.github.droidkaigi.confsched2020.ext.isShow
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.Speaker
import io.github.droidkaigi.confsched2020.model.SpeechSession
import io.github.droidkaigi.confsched2020.model.ThumbsUpCount
import io.github.droidkaigi.confsched2020.model.defaultLang
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.FragmentSessionDetailBinding
import io.github.droidkaigi.confsched2020.session.ui.SessionDetailFragmentDirections.Companion.actionSessionToChrome
import io.github.droidkaigi.confsched2020.session.ui.SessionDetailFragmentDirections.Companion.actionSessionToFloormap
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
import io.github.droidkaigi.confsched2020.ui.animation.MEDIUM_EXPAND_DURATION
import io.github.droidkaigi.confsched2020.ui.transition.fadeThrough
import javax.inject.Inject
import javax.inject.Provider

class SessionDetailFragment : Fragment(R.layout.fragment_session_detail), Injectable {

    @Inject lateinit var systemViewModelFactory: Provider<SystemViewModel>
    private val systemViewModel by assistedActivityViewModels {
        systemViewModelFactory.get()
    }
    @Inject lateinit var sessionDetailViewModelFactory: SessionDetailViewModel.Factory
    private val sessionDetailViewModel by assistedViewModels {
        sessionDetailViewModelFactory.create(navArgs.sessionId, navArgs.searchQuery)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = fadeThrough().apply {
            duration = MEDIUM_EXPAND_DURATION
        }
        exitTransition = Hold()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()

        val binding = FragmentSessionDetailBinding.bind(view)
        val adapter = GroupAdapter<GroupieViewHolder<*>>()
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

        binding.progressBar.show()

        sessionDetailViewModel.uiModel
            .observe(viewLifecycleOwner) { uiModel: SessionDetailViewModel.UiModel ->
                binding.progressBar.isShow = uiModel.isLoading
                uiModel.session
                    ?.let { session ->
                        setupSessionViews(
                            binding,
                            adapter,
                            session,
                            uiModel.showEllipsis,
                            uiModel.searchQuery,
                            uiModel.thumbsUpCount
                        )
                    }
                uiModel.error?.let { systemViewModel.onError(it) }
            }

        binding.bottomAppBar.run {
            doOnNextLayout {
                measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
            }
            setOnMenuItemClickListener { menuItem ->
                val session = binding.session ?: return@setOnMenuItemClickListener true

                when (menuItem.itemId) {
                    R.id.session_share -> {
                        val sessionId = session.id.id
                        val url = resources.getString(R.string.session_share_url).format(sessionId)
                        systemViewModel.shareURL(
                            activity = requireActivity(),
                            url = url
                        )
                    }
                    R.id.floormap -> {
                        val directions = actionSessionToFloormap(session.room)
                        findNavController().navigate(directions)
                    }
                    R.id.session_calendar -> {
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
        adapter: GroupAdapter<GroupieViewHolder<*>>,
        session: Session,
        showEllipsis: Boolean,
        searchQuery: String?,
        thumbsUpCount: ThumbsUpCount
    ) {
        binding.sessionDetailRecycler.transitionName =
            "${session.id}-${navArgs.transitionNameSuffix}"

        val items = mutableListOf<Group>()
        items += sessionDetailTitleItemFactory.create(
            session,
            searchQuery,
            viewLifecycleOwner.lifecycleScope,
            thumbsUpCount
        ) {
            sessionDetailViewModel.thumbsUp(session)
        }
        items += sessionDetailDescriptionItemFactory.create(
            session,
            showEllipsis,
            searchQuery
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
                                    TRANSITION_NAME_SUFFIX,
                                    searchQuery
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
        startPostponedEnterTransition()

        binding.sessionFavorite.setOnClickListener {
            sessionDetailViewModel.favorite(session)
        }
        binding.session = session
    }
}

@Module
abstract class SessionDetailFragmentModule {
    companion object {
        @PageScope
        @Provides
        fun providesLifecycleOwnerLiveData(
            sessionDetailFragment: SessionDetailFragment
        ): LiveData<LifecycleOwner> {
            return sessionDetailFragment.viewLifecycleOwnerLiveData
        }
    }
}
