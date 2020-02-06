package io.github.droidkaigi.confsched2020.floormap.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.navArgs
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2020.di.Injectable
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.floormap.R
import io.github.droidkaigi.confsched2020.floormap.databinding.FragmentFloormapBinding
import io.github.droidkaigi.confsched2020.model.Room

// TODO: Apply the floor map UI
class FloorMapFragment : Fragment(R.layout.fragment_floormap), Injectable {

    private val navArgs: FloorMapFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentFloormapBinding.bind(view)

        navArgs.room?.getRoomTypeResource()?.let { resId ->
            binding.floorMapImage.setImageResource(resId)
        }
    }

    @Module
    abstract class FloorMapFragmentModule {

        companion object {

            @PageScope
            @Provides
            fun providesLifecycleOwnerLiveData(
                floorMapFragment: FloorMapFragment
            ): LiveData<LifecycleOwner> {
                return floorMapFragment.viewLifecycleOwnerLiveData
            }
        }
    }
}

@DrawableRes
fun Room.getRoomTypeResource(): Int {
    val type = this.roomType ?: return R.drawable.ic_floor_map
    return when (type) { // TODO: Add pin images
        Room.RoomType.EXHIBITION -> R.drawable.ic_floor_map
        Room.RoomType.APP_BAR -> R.drawable.ic_floor_map
        Room.RoomType.BACKDROP -> R.drawable.ic_floor_map
        Room.RoomType.CARDS -> R.drawable.ic_floor_map
        Room.RoomType.DIALOGS -> R.drawable.ic_floor_map
        Room.RoomType.PICKERS -> R.drawable.ic_floor_map
        Room.RoomType.SLIDERS -> R.drawable.ic_floor_map
        Room.RoomType.TABS -> R.drawable.ic_floor_map
    }
}
