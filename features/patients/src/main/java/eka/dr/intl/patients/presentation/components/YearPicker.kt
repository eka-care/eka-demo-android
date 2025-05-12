package eka.dr.intl.patients.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral0
import eka.dr.intl.ekatheme.color.DarwinTouchNeutral1000
import eka.dr.intl.ekatheme.color.DarwinTouchPrimary
import eka.dr.intl.patients.presentation.viewModels.AddPatientViewModel
import eka.dr.intl.typography.touchCalloutBold
import eka.dr.intl.typography.touchSubheadlineRegular
import eka.dr.intl.typography.touchTitle3Bold
import eka.dr.intl.typography.touchTitle3Regular
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun YearPicker(
    onClickExit: () -> Unit,
    onAgeChange: (Long) -> Unit,
    viewModel: AddPatientViewModel,
) {
    val yearList = remember {
        (0..120).toList()
    }
    val monthList = remember {
        (0..12).map { it }
    }
    val dayList = remember {
        (0..31).toList()
    }



    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarwinTouchNeutral0)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Select age",
            color = DarwinTouchNeutral1000,
            style = touchTitle3Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "Years",
                    color = DarwinTouchNeutral1000,
                    style = touchSubheadlineRegular,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                InfiniteCircularList(width = 100.dp,
                    itemHeight = 70.dp,
                    items = yearList.toMutableList(),
                    initialItem = viewModel.selectedYear,
                    textStyle = touchTitle3Regular,
                    textColor = DarwinTouchNeutral1000,
                    selectedTextColor = DarwinTouchNeutral1000,
                    onItemSelected = { _, item ->
                        viewModel.selectedYear = item
                    })
            }
            Spacer(
                modifier = Modifier
                    .width(4.dp)
                    .background(Color.Red)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "Months",
                    color = DarwinTouchNeutral1000,
                    style = touchSubheadlineRegular,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                InfiniteCircularList(width = 100.dp,
                    itemHeight = 70.dp,
                    items = monthList.toMutableList(),
                    initialItem = viewModel.selectedMonth,
                    textStyle = touchTitle3Regular,
                    textColor = DarwinTouchNeutral1000,
                    selectedTextColor = DarwinTouchNeutral1000,
                    onItemSelected = { _, item ->
                        viewModel.selectedMonth = item
                    })
            }
            Spacer(
                modifier = Modifier
                    .width(4.dp)
                    .background(Color.Red)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "Days",
                    color = DarwinTouchNeutral1000,
                    style = touchSubheadlineRegular,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                InfiniteCircularList(width = 100.dp,
                    itemHeight = 70.dp,
                    items = dayList.toMutableList(),
                    initialItem = viewModel.selectedDay,
                    textStyle = touchTitle3Regular,
                    textColor = DarwinTouchNeutral1000,
                    selectedTextColor = DarwinTouchNeutral1000,
                    onItemSelected = { _, item ->
                        viewModel.selectedDay = item
                    })
            }
        }
        Button(
            onClick = {
                val age = convertAgeToEpoch(
                    years = viewModel.selectedYear,
                    month = viewModel.selectedMonth,
                    days = viewModel.selectedDay
                )
                onAgeChange(age)
                onClickExit()
            }, modifier = Modifier.fillMaxWidth(), colors = ButtonColors(
                containerColor = DarwinTouchPrimary,
                disabledContainerColor = DarwinTouchPrimary,
                contentColor = DarwinTouchNeutral0,
                disabledContentColor = DarwinTouchNeutral0
            )
        ) {
            val displayAge = getAge(viewModel)
            Text(
                text = "Select $displayAge", style = touchCalloutBold, color = DarwinTouchNeutral0
            )
        }
        Spacer(
            modifier = Modifier.padding(
                WindowInsets.navigationBarsIgnoringVisibility.asPaddingValues(
                    LocalDensity.current
                ).calculateBottomPadding()
            )
        )
    }

}

fun convertAgeToEpoch(years: Int, month: Int, days: Int): Long {
    val currentDate = Calendar.getInstance()
    currentDate.add(Calendar.YEAR, -years)
    currentDate.add(Calendar.MONTH, -month)
    currentDate.add(Calendar.DAY_OF_MONTH, -days)
    return currentDate.timeInMillis
}

fun getAge(viewModel: AddPatientViewModel): String {
    val yearValue = if (viewModel.selectedYear == 0) "" else "${viewModel.selectedYear}y"
    val monthValue = if (viewModel.selectedMonth == 0) "" else "${viewModel.selectedMonth}m"
    val dayValue = if (viewModel.selectedDay == 0) "" else "${viewModel.selectedDay}d"
    return "$yearValue $monthValue $dayValue".trim()
}