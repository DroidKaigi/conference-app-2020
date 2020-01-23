package io.github.droidkaigi.confsched2020.session.ui.item

import android.content.Context
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
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.databinding.ViewHolder
import io.github.droidkaigi.confsched2020.item.EqualableContentsProvider
import io.github.droidkaigi.confsched2020.model.Speaker
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.ItemSpeakerBinding
import io.github.droidkaigi.confsched2020.session.ui.SearchSessionsFragmentDirections.Companion.actionSessionToSpeaker
import io.github.droidkaigi.confsched2020.ui.ProfilePlaceholderCreator
import io.github.droidkaigi.confsched2020.util.lazyWithParam

class SpeakerItem @AssistedInject constructor(
    @Assisted val speaker: Speaker,
    private val lifecycleOwnerLiveData: LiveData<LifecycleOwner>
) : BindableItem<ItemSpeakerBinding>(speaker.id.hashCode().toLong()),
    EqualableContentsProvider {

    private val imageRequestDisposables = mutableListOf<RequestDisposable>()
    private val placeholder by lazyWithParam<Context, VectorDrawableCompat?> { context ->
        ProfilePlaceholderCreator.create(context)
    }

    companion object {
        private const val TRANSITION_NAME_SUFFIX = "speaker"
    }

    override fun getLayout(): Int = R.layout.item_speaker

    override fun bind(viewBinding: ItemSpeakerBinding, position: Int) {
        viewBinding.root.setOnClickListener {
            val extras = FragmentNavigatorExtras(
                viewBinding.image to viewBinding.image.transitionName
            )
            viewBinding.root.findNavController()
                .navigate(actionSessionToSpeaker(speaker.id, TRANSITION_NAME_SUFFIX), extras)
        }
        viewBinding.name.text = speaker.name
        viewBinding.image.transitionName = "${speaker.id}-$TRANSITION_NAME_SUFFIX"

        imageRequestDisposables.clear()
        val imageUrl = speaker.imageUrl
        val context = viewBinding.name.context
        val placeholder = placeholder.get(context)?.also {
            viewBinding.image.setImageDrawable(it)
        }

        imageRequestDisposables += Coil.load(context, imageUrl) {
            crossfade(true)
            placeholder(placeholder)
            transformations(CircleCropTransformation())
            lifecycle(lifecycleOwnerLiveData.value)
            target {
                viewBinding.image.setImageDrawable(it)
            }
        }
    }

    override fun unbind(viewHolder: ViewHolder<ItemSpeakerBinding>) {
        super.unbind(viewHolder)
        imageRequestDisposables.forEach { it.dispose() }
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
