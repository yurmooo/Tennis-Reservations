@file:OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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

@Composable
fun PagerWeekCalendar(
    modifier: Modifier = Modifier,
    onDateSelected: (Date) -> Unit = {}
) {
    var currentWeekOffset by remember { mutableStateOf(0) }
    val today = remember { Calendar.getInstance().time }
    var selectedDate by remember { mutableStateOf(today) }

    val totalWeeksInMonth = remember { getWeeksInCurrentMonth() }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { totalWeeksInMonth }
    )

    LaunchedEffect(pagerState.currentPage) {
        currentWeekOffset = pagerState.currentPage
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Верхняя панель
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (pagerState.currentPage > 0) {
                        currentWeekOffset--
                    }
                },
                enabled = pagerState.currentPage > 0
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_left),
                    contentDescription = "Предыдущая неделя",
                    tint = if (pagerState.currentPage > 0) Color(0xFF4CAF50) else Color(0xFFBDBDBD)
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
                    if (pagerState.currentPage < totalWeeksInMonth - 1) {
                        currentWeekOffset++
                    }
                },
                enabled = pagerState.currentPage < totalWeeksInMonth - 1
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = "Следующая неделя",
                    tint = if (pagerState.currentPage < totalWeeksInMonth - 1)
                        Color(0xFF4CAF50) else Color(0xFFBDBDBD)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Календарь по неделям
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val weekOffset = page
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
        animationSpec = tween(durationMillis = 250)
    )

    val textColor by animateColorAsState(
        targetValue = when {
            isSelected -> Color.White
            isToday -> Color(0xFF4CAF50)
            else -> Color(0xFF333333)
        },
        animationSpec = tween(durationMillis = 250)
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.take(2),
                color = textColor,
                fontSize = 10.sp,
                fontFamily = roboto
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

// --- Вспомогательные функции ---
fun getDatesForWeek(offset: Int): List<Date> {
    val cal = Calendar.getInstance()
    cal.set(Calendar.DAY_OF_MONTH, 1)
    cal.add(Calendar.WEEK_OF_YEAR, offset)
    cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
    return (0 until 7).map {
        cal.time.also { cal.add(Calendar.DAY_OF_YEAR, 1) }
    }
}

fun getWeeksInCurrentMonth(): Int {
    val cal = Calendar.getInstance()
    val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    return (maxDay / 7.0).let {
        if (maxDay % 7 == 0) it.toInt() else it.toInt() + 1
    }
}