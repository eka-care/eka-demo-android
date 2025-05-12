package eka.dr.intl.patients.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eka.dr.intl.ekatheme.color.DarwinTouchPrimary

@Composable
fun <T> InfiniteCircularList(
    width: Dp,
    itemHeight: Dp,
    numberOfDisplayedItems: Int = 3,
    items: List<T>,
    initialItem: T,
    itemScaleFact: Float = 1.5f,
    textStyle: TextStyle,
    textColor: Color,
    selectedTextColor: Color,
    onItemSelected: (index: Int, item: T) -> Unit = { _, _ -> }
) {
    val itemHalfHeight = LocalDensity.current.run { itemHeight.toPx() / 2f }
    val scrollState = rememberLazyListState(0)
    var lastSelectedIndex by remember {
        mutableIntStateOf(0)
    }
    var itemsState by remember {
        mutableStateOf(items)
    }
    LaunchedEffect(items) {
        var targetIndex = items.indexOf(initialItem) - 1
        targetIndex += ((Int.MAX_VALUE / 2) / items.size) * items.size
        itemsState = items
        lastSelectedIndex = targetIndex
        scrollState.scrollToItem(targetIndex)
    }
    Box(modifier = Modifier.background(White), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .width(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .height(44.dp)
                .background(DarwinTouchPrimary.copy(alpha = 0.2f))
        ) {

        }
        LazyColumn(
            modifier = Modifier
                .width(width)
                .height(itemHeight * numberOfDisplayedItems)
                .fadingEdge(
                    brush = remember {
                        Brush.verticalGradient(
                            0F to Color.Transparent,
                            0.5F to Color.Black,
                            1F to Color.Transparent
                        )
                    },
                ),
            state = scrollState,
            flingBehavior = rememberSnapFlingBehavior(
                lazyListState = scrollState
            )
        ) {
            items(
                count = Int.MAX_VALUE,
                itemContent = { i ->
                    val item = itemsState[i % itemsState.size]
                    Box(
                        modifier = Modifier
                            .height(itemHeight)
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                val y = coordinates.positionInParent().y - itemHalfHeight
                                val parentHalfHeight =
                                    (coordinates.parentCoordinates?.size?.height ?: 0) / 2f
                                val isSelected =
                                    (y > parentHalfHeight - itemHalfHeight && y < parentHalfHeight + itemHalfHeight)
                                if (isSelected && lastSelectedIndex != i) {
                                    onItemSelected(i % itemsState.size, item)
                                    lastSelectedIndex = i
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.toString(),
                            style = textStyle,
                            color = if (lastSelectedIndex == i) {
                                selectedTextColor
                            } else {
                                textColor
                            },
                            fontSize = if (lastSelectedIndex == i) {
                                textStyle.fontSize * itemScaleFact
                            } else {
                                textStyle.fontSize
                            }
                        )
                    }
                }
            )
        }
    }
}

@Stable
fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }