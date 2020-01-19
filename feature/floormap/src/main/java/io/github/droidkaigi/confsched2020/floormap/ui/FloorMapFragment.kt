package io.github.droidkaigi.confsched2020.floormap.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.floormap.R
import io.github.droidkaigi.confsched2020.floormap.databinding.FragmentFloormapBinding
import io.github.droidkaigi.confsched2020.util.DaggerFragment
import io.github.droidkaigi.confsched2020.util.autoCleared

// TODO: Apply the floor map UI
class FloorMapFragment : DaggerFragment(R.layout.fragment_floormap) {

    private var binding: FragmentFloormapBinding by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFloormapBinding.bind(view)
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
