package io.github.droidkaigi.confsched2020.session.ui.item

import android.graphics.drawable.Drawable
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

class SessionDetailSpeakerItem(
    private val lifecycleOwner: LifecycleOwner,
    private val speaker: Speaker
) : BindableItem<ItemSessionDetailSpeakerBinding>() {
    override fun getLayout() = R.layout.item_session_detail_speaker

    override fun bind(binding: ItemSessionDetailSpeakerBinding, position: Int) {
        bindSpeakerData(binding.speaker)
    }

    private fun bindSpeakerData(textView: TextView) {
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
            lifecycle(lifecycleOwner)
            target {
                textView.setLeftDrawable(it)
            }
        }
    }

    private fun TextView.setLeftDrawable(drawable: Drawable) {
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