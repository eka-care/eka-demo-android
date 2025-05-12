package eka.dr.intl.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable

@Serializable
object HomeNavModel

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    navigate(HomeNavModel, navOptions)
}