package io.github.droidkaigi.confsched2020.session.ui.item

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import coil.Coil
import coil.api.load
import coil.request.RequestDisposable
import coil.transform.CircleCropTransformation
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.databinding.ViewHolder
import io.github.droidkaigi.confsched2020.item.EqualableContentsProvider
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.Speaker
import io.github.droidkaigi.confsched2020.model.SpeechSession
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.ItemSessionBinding
import io.github.droidkaigi.confsched2020.session.ui.MainSessionsFragmentDirections.actionSessionToSessionDetail
import io.github.droidkaigi.confsched2020.session.ui.viewmodel.SessionsViewModel
import io.github.droidkaigi.confsched2020.util.lazyWithParam
import kotlin.math.max

class SessionItem @AssistedInject constructor(
    @Assisted val session: Session,
    @Assisted val sessionsViewModel: SessionsViewModel,
    val lifecycleOwnerLiveData: LiveData<LifecycleOwner>,
    val navController: NavController
) : BindableItem<ItemSessionBinding>(session.id.hashCode().toLong()),
    EqualableContentsProvider {

    val imageRequestDisposables = mutableListOf<RequestDisposable>()

    val layoutInflater by lazyWithParam<Context, LayoutInflater> { context ->
        LayoutInflater.from(context)
    }

    override fun getLayout(): Int = R.layout.item_session

    override fun bind(viewBinding: ItemSessionBinding, position: Int) {
        viewBinding.favorite.setOnClickListener {
            sessionsViewModel
                .favorite(session)
                .observeBy(lifecycleOwnerLiveData.value!!)
        }
        viewBinding.favorite.setImageResource(
            if (session.isFavorited)
                R.drawable.ic_bookmark_black_24dp
            else
                R.drawable.ic_bookmark_border_black_24dp
        )
        viewBinding.root.setOnClickListener {
            navController.navigate(actionSessionToSessionDetail(session.id))
        }
        viewBinding.live.isVisible = session.isOnGoing
        viewBinding.title.text = session.title.ja
        viewBinding.room.text = session.room.name
        viewBinding.survey.isEnabled = session.isFinished
        imageRequestDisposables.clear()
        viewBinding.speakers.bindSpeaker()
    }

    private fun ViewGroup.bindSpeaker() {
        (0 until max(
            size, (session as? SpeechSession)?.speakers.orEmpty().size
        )).forEach { index ->
            val existSpeakerView = getChildAt(index) as? ViewGroup
            val speaker: Speaker? = (session as? SpeechSession)?.speakers?.getOrNull(index)
            if (existSpeakerView == null && speaker == null) {
                return@forEach
            }
            if (existSpeakerView != null && speaker == null) {
                // Cache for performance
                existSpeakerView.isVisible = false
                return@forEach
            }
            if (existSpeakerView == null && speaker != null) {
                val speakerView = layoutInflater.get(context).inflate(
                    R.layout.layout_speaker, this, false
                ) as ViewGroup
                val textView: TextView = speakerView.findViewById(R.id.speaker)
                bindSpeakerData(speaker, textView)

                addView(speakerView)
                return@forEach
            }
            if (existSpeakerView != null && speaker != null) {
                existSpeakerView.isVisible = true

                val textView: TextView = existSpeakerView.findViewById(R.id.speaker)
                textView.text = speaker.name
                bindSpeakerData(speaker, textView)
            }
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
                    ContextCompat.getColor(
                        context,
                        R.color.colorOnBackgroundSecondary
                    )
                )
            }
        }?.also {
            textView.setLeftDrawable(it)
        }

        imageRequestDisposables += Coil.load(context, imageUrl) {
            crossfade(true)
            placeholder(placeHolder)
            transformations(CircleCropTransformation())
            lifecycle(lifecycleOwnerLiveData.value)
            target {
                textView.setLeftDrawable(it)
            }
        }
    }

    override fun unbind(viewHolder: ViewHolder<ItemSessionBinding>) {
        super.unbind(viewHolder)
        imageRequestDisposables.forEach { it.dispose() }
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

    override fun providerEqualableContents(): Array<*> {
        return arrayOf(session)
    }

    override fun equals(other: Any?): Boolean {
        return isSameContents(other)
    }

    override fun hashCode(): Int {
        return contentsHash()
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(session: Session, sessionsViewModel: SessionsViewModel): SessionItem
    }
}