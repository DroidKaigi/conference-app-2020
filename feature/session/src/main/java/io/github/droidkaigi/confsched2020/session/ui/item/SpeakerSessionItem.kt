package io.github.droidkaigi.confsched2020.session.ui.item

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.item.EqualableContentsProvider
import io.github.droidkaigi.confsched2020.model.SpeechSession
import io.github.droidkaigi.confsched2020.model.defaultLang
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.ItemSpeakerSessionBinding
import io.github.droidkaigi.confsched2020.session.ui.SpeakerFragmentDirections

class SpeakerSessionItem @AssistedInject constructor(
    @Assisted val speechSession: SpeechSession,
    private val lifecycleOwnerLiveData: LiveData<LifecycleOwner>
) : BindableItem<ItemSpeakerSessionBinding>(speechSession.id.hashCode().toLong()),
    EqualableContentsProvider {

    companion object {
        private const val TRANSITION_NAME_SUFFIX = "speaker-session"
    }

    override fun getLayout(): Int = R.layout.item_speaker_session

    override fun bind(viewBinding: ItemSpeakerSessionBinding, position: Int) {
        viewBinding.speakerSessionRoot.transitionName = "${speechSession.id}-$TRANSITION_NAME_SUFFIX"
        viewBinding.speechSession = speechSession
        viewBinding.lang = defaultLang()

        viewBinding.session.setOnClickListener {
            val extra = FragmentNavigatorExtras(
                viewBinding.speakerSessionRoot to viewBinding.speakerSessionRoot.transitionName
            )
            viewBinding.root.findNavController().navigate(
                SpeakerFragmentDirections.actionSpeakerToSessionDetail(
                    speechSession.id,
                    TRANSITION_NAME_SUFFIX
                ),
                extra
            )
        }
    }

    override fun providerEqualableContents(): Array<*> {
        return arrayOf(speechSession)
    }

    override fun equals(other: Any?): Boolean {
        return isSameContents(other)
    }

    override fun hashCode(): Int {
        return contentsHash()
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            speechSession: SpeechSession
        ): SpeakerSessionItem
    }
}
