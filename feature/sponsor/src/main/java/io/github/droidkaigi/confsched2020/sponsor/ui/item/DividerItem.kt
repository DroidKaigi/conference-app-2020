package io.github.droidkaigi.confsched2020.sponsor.ui.item

import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.sponsor.R
import io.github.droidkaigi.confsched2020.sponsor.databinding.ItemDividerBinding

class DividerItem : BindableItem<ItemDividerBinding>() {
    override fun getLayout(): Int = R.layout.item_divider

    override fun bind(viewBinding: ItemDividerBinding, position: Int) {
    }

    override fun isSameAs(other: Item<*>?): Boolean {
        return other is DividerItem
    }
}
