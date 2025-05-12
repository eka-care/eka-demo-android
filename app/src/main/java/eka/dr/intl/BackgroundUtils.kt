package eka.dr.intl

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination
import eka.dr.intl.assistant.navigation.AssistantNavModel
import eka.dr.intl.assistant.navigation.ChatBotPatientSessionNavModel
import eka.dr.intl.common.presentation.component.BackgroundType
import eka.dr.intl.ekatheme.color.Blue50
import eka.dr.intl.ekatheme.color.DarwinTouchPrimaryBgDark
import eka.dr.intl.ekatheme.color.FuchsiaViolet100
import eka.dr.intl.navigation.HomeNavModel
import eka.dr.intl.navigation.isRouteInHierarchy
import eka.dr.intl.patients.naviagtion.PatientNavModel

object BackgroundUtils {
    sealed class RouteBackground {
        data object White : RouteBackground()
        data object DarwinPrimary : RouteBackground()
        data object BlueGradient : RouteBackground()
    }

    private fun getRouteBackground(currentRoute: NavDestination?): RouteBackground {
        return when {
            isPatientRelatedRoute(currentRoute) -> RouteBackground.White
            isHomeRoute(currentRoute) -> RouteBackground.DarwinPrimary
            isChatRoute(currentRoute) -> RouteBackground.BlueGradient
            else -> RouteBackground.White
        }
    }

    private fun isPatientRelatedRoute(route: NavDestination?) = route?.let {
        it.isRouteInHierarchy(PatientNavModel::class) ||
                it.isRouteInHierarchy(ChatBotPatientSessionNavModel::class)
    } ?: false

    private fun isHomeRoute(route: NavDestination?) =
        route?.isRouteInHierarchy(HomeNavModel::class) ?: false

    private fun isChatRoute(route: NavDestination?) = route?.let {
        it.isRouteInHierarchy(AssistantNavModel::class)
    } ?: false

    fun getPaddingColor(currentRoute: NavDestination?): BackgroundType {
        return when (getRouteBackground(currentRoute)) {
            RouteBackground.White -> BackgroundType.Solid(Color.White)
            RouteBackground.DarwinPrimary -> BackgroundType.Solid(DarwinTouchPrimaryBgDark)
            RouteBackground.BlueGradient -> BackgroundType.Gradient(
                Brush.horizontalGradient(
                    colorStops = arrayOf(
                        0.4f to Blue50,
                        1f to FuchsiaViolet100
                    )
                )
            )
        }
    }
}