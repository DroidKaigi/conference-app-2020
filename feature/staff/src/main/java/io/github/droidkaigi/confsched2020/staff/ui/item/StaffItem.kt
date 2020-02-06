package io.github.droidkaigi.confsched2020.staff.ui.item

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import coil.api.load
import coil.transform.CircleCropTransformation
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.model.Staff
import io.github.droidkaigi.confsched2020.staff.R
import io.github.droidkaigi.confsched2020.staff.databinding.ItemStaffBinding

class StaffItem @AssistedInject constructor(
    @Assisted private val staff: Staff,
    private val lifecycleOwnerLiveData: LiveData<LifecycleOwner>
) : BindableItem<ItemStaffBinding>(staff.id.hashCode().toLong()) {

    override fun getLayout(): Int = R.layout.item_staff

    override fun bind(viewBinding: ItemStaffBinding, position: Int) {
        viewBinding.title.text = staff.name

        viewBinding.staffImage.run {
            val placeHolder = VectorDrawableCompat.create(
                context.resources,
                R.drawable.shape_staff_image_background,
                null
            )

            load(staff.iconUrl) {
                crossfade(true)
                placeholder(placeHolder)
                transformations(CircleCropTransformation())
                lifecycle(lifecycleOwnerLiveData.value)
            }
        }
    }

    override fun hasSameContentAs(other: Item<*>): Boolean = staff == (other as? StaffItem)?.staff

    @AssistedInject.Factory
    interface Factory {
        fun create(staff: Staff): StaffItem
    }
}
