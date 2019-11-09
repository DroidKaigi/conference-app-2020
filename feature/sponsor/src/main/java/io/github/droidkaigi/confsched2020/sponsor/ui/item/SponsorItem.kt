package io.github.droidkaigi.confsched2020.sponsor.ui.item

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.sponsor.R
import io.github.droidkaigi.confsched2020.item.EqualableContentsProvider
import io.github.droidkaigi.confsched2020.model.Sponsor
import io.github.droidkaigi.confsched2020.sponsor.databinding.ItemSponsorBinding
import io.github.droidkaigi.confsched2020.sponsor.ui.viewmodel.SponsorsViewModel

class SponsorItem @AssistedInject constructor(
    @Assisted val sponsor: Sponsor,
    @Assisted val sponsorsViewModel: SponsorsViewModel,
    val lifecycleOwnerLiveData: LiveData<LifecycleOwner>
) : BindableItem<ItemSponsorBinding>(sponsor.name.hashCode().toLong()),
    EqualableContentsProvider {
    override fun getLayout(): Int = R.layout.item_sponsor

    override fun bind(viewBinding: ItemSponsorBinding, position: Int) {
        viewBinding.title.text = sponsor.name
    }

    override fun providerEqualableContents(): Array<*> {
        return arrayOf(sponsor)
    }

    override fun equals(other: Any?): Boolean {
        return isSameContents(other)
    }

    override fun hashCode(): Int {
        return contentsHash()
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(sponsor: Sponsor, sponsorsViewModel: SponsorsViewModel): SponsorItem
    }
}