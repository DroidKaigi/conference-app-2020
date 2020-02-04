package io.github.droidkaigi.confsched2020.about.ui.item

import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.about.R
import io.github.droidkaigi.confsched2020.about.databinding.ItemAboutIconBinding

class AboutIconItem @AssistedInject constructor(
    @Assisted private val name: String,
    @Assisted private val onClick: () -> Unit
) : BindableItem<ItemAboutIconBinding>() {
    override fun getLayout(): Int = R.layout.item_about_icon

    override fun bind(viewBinding: ItemAboutIconBinding, position: Int) {
        viewBinding.title.text = name
        viewBinding.root.setOnClickListener {
            onClick()
        }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            name: String,
            onClick: () -> Unit
        ): AboutIconItem
    }
}
