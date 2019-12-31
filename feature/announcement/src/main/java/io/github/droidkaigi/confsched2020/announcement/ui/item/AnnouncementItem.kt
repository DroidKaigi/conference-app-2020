package io.github.droidkaigi.confsched2020.announcement.ui.item

import com.soywiz.klock.DateFormat
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.announcement.R
import io.github.droidkaigi.confsched2020.announcement.databinding.ItemAnnouncementBinding
import io.github.droidkaigi.confsched2020.item.EqualableContentsProvider
import io.github.droidkaigi.confsched2020.model.Announcement
import io.github.droidkaigi.confsched2020.model.defaultTimeZoneOffset

class AnnouncementItem @AssistedInject constructor(
    @Assisted private val announcement: Announcement
) : BindableItem<ItemAnnouncementBinding>(announcement.id), EqualableContentsProvider {

    companion object {
        private val dateFormatter = DateFormat("MM.dd HH:mm")
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
            dateFormatter.format(announcement.publishedAt.toOffset(defaultTimeZoneOffset()))
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
            announcement: Announcement
        ): AnnouncementItem
    }
}