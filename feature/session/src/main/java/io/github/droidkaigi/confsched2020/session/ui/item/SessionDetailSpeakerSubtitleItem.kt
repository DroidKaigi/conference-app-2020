package io.github.droidkaigi.confsched2020.session.ui.item

import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.ItemSessionDetailSpeakerSubtitleBinding

class SessionDetailSpeakerSubtitleItem @AssistedInject constructor() :
    BindableItem<ItemSessionDetailSpeakerSubtitleBinding>() {
    override fun getLayout() = R.layout.item_session_detail_speaker_subtitle

    override fun bind(binding: ItemSessionDetailSpeakerSubtitleBinding, position: Int) {
    }

    override fun isSameAs(other: Item<*>?) = other is SessionDetailSpeakerSubtitleItem

    @AssistedInject.Factory
    interface Factory {
        fun create(): SessionDetailSpeakerSubtitleItem
    }
}
