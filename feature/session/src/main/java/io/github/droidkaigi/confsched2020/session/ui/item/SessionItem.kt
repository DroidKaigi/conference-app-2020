package io.github.droidkaigi.confsched2020.session.ui.item

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import androidx.navigation.NavController
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.item.EqualableContentsProvider
import io.github.droidkaigi.confsched2020.model.ServiceSession
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SpeechSession
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.ItemSessionBinding
import io.github.droidkaigi.confsched2020.session.ui.MainSessionsFragmentDirections.actionSessionToSessionDetail
import io.github.droidkaigi.confsched2020.session.ui.viewmodel.SessionsViewModel

class SessionItem @AssistedInject constructor(
    @Assisted val session: Session,
    @Assisted val sessionsViewModel: SessionsViewModel,
    val lifecycleOwnerLiveData: LiveData<LifecycleOwner>,
    val navController: NavController
) : BindableItem<ItemSessionBinding>(session.id.hashCode().toLong()),
    EqualableContentsProvider {
    override fun getLayout(): Int = R.layout.item_session

    override fun bind(viewBinding: ItemSessionBinding, position: Int) {
        viewBinding.root.background =
            ColorDrawable(if (session.isFavorited) Color.GRAY else Color.TRANSPARENT)

        viewBinding.title.setOnClickListener {
            sessionsViewModel.favorite(session).observe(lifecycleOwnerLiveData.value!!) {
            }
        }
        viewBinding.root.setOnClickListener {
            navController.navigate(actionSessionToSessionDetail(session.id))
        }
        viewBinding.title.text = when (session) {
            is SpeechSession -> session.title
            is ServiceSession -> session.title
        }.ja
    }

    override fun providerEqualableContents(): Array<*> {
        return arrayOf(session)
    }

    override fun equals(other: Any?): Boolean {
        return isSameContents(other)
    }

    override fun hashCode(): Int {
        return contentsHash()
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(session: Session, sessionsViewModel: SessionsViewModel): SessionItem
    }
}