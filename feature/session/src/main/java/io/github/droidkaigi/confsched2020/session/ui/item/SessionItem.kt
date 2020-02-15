package io.github.droidkaigi.confsched2020.session.ui.item

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import coil.Coil
import coil.api.load
import coil.request.RequestDisposable
import coil.transform.CircleCropTransformation
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.databinding.GroupieViewHolder
import io.github.droidkaigi.confsched2020.ext.getThemeColor
import io.github.droidkaigi.confsched2020.model.LocaledString
import io.github.droidkaigi.confsched2020.model.ServiceSession
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.Speaker
import io.github.droidkaigi.confsched2020.model.SpeechSession
import io.github.droidkaigi.confsched2020.model.defaultLang
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.ItemSessionBinding
import io.github.droidkaigi.confsched2020.session.ui.MainSessionsFragmentDirections.Companion.actionSessionToSessionDetail
import io.github.droidkaigi.confsched2020.session.ui.MainSessionsFragmentDirections.Companion.actionSessionToSpeaker
import io.github.droidkaigi.confsched2020.session.ui.viewmodel.SessionsViewModel
import io.github.droidkaigi.confsched2020.ui.ProfilePlaceholderCreator
import io.github.droidkaigi.confsched2020.util.lazyWithParam
import java.util.regex.Pattern
import kotlin.math.max

