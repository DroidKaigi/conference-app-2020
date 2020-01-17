package io.github.droidkaigi.confsched2020.ui

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.plusAssign

class CustomNavHostFragment : NavHostFragment() {

    override fun onCreateNavController(navController: NavController) {
        super.onCreateNavController(navController)
        context?.let {
            navController.navigatorProvider += ChromeCustomTabsNavigator(it)
        }
    }
}
