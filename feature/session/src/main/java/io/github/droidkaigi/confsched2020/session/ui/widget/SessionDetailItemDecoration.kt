package io.github.droidkaigi.confsched2020.session.ui.widget

import android.content.Context
import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import io.github.droidkaigi.confsched2020.session.ui.item.SessionDetailSpeakerItem

/**
 * For setting top and bottom margin of speaker
 */
class SessionDetailItemDecoration(
    private val adapter: GroupAdapter<*>,
    private val context: Context
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        val item = adapter.getItem(itemPosition)
        val dp = context.resources.displayMetrics.density
        var topMargin = 0 * dp
        var bottomMargin = 0 * dp
        if (item is SessionDetailSpeakerItem) {
            if (item.first) {
                // First speaker
                topMargin = 18 * dp
            } else {
                // Other speaker
                topMargin = 8 * dp
            }
        }
        if (itemPosition == adapter.itemCount - 1) {
            // Last item
            bottomMargin += 18 * dp
            if (item is SessionDetailSpeakerItem) {
                // Last item is speaker
                bottomMargin += 32 * dp
            }
        }
        outRect.set(0, topMargin.toInt(), 0, bottomMargin.toInt())
    }
}
