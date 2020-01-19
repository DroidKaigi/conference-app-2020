package io.github.droidkaigi.confsched2020.session.ui.item

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import coil.Coil
import coil.api.load
import coil.transform.CircleCropTransformation
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.ext.getThemeColor
import io.github.droidkaigi.confsched2020.model.Speaker
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.ItemSessionDetailSpeakerBinding
import io.github.droidkaigi.confsched2020.session.ui.SessionDetailFragment

class SessionDetailSpeakerItem(
    private val lifecycleOwner: LifecycleOwner,
    private val speaker: Speaker
) : BindableItem<ItemSessionDetailSpeakerBinding>() {
    override fun getLayout() = R.layout.item_session_detail_speaker

    override fun bind(binding: ItemSessionDetailSpeakerBinding, position: Int) {
        val speakerNameView = binding.speaker
        val speakerImageView = binding.speakerImage
        speakerImageView.transitionName =
            "${speaker.id}-${SessionDetailFragment.TRANSITION_NAME_SUFFIX}"
        bindSpeakerData(speakerNameView, speakerImageView)
    }

    private fun bindSpeakerData(
        speakerNameView: TextView,
        speakerImageView: ImageView
    ) {
        speakerNameView.text = speaker.name
//        setHighlightText(textView, query)
        val imageUrl = speaker.imageUrl
        val context = speakerNameView.context
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
            speakerImageView.setImageDrawable(it)
        }

        Coil.load(context, imageUrl) {
            crossfade(true)
            placeholder(placeHolder)
            transformations(CircleCropTransformation())
            lifecycle(lifecycleOwner)
            target {
                speakerImageView.setImageDrawable(it)
            }
        }
    }
}