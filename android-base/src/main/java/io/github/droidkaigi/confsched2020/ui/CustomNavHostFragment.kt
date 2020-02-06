package io.github.droidkaigi.confsched2020.ui

import androidx.navigation.NavController
import androidx.navigation.dynamicfeatures.fragment.DynamicNavHostFragment
import androidx.navigation.plusAssign
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.testing.FakeSplitInstallManagerFactory
import io.github.droidkaigi.confsched2020.BuildConfig

class CustomNavHostFragment : DynamicNavHostFragment() {

    override fun onCreateNavController(navController: NavController) {
        super.onCreateNavController(navController)
        context?.let {
            navController.navigatorProvider += ChromeCustomTabsNavigator(it)
        }
    }

    override fun createSplitInstallManager(): SplitInstallManager {
        return if (BuildConfig.USE_FAKE_SPLIT) {
            FakeSplitInstallManagerFactory.create(
                requireContext(),
                requireContext().getExternalFilesDir(null)
            )
        } else {
            SplitInstallManagerFactory.create(requireContext())
        }
    }
}
