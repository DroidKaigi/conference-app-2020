package io.github.droidkaigi.confsched2020.session.ui.item

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import coil.Coil
import coil.api.load
import coil.transform.CircleCropTransformation
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.model.Speaker
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.ItemSessionDetailSpeakerBinding
import io.github.droidkaigi.confsched2020.session.ui.SessionDetailFragment
import io.github.droidkaigi.confsched2020.ui.ProfilePlaceholderCreator
import io.github.droidkaigi.confsched2020.util.lazyWithParam

/**
 * @param first For setting margin by SessionDetailItemDecoration
 */
class SessionDetailSpeakerItem @AssistedInject constructor(
    private val lifecycleOwnerLiveData: LiveData<LifecycleOwner>,
    @Assisted private val speaker: Speaker,
    @Assisted val first: Boolean,
    @Assisted private val onClick: (FragmentNavigator.Extras) -> Unit
) : BindableItem<ItemSessionDetailSpeakerBinding>(speaker.id.hashCode().toLong()) {

    private val placeholder by lazyWithParam<Context, VectorDrawableCompat?> {
        ProfilePlaceholderCreator.create(it)
    }

    override fun getLayout() = R.layout.item_session_detail_speaker

    override fun bind(binding: ItemSessionDetailSpeakerBinding, position: Int) {
        val speakerNameView = binding.speaker
        val speakerImageView = binding.speakerImage
        binding.root.transitionName =
            "${speaker.id}-${SessionDetailFragment.TRANSITION_NAME_SUFFIX}"
        binding.root.setOnClickListener {
            val extras = FragmentNavigatorExtras(
                binding.root to binding.root.transitionName
            )
            onClick(extras)
        }
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
        val placeholder = placeholder.get(context)?.also {
            speakerImageView.setImageDrawable(it)
        }

        Coil.load(context, imageUrl) {
            crossfade(true)
            placeholder(placeholder)
            transformations(CircleCropTransformation())
            lifecycle(lifecycleOwnerLiveData.value)
            target {
                speakerImageView.setImageDrawable(it)
            }
        }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            speaker: Speaker,
            first: Boolean,
            onClick: (FragmentNavigator.Extras) -> Unit
        ): SessionDetailSpeakerItem
    }
}
