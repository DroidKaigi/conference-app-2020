package io.github.droidkaigi.confsched2020.session.ui.item

import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
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
import io.github.droidkaigi.confsched2020.model.Speaker
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.ItemSpeakerBinding
import io.github.droidkaigi.confsched2020.session.ui.SearchSessionsFragmentDirections.actionSessionToSpeaker

class SpeakerItem @AssistedInject constructor(
    @Assisted val speaker: Speaker,
    private val lifecycleOwnerLiveData: LiveData<LifecycleOwner>
) : BindableItem<ItemSpeakerBinding>(speaker.id.hashCode().toLong()),
    EqualableContentsProvider {

    private val imageRequestDisposables = mutableListOf<RequestDisposable>()

    override fun getLayout(): Int = R.layout.item_speaker

    override fun bind(viewBinding: ItemSpeakerBinding, position: Int) {
        viewBinding.root.setOnClickListener {
            viewBinding.root.findNavController()
                .navigate(actionSessionToSpeaker(speaker.id))
        }
        viewBinding.name.text = speaker.name

        imageRequestDisposables.clear()
        val imageUrl = speaker.imageUrl
        val context = viewBinding.name.context
        val placeHolder = run {
            VectorDrawableCompat.create(
                context.resources,
                R.drawable.ic_person_outline_black_32dp,
                null
            )?.apply {
                setTint(
                    ContextCompat.getColor(context, R.color.speaker_icon)
                )
            }
        }?.also {
            viewBinding.name.setLeftDrawable(it)
        }

        imageRequestDisposables += Coil.load(context, imageUrl) {
            crossfade(true)
            placeholder(placeHolder)
            transformations(CircleCropTransformation())
            lifecycle(lifecycleOwnerLiveData.value)
            target {
                viewBinding.name.setLeftDrawable(it)
            }
        }
    }

    override fun unbind(viewHolder: ViewHolder<ItemSpeakerBinding>) {
        super.unbind(viewHolder)
        imageRequestDisposables.forEach { it.dispose() }
    }

    fun TextView.setLeftDrawable(drawable: Drawable) {
        val res = context.resources
        val widthDp = 64
        val heightDp = 64
        val widthPx = (widthDp * res.displayMetrics.density).toInt()
        val heightPx = (heightDp * res.displayMetrics.density).toInt()
        drawable.setBounds(0, 0, widthPx, heightPx)
        setCompoundDrawables(
            drawable, null, null, null
        )
    }

    override fun providerEqualableContents(): Array<*> {
        return arrayOf(speaker)
    }

    override fun equals(other: Any?): Boolean {
        return isSameContents(other)
    }

    override fun hashCode(): Int {
        return contentsHash()
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            speaker: Speaker
        ): SpeakerItem
    }
}