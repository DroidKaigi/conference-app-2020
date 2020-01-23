package io.github.droidkaigi.confsched2020.staff.ui.item

import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.item.EqualableContentsProvider
import io.github.droidkaigi.confsched2020.model.Staff
import io.github.droidkaigi.confsched2020.staff.R
import io.github.droidkaigi.confsched2020.staff.databinding.ItemStaffBinding

class StaffItem @AssistedInject constructor(
    @Assisted private val staff: Staff
) : BindableItem<ItemStaffBinding>(staff.id.hashCode().toLong()),
    EqualableContentsProvider {
    override fun getLayout(): Int = R.layout.item_staff

    override fun bind(viewBinding: ItemStaffBinding, position: Int) {
        viewBinding.title.text = staff.name
    }

    override fun providerEqualableContents(): Array<*> {
        return arrayOf(staff)
    }

    override fun equals(other: Any?): Boolean {
        return isSameContents(other)
    }

    override fun hashCode(): Int {
        return contentsHash()
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(staff: Staff): StaffItem
    }
}
