package io.github.droidkaigi.confsched2020.contributor.ui.item

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import coil.api.load
import coil.transform.CircleCropTransformation
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.contributor.R
import io.github.droidkaigi.confsched2020.contributor.databinding.ItemContributorBinding
import io.github.droidkaigi.confsched2020.contributor.ui.ContributorsFragmentDirections.Companion.actionContributorsToChrome
import io.github.droidkaigi.confsched2020.model.Contributor
import io.github.droidkaigi.confsched2020.ui.ProfilePlaceholderCreator
import io.github.droidkaigi.confsched2020.util.lazyWithParam

class ContributorItem @AssistedInject constructor(
    @Assisted private val contributor: Contributor,
    private val lifecycleOwnerLiveData: LiveData<LifecycleOwner>
) : BindableItem<ItemContributorBinding>(contributor.id.toLong()) {

    private val placeholder by lazyWithParam<Context, VectorDrawableCompat?> {
        ProfilePlaceholderCreator.create(it)
    }

    override fun getLayout(): Int = R.layout.item_contributor

    override fun bind(viewBinding: ItemContributorBinding, position: Int) {
        viewBinding.contributor = contributor

        val placeholderDrawable = placeholder.get(viewBinding.contributorImage.context)
        viewBinding.contributorImage.load(contributor.iconUrl) {
            crossfade(true)
            placeholder(placeholderDrawable)
            error(placeholderDrawable)
            transformations(CircleCropTransformation())
            lifecycle(lifecycleOwnerLiveData.value)
        }

        viewBinding.contributorLayout.setOnClickListener {
            it.findNavController().navigate(actionContributorsToChrome(contributor.profileUrl))
        }
    }

    override fun hasSameContentAs(other: Item<*>): Boolean {
        return contributor == (other as? ContributorItem)?.contributor
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(contributor: Contributor): ContributorItem
    }
}
