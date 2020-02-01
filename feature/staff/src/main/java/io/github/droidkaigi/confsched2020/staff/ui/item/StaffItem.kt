package io.github.droidkaigi.confsched2020.staff.ui.item

import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.model.Staff
import io.github.droidkaigi.confsched2020.staff.R
import io.github.droidkaigi.confsched2020.staff.databinding.ItemStaffBinding

class StaffItem @AssistedInject constructor(
    @Assisted private val staff: Staff
) : BindableItem<ItemStaffBinding>(staff.id.hashCode().toLong()) {
    override fun getLayout(): Int = R.layout.item_staff

    override fun bind(viewBinding: ItemStaffBinding, position: Int) {
        viewBinding.title.text = staff.name
    }

    override fun hasSameContentAs(other: Item<*>): Boolean = staff == (other as? StaffItem)?.staff

    @AssistedInject.Factory
    interface Factory {
        fun create(staff: Staff): StaffItem
    }
}
