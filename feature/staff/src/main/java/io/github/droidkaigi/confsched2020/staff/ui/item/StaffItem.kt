package io.github.droidkaigi.confsched2020.staff.ui.item

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import coil.api.load
import coil.transform.CircleCropTransformation
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.item.EqualableContentsProvider
import io.github.droidkaigi.confsched2020.model.Staff
import io.github.droidkaigi.confsched2020.staff.R
import io.github.droidkaigi.confsched2020.staff.databinding.ItemStaffBinding

class StaffItem @AssistedInject constructor(
    @Assisted private val staff: Staff,
    context: Context,
    private val lifecycleOwnerLiveData: LiveData<LifecycleOwner>
) : BindableItem<ItemStaffBinding>(staff.id.hashCode().toLong()),
    EqualableContentsProvider {

    private val placeHolder = VectorDrawableCompat.create(
        context.resources,
        R.drawable.shape_staff_image_background,
        null
    )

    override fun getLayout(): Int = R.layout.item_staff

    override fun bind(viewBinding: ItemStaffBinding, position: Int) {
        viewBinding.title.text = staff.name

        viewBinding.staffImage.load(staff.iconUrl) {
            crossfade(true)
            placeholder(placeHolder)
            transformations(CircleCropTransformation())
            lifecycle(lifecycleOwnerLiveData.value)
        }
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
