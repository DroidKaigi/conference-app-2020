package io.github.droidkaigi.confsched2020.session.ui.item

import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.xwray.groupie.databinding.BindableItem
import io.github.droidkaigi.confsched2020.model.Session
import io.github.droidkaigi.confsched2020.model.SpeechSession
import io.github.droidkaigi.confsched2020.session.R
import io.github.droidkaigi.confsched2020.session.databinding.ItemSessionDetailMaterialBinding

class SessionDetailMaterialItem @AssistedInject constructor(
    @Assisted private val session: Session,
    @Assisted private val listener: Listener
) : BindableItem<ItemSessionDetailMaterialBinding>(session.id.hashCode().toLong()) {

    interface Listener {
        fun onClickMovie(movieUrl: String)

        fun onClickSlide(slideUrl: String)
    }

    override fun getLayout() = R.layout.item_session_detail_material

    override fun bind(binding: ItemSessionDetailMaterialBinding, position: Int) {
        setUpMaterialData(binding)
    }

    private fun setUpMaterialData(binding: ItemSessionDetailMaterialBinding) {
        if (session is SpeechSession) {
            session.videoUrl?.let {
                setUpMovieView(binding, it)
            } ?: setUpNoMovieView(binding)
            session.slideUrl?.let {
                setUpSlideView(binding, it)
            } ?: setUpNoSlideView(binding)
        }
        setUpNoMovieView(binding)
        setUpNoSlideView(binding)
    }

    private fun setUpNoMovieView(binding: ItemSessionDetailMaterialBinding) {
        val context = binding.movie.context
        val icVideo = ContextCompat.getDrawable(context, R.drawable.ic_video_24dp)
        icVideo?.let { binding.movie.setLeftDrawable(it, 24) }
    }

    private fun setUpMovieView(binding: ItemSessionDetailMaterialBinding, movieUrl: String) {
        val context = binding.movie.context
        val icVideo =
            ContextCompat.getDrawable(context, R.drawable.ic_video_light_blue_24dp)
        icVideo?.let { binding.movie.setLeftDrawable(it, 24) }
        binding.movie.setTextColor(ContextCompat.getColor(context, R.color.light_blue_300))
        binding.movie.setOnClickListener {
            listener.onClickMovie(movieUrl)
        }
    }

    private fun setUpNoSlideView(binding: ItemSessionDetailMaterialBinding) {
        val context = binding.slide.context
        val icSlide = ContextCompat.getDrawable(context, R.drawable.ic_slide_24dp)
        icSlide?.let { binding.slide.setLeftDrawable(it, 24) }
    }

    private fun setUpSlideView(binding: ItemSessionDetailMaterialBinding, slideUrl: String) {
        val context = binding.slide.context
        val icSlide =
            ContextCompat.getDrawable(context, R.drawable.ic_slide_light_blue_24dp)
        icSlide?.let { binding.slide.setLeftDrawable(it, 24) }
        binding.slide.setTextColor(ContextCompat.getColor(context, R.color.light_blue_300))
        binding.slide.setOnClickListener {
            listener.onClickSlide(slideUrl)
        }
    }

    private fun TextView.setLeftDrawable(drawable: Drawable, sizeDp: Int = 32) {
        val res = context.resources
        val widthPx = (sizeDp * res.displayMetrics.density).toInt()
        val heightPx = (sizeDp * res.displayMetrics.density).toInt()
        drawable.setBounds(0, 0, widthPx, heightPx)
        setCompoundDrawables(
            drawable, null, null, null
        )
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(
            session: Session,
            listener: Listener
        ): SessionDetailMaterialItem
    }
}
