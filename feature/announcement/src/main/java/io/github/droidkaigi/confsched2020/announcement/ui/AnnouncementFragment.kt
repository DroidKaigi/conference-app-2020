package io.github.droidkaigi.confsched2020.announcement.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import io.github.droidkaigi.confsched2020.announcement.R
import io.github.droidkaigi.confsched2020.announcement.databinding.FragmentAnnouncementBinding
import io.github.droidkaigi.confsched2020.announcement.ui.item.AnnouncementItem
import io.github.droidkaigi.confsched2020.announcement.ui.viewmodel.AnnouncementViewModel
import io.github.droidkaigi.confsched2020.di.PageScope
import io.github.droidkaigi.confsched2020.ext.assistedActivityViewModels
import io.github.droidkaigi.confsched2020.ext.assistedViewModels
import io.github.droidkaigi.confsched2020.system.ui.viewmodel.SystemViewModel
import io.github.droidkaigi.confsched2020.util.DaggerFragment
import io.github.droidkaigi.confsched2020.util.ProgressTimeLatch
import io.github.droidkaigi.confsched2020.util.autoCleared
import javax.inject.Inject
import javax.inject.Provider

class AnnouncementFragment : DaggerFragment(R.layout.fragment_announcement) {

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

    private var binding: FragmentAnnouncementBinding by autoCleared()

    private var progressTimeLatch: ProgressTimeLatch by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAnnouncementBinding.bind(view)

        val groupAdapter = GroupAdapter<ViewHolder<*>>()
        binding.announcementRecycler.run {
            addItemDecoration(AnnouncementItemDecoration(resources.getDimension(R.dimen.announcement_item_offset)))
            adapter = groupAdapter
            doOnApplyWindowInsets { recyclerView, insets, initialState ->
                // Set a bottom padding due to the system UI is enabled.
                recyclerView.updatePadding(bottom = insets.systemWindowInsetBottom + initialState.paddings.bottom)
            }
        }

        progressTimeLatch = ProgressTimeLatch { showProgress ->
            binding.progressBar.isVisible = showProgress
        }.apply {
            loading = true
        }

        announcementViewModel.uiModel.observe(viewLifecycleOwner) { uiModel ->
            progressTimeLatch.loading = uiModel.isLoading
            binding.emptyMessage.isVisible = uiModel.isEmpty
            groupAdapter.update(
                uiModel.announcements.map { announcement -> announcementItemFactory.create(announcement) }
            )
            uiModel.error?.let {
                systemViewModel.onError(it)
            }
        }
    }

    private class AnnouncementItemDecoration(val offset: Float) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            val position = parent.getChildLayoutPosition(view)
            if (position > 0) {
                outRect.top = offset.toInt()
            }
        }
    }

    @Module
    abstract class AnnouncementFragmentModule {

        @Module
        companion object {

            @PageScope
            @JvmStatic
            @Provides
            fun providesLifecycleOwnerLiveData(
                announcementFragment: AnnouncementFragment
            ): LiveData<LifecycleOwner> {
                return announcementFragment.viewLifecycleOwnerLiveData
            }
        }
    }
}
