package eka.dr.intl.patients.presentation.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshContainer(
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    pullToRefreshState: PullToRefreshState,
    content: @Composable (() -> Unit),
) {
//    Box(modifier = modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)) {
//        content()
//        if (pullToRefreshState.isRefreshing) {
//            LaunchedEffect(true) {
//                onRefresh()
//            }
//        }
//
//        LaunchedEffect(isRefreshing) {
//            if (isRefreshing) {
//                pullToRefreshState.startRefresh()
//            } else {
//                pullToRefreshState.endRefresh()
//            }
//        }
//
//        PullToRefreshContainer(
//            state = pullToRefreshState,
//            modifier = Modifier.align(Alignment.TopCenter),
//            containerColor = if (pullToRefreshState.verticalOffset != 0f) PullToRefreshDefaults.containerColor else Color.Transparent,
//            contentColor = if (pullToRefreshState.verticalOffset != 0f) PullToRefreshDefaults.contentColor else Color.Transparent,
//        )
//    }
}