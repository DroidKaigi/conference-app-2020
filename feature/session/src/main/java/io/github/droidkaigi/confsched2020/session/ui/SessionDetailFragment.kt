package io.github.droidkaigi.confsched2020.session.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.text.inSpans
import androidx.annotation.IdRes
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionManager
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import coil.Coil
import coil.api.load
import coil.transform.CircleCropTransformation
import com.google.android.material.chip.Chip
import dagger.Module
import dagger.Provides
import dagger.android.support.DaggerFragment
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedActivityViewModels
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import io.github.droidkaigi.confsched2020.ext.getThemeColor
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.Speaker
import io.github.droidkaigi.confsched2020.model.SpeechSession
import io.github.droidkaigi.confsched2020.model.defaultLang
import io.github.droidkaigi.confsched2020.model.defaultTimeZoneOffset
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.FragmentSessionDetailBinding
import io.github.droidkaigi.confsched2020.session.ui.SessionDetailFragmentDirections.actionSessionToSpeaker
import io.github.droidkaigi.confsched2020.session.ui.SessionDetailFragmentDirections.actionSessionToSurvey
import io.github.droidkaigi.confsched2020.session.ui.item.SessionItem
import io.github.droidkaigi.confsched2020.session.ui.viewmodel.SessionDetailViewModel
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel
import io.github.droidkaigi.confsched2020.util.ProgressTimeLatch
import io.github.droidkaigi.confsched2020.util.autoCleared
import javax.inject.Inject
import javax.inject.Provider

private const val ELLIPSIS_LINE_COUNT = 6

class SessionDetailFragment : DaggerFragment() {

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
    private var showEllipsis = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_session_detail,
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
        binding.sessionFavorite.setOnClickListener {
            sessionDetailViewModel.favorite(session)
        }
        binding.survey.setOnClickListener {
            findNavController().navigate(actionSessionToSurvey(session.id))
        }
        binding.session = session
        setupSessionDescription(session.desc)
        binding.speechSession = (session as? SpeechSession)
        binding.lang = defaultLang()
        binding.time.text = session.timeSummary(defaultLang(), defaultTimeZoneOffset())
        if (session is SpeechSession) {
            val langLabel = session.lang.text.getByLang(defaultLang())
            val categoryLabel = session.category.name.getByLang(defaultLang())
            val newTag = "$categoryLabel:$categoryLabel"
            val savedTag = binding.tags.tag
            if (savedTag != newTag) {
                binding.tags.removeAllViews()
                binding.tags.addView(Chip(context).apply {
                    text = categoryLabel
                    isClickable = false
                })
                binding.tags.addView(Chip(context).apply {
                    text = langLabel
                    isClickable = false
                })
                binding.tags.tag = newTag
            }
        }
        binding.speakers.bindSpeaker(session)
    }

    private fun setupSessionDescription(fullDescription: String) {
        val textView = binding.sessionDescription
        textView.doOnPreDraw {
            textView.text = fullDescription
            //Return here if not more than the specified number of rows
            if (!(textView.lineCount > ELLIPSIS_LINE_COUNT && showEllipsis)) return@doOnPreDraw
            val lastLineStartPosition = textView.layout.getLineStart(ELLIPSIS_LINE_COUNT - 1)
            val ellipsis = getString(R.string.ellipsis_label)
            val lastLineText = TextUtils.ellipsize(
                fullDescription.substring(lastLineStartPosition),
                textView.paint,
                textView.width - textView.paint.measureText(ellipsis),
                TextUtils.TruncateAt.END
            )
            val ellipsisColor = ContextCompat.getColor(requireContext(), R.color.design_default_color_secondary)
            val onClickListener = {
                TransitionManager.beginDelayedTransition(binding.sessionLayout)
                textView.text = fullDescription
                showEllipsis = !showEllipsis
            }
            val detailText = fullDescription.substring(0, lastLineStartPosition) + lastLineText
            val text = buildSpannedString {
                clickableSpan(onClickListener, {
                    append(detailText)
                    color(ellipsisColor) {
                        append(ellipsis)
                    }
                })
            }
            textView.setText(text, TextView.BufferType.SPANNABLE)
            textView.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun SpannableStringBuilder.clickableSpan(
        clickListener: () -> Unit,
        builderAction: SpannableStringBuilder.() -> Unit
    ) {
        inSpans(object : ClickableSpan() {
            override fun onClick(widget: View) {
                clickListener()
            }

            override fun updateDrawState(ds: TextPaint) {
                // nothing
            }
        }, builderAction)
    }

    private fun ViewGroup.bindSpeaker(session: Session) {
        removeAllViews()
        (session as? SpeechSession)?.speakers.orEmpty().indices.forEach { index ->
            val speaker: Speaker =
                (session as? SpeechSession)?.speakers?.getOrNull(index) ?: return@forEach
            val speakerView = layoutInflater.inflate(
                R.layout.layout_speaker, this, false
            ) as ViewGroup
            speakerView.setOnClickListener {
                findNavController().navigate(actionSessionToSpeaker(speaker.id))
            }
            val textView: TextView = speakerView.findViewById(R.id.speaker)
            bindSpeakerData(speaker, textView)

            addView(speakerView)
        }
    }

    private fun bindSpeakerData(
        speaker: Speaker,
        textView: TextView
    ) {
        textView.text = speaker.name
//        setHighlightText(textView, query)
        val imageUrl = speaker.imageUrl
        val context = textView.context
        val placeHolder = run {
            VectorDrawableCompat.create(
                context.resources,
                R.drawable.ic_person_outline_black_32dp,
                null
            )?.apply {
                setTint(
                    context.getThemeColor(R.attr.colorOnBackground)
                )
            }
        }?.also {
            textView.setLeftDrawable(it)
        }

        Coil.load(context, imageUrl) {
            crossfade(true)
            placeholder(placeHolder)
            transformations(CircleCropTransformation())
            lifecycle(viewLifecycleOwner)
            target {
                textView.setLeftDrawable(it)
            }
        }
    }

    fun TextView.setLeftDrawable(drawable: Drawable) {
        val res = context.resources
        val widthDp = 32
        val heightDp = 32
        val widthPx = (widthDp * res.displayMetrics.density).toInt()
        val heightPx = (heightDp * res.displayMetrics.density).toInt()
        drawable.setBounds(0, 0, widthPx, heightPx)
        setCompoundDrawables(
            drawable, null, null, null
        )
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
