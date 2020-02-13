package io.github.droidkaigi.confsched2020.session.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

class BottomSheetMotionLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MotionLayout(context, attrs, defStyleAttr) {
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val frameLayout = parent as? FrameLayout
        val layoutParams = frameLayout?.layoutParams as? CoordinatorLayout.LayoutParams
        val bottomSheetBehavior = layoutParams?.behavior as? BottomSheetBehavior<*>
        bottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                progress = 1 - slideOffset
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }
        })
    }


    private var undergoingMotion: Boolean = false

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        // super.onNestedScroll(...) {
        //     consumed[0] += dxUnconsumed;
        //     consumed[1] += dyUnconsumed;
        // }

        // Only add to consumed if we are still undergoing motion, or a child is consuming the scroll
        if (undergoingMotion || dxConsumed != 0 || dyConsumed != 0) {
            consumed[0] += dxUnconsumed
            consumed[1] += dyUnconsumed
        }
        // Reset undergoingMotion, as onNestedPreScroll will always set this back to true if we are still undergoing
        // motion
        undergoingMotion = false

    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        super.onNestedPreScroll(target, dx, dy, consumed, type)
        // If consumed is non-zero, then this MotionLayout is changing its progress
        if (consumed[0] != 0 || consumed[1] != 0) {
            undergoingMotion = true
        }
    }
}
