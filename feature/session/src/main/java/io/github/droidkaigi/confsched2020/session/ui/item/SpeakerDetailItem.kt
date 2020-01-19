package io.github.droidkaigi.confsched2020.session.ui.item

import android.content.Context
import android.text.method.LinkMovementMethod
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import coil.Coil
import coil.api.load
import coil.transform.CircleCropTransformation
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.ext.getThemeColor
import io.github.droidkaigi.confsched2020.item.EqualableContentsProvider
import io.github.droidkaigi.confsched2020.model.Speaker
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.ItemSpeakerDetailBinding
import javax.inject.Named

class SpeakerDetailItem @AssistedInject constructor(
    @Assisted val speaker: Speaker,
    @Assisted @Named("transitionNameSuffix") val transitionNameSuffix: String,
    @Assisted val onImageLoadedCallback: () -> Unit,
    private val context: Context,
    private val lifecycleOwnerLiveData: LiveData<LifecycleOwner>
) : BindableItem<ItemSpeakerDetailBinding>(speaker.id.hashCode().toLong()),
    EqualableContentsProvider {

    private val placeHolder = VectorDrawableCompat.create(
        context.resources,
        R.drawable.ic_person_outline_black_32dp,
        null
    )?.apply {
        setTint(
            context.getThemeColor(R.attr.colorOnBackground)
        )
    }

    override fun getLayout(): Int = R.layout.item_speaker_detail

    override fun bind(viewBinding: ItemSpeakerDetailBinding, position: Int) {
        viewBinding.speaker = speaker

        viewBinding.speakerDescription.movementMethod = LinkMovementMethod.getInstance()
        viewBinding.speakerImage.transitionName = "${speaker.id}-${transitionNameSuffix}"

        speaker.imageUrl ?: onImageLoadedCallback()

        Coil.load(context, speaker.imageUrl) {
            crossfade(true)
            placeholder(placeHolder)
            transformations(CircleCropTransformation())
            lifecycle(lifecycleOwnerLiveData.value)
            target(
                onSuccess = {
                    viewBinding.speakerImage.setImageDrawable(it)
                    onImageLoadedCallback()
                },
                onError = {
                    onImageLoadedCallback()
                }
            )
        }
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
            speaker: Speaker,
            @Named("transitionNameSuffix") transitionNameSuffix: String,
            onImageLoadedCallback: () -> Unit
        ): SpeakerDetailItem
    }
}