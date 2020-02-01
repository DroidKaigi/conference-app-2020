package io.github.droidkaigi.confsched2020.session.ui.item

import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SpeechSession
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.ItemSessionDetailTargetBinding

/**
 * Intended Audience
 */
class SessionDetailTargetItem @AssistedInject constructor(
    @Assisted private val session: Session
) : BindableItem<ItemSessionDetailTargetBinding>(session.id.hashCode().toLong()) {
    override fun getLayout() = R.layout.item_session_detail_target

    override fun bind(binding: ItemSessionDetailTargetBinding, position: Int) {
        binding.session = session
        binding.speechSession = (session as? SpeechSession)
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(session: Session): SessionDetailTargetItem
    }
}
