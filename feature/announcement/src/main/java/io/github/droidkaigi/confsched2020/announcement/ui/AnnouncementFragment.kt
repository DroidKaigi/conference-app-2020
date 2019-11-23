package io.github.droidkaigi.confsched2020.announcement.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import dagger.android.support.DaggerFragment
import io.github.droidkaigi.confsched2020.announcement.R
import io.github.droidkaigi.confsched2020.announcement.databinding.FragmentAnnouncementBinding
import io.github.droidkaigi.confsched2020.announcement.ui.item.AnnouncementItem
import io.github.droidkaigi.confsched2020.announcement.ui.viewmodel.AnnouncementViewModel
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedActivityViewModels
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import io.github.droidkaigi.confsched2020.ext.toAppError
import io.github.droidkaigi.confsched2020.model.Announcement
import io.github.droidkaigi.confsched2020.model.LoadState
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel
import io.github.droidkaigi.confsched2020.util.ProgressTimeLatch
import javax.inject.Inject
import javax.inject.Provider

class AnnouncementFragment : DaggerFragment() {

    @Inject
    lateinit var announcementModelFactory: AnnouncementViewModel.Factory
    private val announcementViewModel: AnnouncementViewModel by assistedViewModels {
        announcementModelFactory.create()
    }

    @Inject
    lateinit var systemViewModelProvider: Provider<SystemViewModel>
    private val systemViewModel: SystemViewModel by assistedActivityViewModels {
        systemViewModelProvider.get()
    }

    @Inject
    lateinit var announcementItemFactory: AnnouncementItem.Factory

    private lateinit var binding: FragmentAnnouncementBinding

    private lateinit var progressTimeLatch: ProgressTimeLatch

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val groupAdapter = GroupAdapter<ViewHolder<*>>()
        binding.announcementRecycler.adapter = groupAdapter

        progressTimeLatch = ProgressTimeLatch { showProgress ->
            binding.progressBar.isVisible = showProgress
        }.apply {
            loading = true
        }

        announcementViewModel.uiModel.observe(viewLifecycleOwner) { uiModel ->
            progressTimeLatch.loading = uiModel.isLoading
            binding.emptyMessage.isVisible = uiModel.isEmpty
            groupAdapter.update(
                uiModel.announcements.map { announcement -> announcement.toItem() }
            )
            uiModel.error?.let {
                systemViewModel.onError(it)
            }
        }
    }

    private fun Announcement.toItem(): Item<*> {
        return announcementItemFactory.create(this)
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