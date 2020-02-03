package io.github.droidkaigi.confsched2020.sponsor.ui.item

import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.sponsor.R
import io.github.droidkaigi.confsched2020.sponsor.databinding.ItemDividerBinding

class DividerItem : BindableItem<ItemDividerBinding>(GROUPIE_ITEM_ID) {
    override fun getLayout(): Int = R.layout.item_divider

    override fun bind(viewBinding: ItemDividerBinding, position: Int) {
    }

    companion object {
        private const val GROUPIE_ITEM_ID = -1L
    }
}
