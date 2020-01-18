package io.github.droidkaigi.confsched2020.floormap.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import dagger.Module
import dagger.Provides
import dagger.android.support.DaggerFragment
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.floormap.R
import io.github.droidkaigi.confsched2020.floormap.databinding.FragmentFloormapBinding
import io.github.droidkaigi.confsched2020.util.autoCleared

// TODO: Apply the floor map UI
class FloorMapFragment : DaggerFragment() {

    private var binding: FragmentFloormapBinding by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_floormap,
            container,
            false
        )
        return binding.root
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