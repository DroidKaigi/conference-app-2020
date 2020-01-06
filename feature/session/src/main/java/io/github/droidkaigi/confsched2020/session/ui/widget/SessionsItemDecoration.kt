package io.github.droidkaigi.confsched2020.session.ui.widget

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.view.forEach
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.ui.item.SessionItem

class SessionsItemDecoration(val adapter: GroupAdapter<*>, val res: Resources) :
    RecyclerView.ItemDecoration() {
    private val textPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            textSize = sessionTimeTextSizeInPx
        }
    }
    private val sessionTimeTextSizeInPx by lazy {
        res.getDimensionPixelSize(R.dimen.session_time_text_size).toFloat()
    }
    private val sessionTimeSpaceInPx by lazy {
        res.getDimensionPixelSize(R.dimen.session_time_space)
    }
    private val sessionTimeTextMarginTopInPx by lazy {
        res.getDimensionPixelSize(R.dimen.session_time_text_margin_top).toFloat()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        var lastStartTimeText: String? = null
        parent.forEach { view ->
            val layoutParams = view.layoutParams as RecyclerView.LayoutParams
            val viewAdapterPosition = layoutParams.viewAdapterPosition
            if (viewAdapterPosition == -1) return@forEach
            val sessionItem = adapter.getItem(viewAdapterPosition) as SessionItem
            val startTimeText = sessionItem.startSessionTime()
            if (lastStartTimeText != startTimeText) {
                c.drawText(
                    startTimeText,
                    0F,
                    view.y + sessionTimeTextMarginTopInPx,
                    textPaint
                )
                lastStartTimeText = startTimeText
            }
        }
    }
}