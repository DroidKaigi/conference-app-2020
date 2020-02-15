package io.github.droidkaigi.confsched2020.session.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.NestedScrollingParent
import androidx.core.view.NestedScrollingParent2
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.ViewCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlin.reflect.KClass

class BottomSheetMotionLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MotionLayout(context, attrs, defStyleAttr) {
    private var nestedScrollParent: NestedScrollingParent? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        nestedScrollParent = findParent()
        val parent = parent as? View
        val layoutParams = parent?.layoutParams as? CoordinatorLayout.LayoutParams
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

    private inline fun <reified T : Any> View.findParent(): T? {
        return this.findParentImpl(T::class)
    }

    tailrec fun <T : Any> View.findParentImpl(clazz: KClass<T>): T? {
        val parent = parent
        @Suppress("UNCHECKED_CAST")
        if (parent::class == clazz) return parent as T
        if (parent == null) return null
        return (parent as? View)?.findParentImpl<T>(clazz)
    }

    // NestedScrollingParent3
    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        (nestedScrollParent as? NestedScrollingParent3)?.onNestedScroll(
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed
        )
    }

    // NestedScrollingParent2
    override fun onStartNestedScroll(
        child: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return (nestedScrollParent as? NestedScrollingParent2)?.onStartNestedScroll(
            child,
            target,
            axes,
            type
        ) ?: false
    }

    override fun onNestedScrollAccepted(
        child: View,
        target: View,
        axes: Int,
        type: Int
    ) {
        (nestedScrollParent as? NestedScrollingParent2)?.onNestedScrollAccepted(
            child,
            target,
            axes,
            type
        )
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        (nestedScrollParent as? NestedScrollingParent2)?.onStopNestedScroll(target, type)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        (nestedScrollParent as? NestedScrollingParent2)?.onNestedScroll(
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type
        )
    }

    override fun onNestedPreScroll(
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        (nestedScrollParent as? NestedScrollingParent2)?.onNestedPreScroll(
            target,
            dx,
            dy,
            consumed,
            type
        )
    }

    // NestedScrollingParent
    override fun onStartNestedScroll(
        child: View,
        target: View,
        nestedScrollAxes: Int
    ): Boolean {
        return nestedScrollParent?.onStartNestedScroll(
            child,
            target,
            nestedScrollAxes
        ) ?: false
    }

    override fun onNestedScrollAccepted(
        child: View,
        target: View,
        nestedScrollAxes: Int
    ) {
        nestedScrollParent?.onNestedScrollAccepted(
            child,
            target,
            nestedScrollAxes
        )
    }

    override fun onStopNestedScroll(target: View) {
        nestedScrollParent?.onStopNestedScroll(target)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        nestedScrollParent?.onNestedScroll(
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed
        )
    }

    override fun onNestedPreScroll(
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray
    ) {
        nestedScrollParent?.onNestedPreScroll(
            target,
            dx,
            dy,
            consumed
        )
    }

    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        return nestedScrollParent?.onNestedFling(target, velocityX, velocityY, consumed) ?: false
    }

    override fun onNestedPreFling(
        target: View,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return nestedScrollParent?.onNestedPreFling(target, velocityX, velocityY) ?: false
    }

    override fun getNestedScrollAxes(): Int {
        return nestedScrollParent?.nestedScrollAxes ?: ViewCompat.SCROLL_AXIS_NONE
    }
}
