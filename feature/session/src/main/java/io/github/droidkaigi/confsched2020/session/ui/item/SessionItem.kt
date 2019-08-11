package io.github.droidkaigi.confsched2020.session.ui.item

import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.model.LocaledString
import io.github.droidkaigi.confsched2020.model.ServiceSession
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SpeechSession
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.ItemSessionBinding

class SessionItem(val session:Session): BindableItem<ItemSessionBinding>() {
    override fun getLayout(): Int = R.layout.item_session

    override fun bind(viewBinding: ItemSessionBinding, position: Int) {
        viewBinding.title.text = when (session) {
            is SpeechSession -> session.title
            is ServiceSession -> session.title
        }.ja
    }

}