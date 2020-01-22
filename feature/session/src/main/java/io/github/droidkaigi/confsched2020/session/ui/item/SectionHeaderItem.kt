package io.github.droidkaigi.confsched2020.session.ui.item

import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.item.EqualableContentsProvider
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.ItemSectionHeaderBinding

class SectionHeaderItem @AssistedInject constructor(
    @Assisted val title: String
) : BindableItem<ItemSectionHeaderBinding>(title.hashCode().toLong()),
    EqualableContentsProvider {
    override fun getLayout(): Int = R.layout.item_section_header

    override fun bind(viewBinding: ItemSectionHeaderBinding, position: Int) {
        viewBinding.title.text = title
    }

    override fun providerEqualableContents(): Array<*> {
        return arrayOf(title)
    }

    override fun equals(other: Any?): Boolean {
        return isSameContents(other)
    }

    override fun hashCode(): Int {
        return contentsHash()
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(title: String): SectionHeaderItem
    }
}
