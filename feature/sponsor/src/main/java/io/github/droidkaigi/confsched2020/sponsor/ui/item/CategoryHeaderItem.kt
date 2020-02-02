package io.github.droidkaigi.confsched2020.sponsor.ui.item

import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.model.SponsorCategory
import io.github.droidkaigi.confsched2020.sponsor.R
import io.github.droidkaigi.confsched2020.sponsor.databinding.ItemCategoryHeaderBinding

class CategoryHeaderItem @AssistedInject constructor(
    @Assisted private val category: SponsorCategory.Category
) : BindableItem<ItemCategoryHeaderBinding>(category.id.hashCode().toLong()) {
    override fun getLayout(): Int = R.layout.item_category_header

    override fun bind(viewBinding: ItemCategoryHeaderBinding, position: Int) {
        viewBinding.title.text = category.title
    }

    override fun hasSameContentAs(other: Item<*>): Boolean =
        category == (other as? CategoryHeaderItem)?.category

    @AssistedInject.Factory
    interface Factory {
        fun create(category: SponsorCategory.Category): CategoryHeaderItem
    }
}
