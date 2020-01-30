package io.github.droidkaigi.confsched2020.floormap.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2020.di.Injectable
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.floormap.R

// TODO: Apply the floor map UI
class FloorMapFragment : Fragment(R.layout.fragment_floormap), Injectable {

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
