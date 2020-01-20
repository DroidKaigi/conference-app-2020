package io.github.droidkaigi.confsched2020.contributor.ui.item

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import coil.api.load
import coil.transform.CircleCropTransformation
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.contributor.R
import io.github.droidkaigi.confsched2020.contributor.databinding.ItemContributorBinding
import io.github.droidkaigi.confsched2020.contributor.ui.ContributorsFragmentDirections.Companion.actionContributorsToChrome
import io.github.droidkaigi.confsched2020.model.Contributor
import io.github.droidkaigi.confsched2020.ui.SpeakerPlaceholderCreator

class ContributorItem @AssistedInject constructor(
    @Assisted private val contributor: Contributor,
    private val lifecycleOwnerLiveData: LiveData<LifecycleOwner>
) : BindableItem<ItemContributorBinding>(contributor.id.toLong()) {

    override fun getLayout(): Int = R.layout.item_contributor

    override fun bind(viewBinding: ItemContributorBinding, position: Int) {
        viewBinding.contributor = contributor

        val context = viewBinding.contributorImage.context
        val placeholder = SpeakerPlaceholderCreator.create(context)
        viewBinding.contributorImage.load(contributor.iconUrl) {
            crossfade(true)
            placeholder(placeholder)
            transformations(CircleCropTransformation())
            lifecycle(lifecycleOwnerLiveData.value)
        }

        viewBinding.contributorLayout.setOnClickListener {
            it.findNavController().navigate(actionContributorsToChrome(contributor.profileUrl))
        }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            contributor: Contributor
        ): ContributorItem
    }
}
