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
    private val sessionTimeTextMarginStartInPx by lazy {
        res.getDimensionPixelSize(R.dimen.session_time_text_margin_start).toFloat()
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
        outRect.left = sessionTimeSpaceInPx
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        var lastStartTimeText: String? = null
        parent.forEach { view ->
            val layoutParams = view.layoutParams as RecyclerView.LayoutParams
            val sessionItem = adapter.getItem(layoutParams.viewAdapterPosition) as SessionItem
            sessionItem.session.startTime
            val startTimeText = sessionItem.session.startTimeText
            if (lastStartTimeText != startTimeText) {
                c.drawText(
                    startTimeText,
                    sessionTimeTextMarginStartInPx,
                    view.y + sessionTimeTextMarginTopInPx,
                    textPaint
                )
                lastStartTimeText = startTimeText
            }
        }
    }
}