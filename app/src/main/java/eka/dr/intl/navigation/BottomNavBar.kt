package eka.dr.intl.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import eka.dr.intl.assistant.navigation.AssistantNavModel
import eka.dr.intl.assistant.navigation.navigateToDocAssist
import eka.dr.intl.common.IAmCommon
import eka.dr.intl.common.Restrictions
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral200
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral800
import eka.dr.intl.ekatheme.color.DarwinTouchPrimary
import eka.dr.intl.ekatheme.color.DarwinTouchPrimaryBgLight
import eka.dr.intl.icons.R
import eka.dr.intl.patients.naviagtion.PatientNavModel
import eka.dr.intl.patients.naviagtion.navigateToPatientDirectory
import eka.dr.intl.typography.touchLabelRegular
import eka.dr.intl.ui.atom.IconWrapper
import kotlin.reflect.KClass

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val context = LocalContext.current
    val isDocAssistAllowed = (context.applicationContext as IAmCommon).isAllowedToAccess(Restrictions.DOC_ASSIST)

    val items = buildList {
        add(BottomNavItem.Home)
        if (isDocAssistAllowed) {
            add(BottomNavItem.DocAssist)
        }
        add(BottomNavItem.Patient)
    }
    val haptic = LocalHapticFeedback.current
    NavigationBar(
        containerColor = DarwinTouchNeutral0, modifier = modifier.drawBehind {
            val strokeWidth = 1.dp.toPx()
            drawLine(
                color = DarwinTouchNeutral200,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = strokeWidth
            )
        }
    ) {
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStackEntry?.destination

        items.forEach { item ->
            val isSelected = currentDestination.isRouteInHierarchy(item.route)

            NavigationBarItem(
                icon = {
                    if (isSelected) {
                        item.selectedIcon()
                    } else {
                        item.unselectedIcon()
                    }
                },
                label = {
                    Text(
                        item.title,
                        color = if (isSelected) DarwinTouchPrimary else DarwinTouchNeutral800,
                        style = touchLabelRegular
                    )
                },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        executeBottomNavigation(item, navController)
                    }
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = DarwinTouchPrimaryBgLight
                )
            )
        }
    }
}

fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any {
        it.hasRoute(route)
    } ?: false

fun NavController.defaultNavOptions() = navOptions {
    launchSingleTop = true
}

fun executeBottomNavigation(bottomNavItem: BottomNavItem, navController: NavHostController) {
    val navOptions = navOptions {
        popUpTo(navController.graph.findStartDestination().id) {
            inclusive = true
        }
        launchSingleTop = true
    }
    when (bottomNavItem) {
        is BottomNavItem.Home -> {
            navController.navigateToHome(
                navOptions = navOptions
            )
        }

        is BottomNavItem.Patient -> {
            navController.navigateToPatientDirectory(navOptions = navController.defaultNavOptions())
        }

        is BottomNavItem.DocAssist -> {
            navController.navigateToDocAssist(navOptions = navController.defaultNavOptions())
        }
    }
}


sealed class BottomNavItem(
    val route: KClass<*>,
    val title: String,
    val selectedIcon: @Composable (() -> Unit),
    val unselectedIcon: @Composable (() -> Unit)
) {
    data object Home :
        BottomNavItem(
            route = HomeNavModel::class, title = "Home",
            selectedIcon = {
                IconWrapper(
                    icon = R.drawable.ic_house_solid,
                    contentDescription = "Home",
                    modifier = Modifier.size(20.dp),
                    tint = DarwinTouchPrimary
                )
            }, unselectedIcon = {
                IconWrapper(
                    icon = R.drawable.ic_house_regular,
                    contentDescription = "Home",
                    modifier = Modifier.size(20.dp),
                )
            })

    data object Patient : BottomNavItem(
        PatientNavModel::class, "Patient",
        selectedIcon = {
            IconWrapper(
                icon = R.drawable.ic_magnifying_glass_regular,
                contentDescription = "",
                modifier = Modifier.size(20.dp),
                tint = DarwinTouchPrimary
            )
        },
        unselectedIcon = {
            IconWrapper(
                icon = R.drawable.ic_magnifying_glass_regular,
                contentDescription = "",
                modifier = Modifier.size(20.dp),
            )
        }
    )

    data object DocAssist :
        BottomNavItem(
            AssistantNavModel::class, "DocAssist",
            selectedIcon = {
                IconWrapper(
                    icon = R.drawable.ic_doc_assist_filled_custom,
                    contentDescription = "",
                    modifier = Modifier.size(20.dp),
                    tint = DarwinTouchPrimary
                )
            },
            unselectedIcon = {
                IconWrapper(
                    icon = R.drawable.ic_doc_assist_custom,
                    contentDescription = "",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified
                )
            }
        )
}

