package io.github.droidkaigi.confsched2020.about.ui.item

import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.about.R
import io.github.droidkaigi.confsched2020.about.databinding.ItemAboutTextBinding

class AboutTextItem @AssistedInject constructor(
    @Assisted private val name: String,
    @Assisted private val content: String
) : BindableItem<ItemAboutTextBinding>() {
    override fun getLayout(): Int = R.layout.item_about_text

    override fun bind(viewBinding: ItemAboutTextBinding, position: Int) {
        viewBinding.title.text = name
        viewBinding.content.text = content
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            name: String,
            content: String
        ): AboutTextItem
    }
}
