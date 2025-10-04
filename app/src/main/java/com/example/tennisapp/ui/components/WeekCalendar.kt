@file:OptIn(ExperimentalAnimationApi::class)

package com.example.tennisapp.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tennisapp.R
import com.example.tennisapp.roboto
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerWeekCalendar(
    modifier: Modifier = Modifier,
    onDateSelected: (Date) -> Unit = {}
) {
    var currentWeekOffset by remember { mutableStateOf(0) }
    val today = remember { getToday() }
    var selectedDate by remember { mutableStateOf(today) }

    val pagerState = rememberPagerState(
        pageCount = { Int.MAX_VALUE },
        initialPage = Int.MAX_VALUE / 2
    )

    LaunchedEffect(pagerState.currentPage) {
        currentWeekOffset = pagerState.currentPage - (Int.MAX_VALUE / 2)
    }

    LaunchedEffect(currentWeekOffset) {
        val targetPage = currentWeekOffset + (Int.MAX_VALUE / 2)
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (currentWeekOffset > 0) {
                        currentWeekOffset--
                    }
                },
                enabled = currentWeekOffset > 0
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_left),
                    contentDescription = "Предыдущая неделя",
                    tint = if (currentWeekOffset > 0) Color(0xFF4CAF50) else Color(0xFFBDBDBD)
                )
            }

            Text(
                text = SimpleDateFormat("MMMM yyyy", Locale("ru"))
                    .format(getDatesForWeek(currentWeekOffset).first()),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    fontFamily = roboto
                )
            )

            IconButton(
                onClick = {
                    if (!isLastWeekOfMonth(currentWeekOffset)) {
                        currentWeekOffset++
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = "Следующая неделя",
                    tint = if (!isLastWeekOfMonth(currentWeekOffset)) Color(0xFF4CAF50) else Color(0xFFBDBDBD)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val weekOffset = page - (Int.MAX_VALUE / 2)
            val dates = getDatesForWeek(weekOffset)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                dates.forEach { date ->
                    val day = SimpleDateFormat("EE", Locale("ru")).format(date).uppercase()
                    val dayNumber = SimpleDateFormat("d", Locale.getDefault()).format(date)
                    val isSelected = date == selectedDate
                    val isToday = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date) ==
                            SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(today)

                    SmoothDayItem(
                        day = day,
                        dayNumber = dayNumber,
                        isSelected = isSelected,
                        isToday = isToday,
                        onClick = {
                            selectedDate = date
                            onDateSelected(date)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SmoothDayItem(
    day: String,
    dayNumber: String,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = when {
            isSelected -> Color(0xFF4CAF50)
            isToday -> Color(0x224CAF50)
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 300)
    )

    val textColor by animateColorAsState(
        targetValue = when {
            isSelected -> Color.White
            isToday -> Color(0xFF4CAF50)
            else -> Color(0xFF333333)
        },
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = day.take(2),
                color = textColor,
                fontSize = 10.sp,
                fontFamily = roboto,
                maxLines = 1
            )
            Text(
                text = dayNumber,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = roboto
            )
        }
    }
}

fun getDatesForWeek(offset: Int): List<Date> {
    val cal = Calendar.getInstance()
    cal.add(Calendar.WEEK_OF_YEAR, offset)
    cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
    return (0 until 7).map {
        cal.time.also { cal.add(Calendar.DAY_OF_YEAR, 1) }
    }
}

fun getToday(): Date = Calendar.getInstance().time

fun isLastWeekOfMonth(offset: Int): Boolean {
    val cal = Calendar.getInstance()
    cal.add(Calendar.WEEK_OF_YEAR, offset)
    val currentMonth = cal.get(Calendar.MONTH)
    cal.add(Calendar.WEEK_OF_YEAR, 1)
    return cal.get(Calendar.MONTH) != currentMonth
}