class SessionItem @AssistedInject constructor(
    @Assisted private val session: Session,
    @Assisted private val sessionsViewModel: SessionsViewModel,
    @Assisted private val searchQuery: String?,
    private val lifecycleOwnerLiveData: LiveData<LifecycleOwner>
) : BindableItem<ItemSessionBinding>(session.id.hashCode().toLong()) {

    private val imageRequestDisposables = mutableListOf<RequestDisposable>()

    private val layoutInflater by lazyWithParam<Context, LayoutInflater> { context ->
        LayoutInflater.from(context)
    }

    private val placeholder by lazyWithParam<Context, VectorDrawableCompat?> { context ->
        ProfilePlaceholderCreator.create(context)
    }

    companion object {
        private const val TRANSITION_NAME_SUFFIX = "session"
    }

    override fun getLayout(): Int = R.layout.item_session

    override fun bind(viewBinding: ItemSessionBinding, position: Int) {
        with(viewBinding) {
            favorite.setOnClickListener {
                favorite.isSelected = !favorite.isSelected
                sessionsViewModel.favorite(session)
            }
            bindFavorite(session.isFavorited, favorite)
            root.setOnClickListener {
                val extra = FragmentNavigatorExtras(
                    itemRoot to itemRoot.transitionName
                )
                root.findNavController()
                    .navigate(
                        actionSessionToSessionDetail(
                            session.id,
                            TRANSITION_NAME_SUFFIX,
                            searchQuery
                        ),
                        extra
                    )
            }
            itemRoot.transitionName = "${session.id}-$TRANSITION_NAME_SUFFIX"
            live.isVisible = session.isOnGoing
            bindSessionMessage(session, viewBinding)
            title.text = session.title.currentLangString
            title.setSearchHighlight()
            room.text = session.minutesRoom(defaultLang())
            imageRequestDisposables.clear()
            speakers.bindSpeaker()
        }
    }

    override fun bind(
        viewBinding: ItemSessionBinding,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            bind(viewBinding, position)
        } else {
            payloads.distinct().forEach { payload ->
                when (payload) {
                    is ItemPayload.FavoritePayload -> {
                        bindFavorite(payload.isFavorited, viewBinding.favorite)
                    }
                }
            }
        }
    }

    private fun bindFavorite(
        isFavorited: Boolean,
        imageButton: ImageButton
    ) {
        imageButton.isSelected = isFavorited
    }

    private fun bindSessionMessage(
        session: Session,
        viewBinding: ItemSessionBinding
    ) {
        when (session) {
            is SpeechSession -> {
                viewBinding.sessionMessage.text = session.message?.getByLang(defaultLang())
                viewBinding.sessionMessage.setSearchHighlight()
                viewBinding.sessionMessage.isVisible = session.hasMessage
            }
            is ServiceSession -> {
                viewBinding.sessionMessage.isVisible = false
            }
        }
//        Test Code
//        viewBinding.sessionMessage.text = "セッション部屋がRoom1からRoom3に変更になりました（サンプル）"
//        viewBinding.sessionMessage.isVisible = true
    }

    private fun ViewGroup.bindSpeaker() {
        (
            0 until max(
                size, (session as? SpeechSession)?.speakers.orEmpty().size
            )
            ).forEach { index ->
            val existSpeakerView = getChildAt(index) as? ViewGroup
            val speaker: Speaker? = (session as? SpeechSession)?.speakers?.getOrNull(index)
            if (speaker == null) {
                // Cache for performance
                existSpeakerView?.isVisible = false
                return@forEach
            }
            val speakerView = if (existSpeakerView == null) {
                // NOTE: attachToRoot: true changes return value. https://stackoverflow.com/q/41491744/1474113
                val view = layoutInflater.get(context).inflate(
                    R.layout.layout_speaker, this, false
                ) as ViewGroup
                addView(view)
                view
            } else {
                existSpeakerView.isVisible = true
                existSpeakerView
            }
            val speakerNameView = speakerView.findViewById<TextView>(R.id.speaker)
            val speakerImageView = speakerView.findViewById<ImageView>(R.id.speaker_image)
            speakerImageView.transitionName = "${speaker.id}-$TRANSITION_NAME_SUFFIX"
            speakerView.setOnClickListener {
                val extras = FragmentNavigatorExtras(
                    speakerImageView to speakerImageView.transitionName
                )
                it.findNavController().navigate(
                    actionSessionToSpeaker(
                        speaker.id,
                        TRANSITION_NAME_SUFFIX,
                        null
                    ),
                    extras
                )
            }
            bindSpeakerData(speaker, speakerNameView, speakerImageView)
        }
    }

    private fun bindSpeakerData(
        speaker: Speaker,
        speakerNameView: TextView,
        speakerImageView: ImageView
    ) {
        speakerNameView.text = speaker.name
        speakerNameView.setSearchHighlight()
        val imageUrl = speaker.imageUrl
        val context = speakerNameView.context
        val placeholder = placeholder.get(context)?.also {
            speakerImageView.setImageDrawable(it)
        }

        imageRequestDisposables += Coil.load(context, imageUrl) {
            crossfade(true)
            placeholder(placeholder)
            transformations(CircleCropTransformation())
            lifecycle(lifecycleOwnerLiveData.value)
            target {
                speakerImageView.setImageDrawable(it)
            }
        }
    }

    override fun unbind(viewHolder: GroupieViewHolder<ItemSessionBinding>) {
        super.unbind(viewHolder)
        imageRequestDisposables.forEach { it.dispose() }
    }

    private fun TextView.setSearchHighlight() {
        if (searchQuery.isNullOrEmpty()) return
        val highlightColor = context.getThemeColor(R.attr.colorSecondary)
        val pattern = Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(text)
        val spannableStringBuilder = SpannableStringBuilder(text)
        while (matcher.find()) {
            spannableStringBuilder.setSpan(
                BackgroundColorSpan(highlightColor),
                matcher.start(),
                matcher.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        text = spannableStringBuilder
    }

    fun startSessionDate(): String = session.startDayText

    fun startSessionTime(): String = session.startTimeText

    fun title(): LocaledString = session.title

    override fun getChangePayload(newItem: Item<*>): Any? {
        return when {
            newItem !is SessionItem -> null
            isChangeFavorited(newItem) -> ItemPayload.FavoritePayload(newItem.session.isFavorited)
            else -> null
        }
    }

    private fun isChangeFavorited(newItem: SessionItem): Boolean {
        return session.isFavorited != newItem.session.isFavorited
    }

    override fun hasSameContentAs(other: Item<*>): Boolean =
        session == (other as? SessionItem)?.session

    private sealed class ItemPayload {
        data class FavoritePayload(val isFavorited: Boolean) : ItemPayload()
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            session: Session,
            sessionsViewModel: SessionsViewModel,
            searchQuery: String? = null
        ): SessionItem
    }
}
