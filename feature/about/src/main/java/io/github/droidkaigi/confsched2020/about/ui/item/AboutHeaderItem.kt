package io.github.droidkaigi.confsched2020.about.ui.item

import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.about.R
import io.github.droidkaigi.confsched2020.about.databinding.ItemAboutHeaderBinding

class AboutHeaderItem @AssistedInject constructor(
    @Assisted private val onClickTwitter: () -> Unit,
    @Assisted private val onClickYoutube: () -> Unit,
    @Assisted private val onClickMedium: () -> Unit
) : BindableItem<ItemAboutHeaderBinding>() {
    override fun getLayout(): Int = R.layout.item_about_header

    override fun bind(viewBinding: ItemAboutHeaderBinding, position: Int) {
        viewBinding.twitterButton.setOnClickListener {
            onClickTwitter()
        }
        viewBinding.youtubeButton.setOnClickListener {
            onClickYoutube()
        }
        viewBinding.mediumButton.setOnClickListener {
            onClickMedium()
        }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            onClickTwitter: () -> Unit,
            onClickYoutube: () -> Unit,
            onClickMedium: () -> Unit
        ): AboutHeaderItem
    }
}
