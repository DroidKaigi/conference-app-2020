package io.github.droidkaigi.confsched2020.session.ui

import android.graphics.drawable.Drawable
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
import io.github.droidkaigi.confsched2020.session.databinding.FragmentSessionDetailBinding
import io.github.droidkaigi.confsched2020.session.ui.SessionDetailFragmentDirections.Companion.actionSessionToChrome
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

class SessionDetailFragment : DaggerFragment(R.layout.fragment_session_detail) {

    private var binding: FragmentSessionDetailBinding by autoCleared()

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
    lateinit var sessionDetailTitleItemFactory: SessionDetailTitleItem.Factory

    @Inject
    lateinit var sessionDetailDescriptionItemFactory: SessionDetailDescriptionItem.Factory

    @Inject
    lateinit var sessionDetailTargetItemFactory: SessionDetailTargetItem.Factory

    @Inject
    lateinit var sessionDetailSpeakerSubtitleItemFactory: SessionDetailSpeakerSubtitleItem.Factory

    @Inject
    lateinit var sessionDetailSpeakerItemFactory: SessionDetailSpeakerItem.Factory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSessionDetailBinding.bind(view)

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
            adapter.add(sessionDetailTitleItemFactory.create(session) {
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
//        binding.sessionFavorite.setOnClickListener {s
//            sessionDetailViewModel.favorite(session)
//        }
        binding.session = session
//        setupSessionDescription(session.desc)
//        binding.speechSession = (session as? SpeechSession)
//        binding.lang = defaultLang()
//        if (session is SpeechSession) {
//            val langLabel = session.lang.text.getByLang(defaultLang())
//            val categoryLabel = session.category.name.getByLang(defaultLang())
//            val newTag = "$categoryLabel:$categoryLabel"
//            val savedTag = binding.tags.tag
//            if (savedTag != newTag) {
//                binding.tags.removeAllViews()
//                binding.tags.addView(Chip(context).apply {
//                    text = categoryLabel
//                    isClickable = false
//                })
//                binding.tags.addView(Chip(context).apply {
//                    text = langLabel
//                    isClickable = false
//                })
//                binding.tags.tag = newTag
//            }
//        }
//        binding.speakers.bindSpeaker(session)
        //       setUpMaterialData(session)
    }

//    private fun setupSessionDescription(fullDescription: String) {
//        val textView = binding.sessionDescription
//        textView.doOnPreDraw {
//            textView.text = fullDescription
//            //Return here if not more than the specified number of rows
//            if (!(textView.lineCount > ELLIPSIS_LINE_COUNT && showEllipsis)) return@doOnPreDraw
//            val lastLineStartPosition = textView.layout.getLineStart(ELLIPSIS_LINE_COUNT - 1)
//            val ellipsis = getString(R.string.ellipsis_label)
//            val lastLineText = TextUtils.ellipsize(
//                fullDescription.substring(lastLineStartPosition),
//                textView.paint,
//                textView.width - textView.paint.measureText(ellipsis),
//                TextUtils.TruncateAt.END
//            )
//            val ellipsisColor =
//                ContextCompat.getColor(requireContext(), R.color.design_default_color_secondary)
//            val onClickListener = {
//                TransitionManager.beginDelayedTransition(binding.sessionLayout)
//                textView.text = fullDescription
//                showEllipsis = !showEllipsis
//            }
//            val detailText = fullDescription.substring(0, lastLineStartPosition) + lastLineText
//            val text = buildSpannedString {
//                clickableSpan(onClickListener, {
//                    append(detailText)
//                    color(ellipsisColor) {
//                        append(ellipsis)
//                    }
//                })
//            }
//            textView.setText(text, TextView.BufferType.SPANNABLE)
//            textView.movementMethod = LinkMovementMethod.getInstance()
//        }
//    }
//
//    private fun SpannableStringBuilder.clickableSpan(
//        clickListener: () -> Unit,
//        builderAction: SpannableStringBuilder.() -> Unit
//    ) {
//        inSpans(object : ClickableSpan() {
//            override fun onClick(widget: View) {
//                clickListener()
//            }
//
//            override fun updateDrawState(ds: TextPaint) {
//                // nothing
//            }
//        }, builderAction)
//    }
//
//    private fun ViewGroup.bindSpeaker(session: Session) {
//        removeAllViews()
//        (session as? SpeechSession)?.speakers.orEmpty().indices.forEach { index ->
//            val speaker: Speaker =
//                (session as? SpeechSession)?.speakers?.getOrNull(index) ?: return@forEach
//            val speakerView = layoutInflater.inflate(
//                R.layout.layout_speaker_session_detail, this, false
//            ) as ViewGroup
//            val speakerNameView = speakerView.findViewById<TextView>(R.id.speaker)
//            val speakerImageView = speakerView.findViewById<ImageView>(R.id.speaker_image)
//            speakerImageView.transitionName = "${speaker.id}-${TRANSITION_NAME_SUFFIX}"
//            speakerView.setOnClickListener {
//                val extras = FragmentNavigatorExtras(
//                    speakerImageView to speakerImageView.transitionName
//                )
//            }
//            adapter.add(sessionDetailTitleItemFactory.create(session) {
//                findNavController().navigate(actionSessionToSurvey(session.id))
//            })
//            adapter.add(sessionDetailDescriptionItemFactory.create(session))
//            if (session.hasIntendedAudience)
//                adapter.add(sessionDetailTargetItemFactory.create(session))
//            if (session.hasSpeaker) {
//                adapter.add(sessionDetailSpeakerSubtitleItemFactory.create())
//                var firstSpeaker = true
//                (session as? SpeechSession)?.speakers.orEmpty().indices.forEach { index ->
//                    val speaker: Speaker =
//                        (session as? SpeechSession)?.speakers?.getOrNull(index)
//                            ?: return@forEach
//                    adapter.add(sessionDetailSpeakerItemFactory.create(
//                        speaker,
//                        firstSpeaker
//                    ) { extras ->
//                        findNavController()
//                            .navigate(
//                                actionSessionToSpeaker(
//                                    speaker.id,
//                                    TRANSITION_NAME_SUFFIX
//                                ), extras
//                            )
//                    }
//                    )
//                    firstSpeaker = false
//                }
//            }
//        }
//        binding.sessionFavorite.setOnClickListener {
//            sessionDetailViewModel.favorite(session)
//        }
//        binding.session = session
//    }
//
//    private fun setUpMaterialData(session: Session) {
//        if (session is SpeechSession) {
//            session.videoUrl?.let {
//                setUpMovieView(it)
//            } ?: setUpNoMovieView()
//            session.slideUrl?.let {
//                setUpSlideView(it)
//            } ?: setUpNoSlideView()
//            return
//        }
//        setUpNoMovieView()
//        setUpNoSlideView()
//    }
//
//    private fun setUpNoMovieView() {
//        val icVideo = ContextCompat.getDrawable(requireContext(), R.drawable.ic_video_24dp)
//        icVideo?.let { binding.movie.setLeftDrawable(it, 24) }
//    }
//
//    private fun setUpMovieView(movieUrl: String) {
//        val icVideo =
//            ContextCompat.getDrawable(requireContext(), R.drawable.ic_video_light_blue_24dp)
//        icVideo?.let { binding.movie.setLeftDrawable(it, 24) }
//        binding.movie.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_blue_300))
//        binding.movie.setOnClickListener {
//            findNavController().navigate(actionSessionToChrome(movieUrl))
//        }
//    }
//
//    private fun setUpNoSlideView() {
//        val icSlide = ContextCompat.getDrawable(requireContext(), R.drawable.ic_slide_24dp)
//        icSlide?.let { binding.slide.setLeftDrawable(it, 24) }
//    }
//
//    private fun setUpSlideView(slideUrl: String) {
//        val icSlide =
//            ContextCompat.getDrawable(requireContext(), R.drawable.ic_slide_light_blue_24dp)
//        icSlide?.let { binding.slide.setLeftDrawable(it, 24) }
//        binding.slide.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_blue_300))
//        binding.slide.setOnClickListener {
//            findNavController().navigate(actionSessionToChrome(slideUrl))
//        }
//    }
//
//    private fun TextView.setLeftDrawable(drawable: Drawable, sizeDp: Int = 32) {
//        val res = context.resources
//        val widthPx = (sizeDp * res.displayMetrics.density).toInt()
//        val heightPx = (sizeDp * res.displayMetrics.density).toInt()
//        drawable.setBounds(0, 0, widthPx, heightPx)
//        setCompoundDrawables(
//            drawable, null, null, null
//        )
//    }
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
