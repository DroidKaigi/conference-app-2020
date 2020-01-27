package io.github.droidkaigi.confsched2020.session.ui.item

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.widget.TextView
import androidx.core.view.doOnPreDraw
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
import io.github.droidkaigi.confsched2020.ui.ProfilePlaceholderCreator
import io.github.droidkaigi.confsched2020.util.lazyWithParam
import java.util.regex.Pattern
import javax.inject.Named

class SpeakerDetailItem @AssistedInject constructor(
    @Assisted val speaker: Speaker,
    @Assisted @Named("transitionNameSuffix")
    val transitionNameSuffix: String,
    @Assisted val searchQuery: String?,
    @Assisted val onImageLoadedCallback: () -> Unit,
    private val lifecycleOwnerLiveData: LiveData<LifecycleOwner>
) : BindableItem<ItemSpeakerDetailBinding>(speaker.id.hashCode().toLong()),
    EqualableContentsProvider {

    private val placeholder by lazyWithParam<Context, VectorDrawableCompat?> { context ->
        ProfilePlaceholderCreator.create(context)
    }

    override fun getLayout(): Int = R.layout.item_speaker_detail

    override fun bind(viewBinding: ItemSpeakerDetailBinding, position: Int) {
        viewBinding.speaker = speaker

        viewBinding.speakerDescription.movementMethod = LinkMovementMethod.getInstance()
        viewBinding.speakerDescription.doOnPreDraw {
            viewBinding.speakerDescription.setSearchHighlight()
        }
        viewBinding.speakerImage.transitionName = "${speaker.id}-$transitionNameSuffix"

        speaker.imageUrl ?: onImageLoadedCallback()

        val context = viewBinding.root.context

        Coil.load(context, speaker.imageUrl) {
            crossfade(true)
            placeholder(placeholder.get(context))
            transformations(CircleCropTransformation())
            lifecycle(lifecycleOwnerLiveData.value)
            target(
                onStart = {
                    viewBinding.speakerImage.setImageDrawable(it)
                },
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
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        text = spannableStringBuilder
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            speaker: Speaker,
            @Named("transitionNameSuffix") transitionNameSuffix: String,
            searchQuery: String?,
            onImageLoadedCallback: () -> Unit
        ): SpeakerDetailItem
    }
}
