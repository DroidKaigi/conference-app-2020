package io.github.droidkaigi.confsched2020.sponsor.ui.item

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.item.EqualableContentsProvider
import io.github.droidkaigi.confsched2020.model.SponsorPlan
import io.github.droidkaigi.confsched2020.sponsor.R
import io.github.droidkaigi.confsched2020.sponsor.databinding.ItemCategoryHeaderBinding
import io.github.droidkaigi.confsched2020.sponsor.ui.viewmodel.SponsorsViewModel

class CategoryHeaderItem @AssistedInject constructor(
    @Assisted val plan: SponsorPlan.Plan,
    @Assisted val sponsorsViewModel: SponsorsViewModel,
    val lifecycleOwnerLiveData: LiveData<LifecycleOwner>
) : BindableItem<ItemCategoryHeaderBinding>(plan.id.hashCode().toLong()),
    EqualableContentsProvider {
    override fun getLayout(): Int = R.layout.item_category_header

    override fun bind(viewBinding: ItemCategoryHeaderBinding, position: Int) {
        viewBinding.title.text = plan.title
    }

    override fun providerEqualableContents(): Array<*> {
        return arrayOf(plan)
    }

    override fun equals(other: Any?): Boolean {
        return isSameContents(other)
    }

    override fun hashCode(): Int {
        return contentsHash()
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(plan: SponsorPlan.Plan, sponsorsViewModel: SponsorsViewModel): CategoryHeaderItem
    }
}