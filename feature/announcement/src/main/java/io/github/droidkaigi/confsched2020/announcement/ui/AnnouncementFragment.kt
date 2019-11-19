package io.github.droidkaigi.confsched2020.announcement.ui

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
import io.github.droidkaigi.confsched2020.announcement.R
import io.github.droidkaigi.confsched2020.announcement.databinding.FragmentAnnouncementBinding
import io.github.droidkaigi.confsched2020.announcement.ui.viewmodel.AnnouncementViewModel
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import javax.inject.Inject

class AnnouncementFragment : DaggerFragment() {

    @Inject
    lateinit var announcementModelFactory: AnnouncementViewModel.Factory
    private val announcementViewModel: AnnouncementViewModel by assistedViewModels {
        announcementModelFactory.create()
    }

    private lateinit var binding: FragmentAnnouncementBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_announcement,
            container,
            false
        )
        return binding.root
    }

    @Module
    abstract class AnnouncementFragmentModule {

        @Module
        companion object {

            @PageScope
            @JvmStatic
            @Provides
            fun providesLifeCycleLiveData(
                announcementFragment: AnnouncementFragment
            ): LiveData<LifecycleOwner> {
                return announcementFragment.viewLifecycleOwnerLiveData
            }
        }
    }
}
