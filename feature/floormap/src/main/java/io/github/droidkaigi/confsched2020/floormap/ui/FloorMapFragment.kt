package io.github.droidkaigi.confsched2020.floormap.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.navArgs
import dagger.Module
import dagger.Provides
import dagger.android.support.DaggerFragment
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.floormap.R
import io.github.droidkaigi.confsched2020.floormap.databinding.FragmentFloormapBinding
import io.github.droidkaigi.confsched2020.model.FloorMap

// TODO: Apply the floor map UI
class FloorMapFragment : DaggerFragment() {

    private val navArgs: FloorMapFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_floormap,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentFloormapBinding.bind(view)

        binding.floorMapImage.setImageResource(getFloorMapResource())
    }

    private fun getFloorMapResource(): Int {
        val floor = FloorMap.fromId(navArgs.roomId) ?: return R.drawable.ic_floor_map
        return when (floor) {
            FloorMap.APP_BAR -> R.drawable.ic_floor_map_app_bars
            FloorMap.BACKDROP -> R.drawable.ic_floor_map_backdrop
            FloorMap.CARDS -> R.drawable.ic_floor_map_cards
            FloorMap.DIALOGS -> R.drawable.ic_floor_map_dialogs
            FloorMap.PICKERS -> R.drawable.ic_floor_map_pickers
            FloorMap.SLIDERS -> R.drawable.ic_floor_map_sliders
            FloorMap.TABS -> R.drawable.ic_floor_map_tabs
        }
    }

    @Module
    abstract class FloorMapFragmentModule {

        @Module
        companion object {

            @PageScope
            @JvmStatic
            @Provides
            fun providesLifecycleOwnerLiveData(
                floorMapFragment: FloorMapFragment
            ): LiveData<LifecycleOwner> {
                return floorMapFragment.viewLifecycleOwnerLiveData
            }
        }
    }
}
