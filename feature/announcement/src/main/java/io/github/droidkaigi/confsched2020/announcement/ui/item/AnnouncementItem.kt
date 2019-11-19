package io.github.droidkaigi.confsched2020.announcement.ui.item

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.soywiz.klock.DateFormat
import com.soywiz.klock.TimezoneOffset
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.announcement.R
import io.github.droidkaigi.confsched2020.announcement.databinding.ItemAnnouncementBinding
import io.github.droidkaigi.confsched2020.announcement.ui.viewmodel.AnnouncementViewModel
import io.github.droidkaigi.confsched2020.item.EqualableContentsProvider
import io.github.droidkaigi.confsched2020.model.Announcement
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel

class AnnouncementItem @AssistedInject constructor(
    @Assisted val announcement: Announcement,
    @Assisted val announcementViewModel: AnnouncementViewModel,
    @Assisted val systemViewModel: SystemViewModel,
    val lifecycleOwnerLiveData: LiveData<LifecycleOwner>
) : BindableItem<ItemAnnouncementBinding>(announcement.id), EqualableContentsProvider {

    companion object {
        private val dateFormatter = DateFormat("MM.dd HH:mm")
        private val jstOffset = TimezoneOffset(9.0 * 60 * 60 * 1000)
    }

    override fun getLayout(): Int = R.layout.item_announcement

    override fun bind(viewBinding: ItemAnnouncementBinding, position: Int) {
        viewBinding.announcementIcon.setImageResource(
            when (announcement.type) {
                Announcement.Type.NOTIFICATION -> R.drawable.ic_feed_notification_blue_20dp
                Announcement.Type.ALERT -> R.drawable.ic_feed_alert_amber_20dp
                Announcement.Type.FEEDBACK -> R.drawable.ic_feed_feedback_cyan_20dp
            }
        )
        viewBinding.announcementTitle.text = announcement.title
        viewBinding.announcementContent.text = announcement.content
        viewBinding.announcementDateTime.text =
            dateFormatter.format(announcement.publishedAt.toOffset(jstOffset))
    }

    override fun providerEqualableContents(): Array<*> {
        return arrayOf(announcement)
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
            announcement: Announcement,
            announcementViewModel: AnnouncementViewModel,
            systemViewModel: SystemViewModel
        ): AnnouncementItem
    }
}