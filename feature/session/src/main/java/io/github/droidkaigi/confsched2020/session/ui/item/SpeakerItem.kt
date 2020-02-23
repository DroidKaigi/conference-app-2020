package io.github.droidkaigi.confsched2020.session.ui.item

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.widget.TextView
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
import io.github.droidkaigi.confsched2020.model.Speaker
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.ItemSpeakerBinding
import io.github.droidkaigi.confsched2020.session.ui.SearchSessionsFragmentDirections.Companion.actionSessionToSpeaker
import io.github.droidkaigi.confsched2020.ui.ProfilePlaceholderCreator
import io.github.droidkaigi.confsched2020.util.lazyWithParam
import java.util.regex.Pattern

class SpeakerItem @AssistedInject constructor(
    @Assisted val speaker: Speaker,
    @Assisted val searchQuery: String?,
    private val lifecycleOwnerLiveData: LiveData<LifecycleOwner>
) : BindableItem<ItemSpeakerBinding>(speaker.id.hashCode().toLong()) {

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
                viewBinding.root to viewBinding.root.transitionName
            )
            viewBinding.root.findNavController().navigate(
                actionSessionToSpeaker(
                    speaker.id,
                    TRANSITION_NAME_SUFFIX,
                    searchQuery),
                extras
            )
        }
        viewBinding.name.text = speaker.name
        viewBinding.name.setSearchHighlight()
        viewBinding.root.transitionName = "${speaker.id}-$TRANSITION_NAME_SUFFIX"

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

    override fun unbind(viewHolder: GroupieViewHolder<ItemSpeakerBinding>) {
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
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        text = spannableStringBuilder
    }

    override fun hasSameContentAs(other: Item<*>): Boolean =
        speaker == (other as? SpeakerItem)?.speaker

    @AssistedInject.Factory
    interface Factory {
        fun create(
            speaker: Speaker,
            searchQuery: String? = null
        ): SpeakerItem
    }
}
