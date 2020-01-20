package io.github.droidkaigi.confsched2020.session.ui.item

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.findNavController
import coil.request.RequestDisposable
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.databinding.ViewHolder
import io.github.droidkaigi.confsched2020.item.EqualableContentsProvider
import io.github.droidkaigi.confsched2020.model.SpeechSession
import io.github.droidkaigi.confsched2020.model.defaultLang
import io.github.droidkaigi.confsched2020.model.defaultTimeZoneOffset
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.ItemSpeakerSessionBinding
import io.github.droidkaigi.confsched2020.session.ui.SpeakerFragmentDirections

class SpeakerSessionItem @AssistedInject constructor(
    @Assisted val speechSession: SpeechSession,
    private val lifecycleOwnerLiveData: LiveData<LifecycleOwner>
) : BindableItem<ItemSpeakerSessionBinding>(speechSession.id.hashCode().toLong()),
    EqualableContentsProvider {

    override fun getLayout(): Int = R.layout.item_speaker_session

    override fun bind(viewBinding: ItemSpeakerSessionBinding, position: Int) {
        viewBinding.speechSession = speechSession
        viewBinding.lang = defaultLang()

        viewBinding.session.setOnClickListener {
            viewBinding.root.findNavController().navigate(
                SpeakerFragmentDirections.actionSpeakerToSessionDetail(
                    speechSession.id
                )
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