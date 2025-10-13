@file:OptIn(ExperimentalAnimationApi::class)

package com.example.tennisapp.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tennisapp.R
import com.example.tennisapp.roboto
import com.example.tennisapp.ui.components.PagerWeekCalendar
import kotlinx.coroutines.launch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext

@Composable
fun BookingContent(navController: NavController) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var selectedSport by remember { mutableStateOf<String?>(null) }
    var selectedCoach by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }
    var selectedOptions by remember { mutableStateOf(setOf<String>()) }
    var currentStep by remember { mutableStateOf(1) }

    LaunchedEffect(currentStep) {
        snapshotFlow { scrollState.maxValue }.collect { max ->
            if (max > 0) {
                scrollState.animateScrollTo(max)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StepContainer(visible = true) {
            SportSelector(
                selected = selectedSport,
                onSelect = {
                    selectedSport = it
                    currentStep = 2
                    coroutineScope.launch {
                        scrollState.animateScrollTo(scrollState.maxValue)
                    }
                }
            )
        }

        StepContainer(visible = currentStep >= 2) {
            Text("Выберите тренера", style = MaterialTheme.typography.titleMedium.copy(fontFamily = roboto))
            Spacer(modifier = Modifier.height(8.dp))

            val coaches = listOf(
                Coach("Алексей", "Мастер спорта, 8 лет опыта", R.drawable.ic_arrow_right),
                Coach("Мария", "Бывшая чемпионка России", R.drawable.ic_arrow_right),
                Coach("Иван", "Профессиональный игрок, 10 лет стажа", R.drawable.ic_arrow_right),
                Coach("Без тренера", "Самостоятельная тренировка", R.drawable.ic_arrow_right)
            )

            coaches.forEach { coach ->
                CoachCard(
                    coach = coach,
                    isSelected = selectedCoach == coach.name,
                    onClick = {
                        selectedCoach = coach.name
                        currentStep = 3
                        coroutineScope.launch {
                            scrollState.animateScrollTo(scrollState.maxValue)
                        }
                    }
                )
            }
        }

        StepContainer(visible = currentStep >= 3) {
            Text("Выберите дату", style = MaterialTheme.typography.titleMedium.copy(fontFamily = roboto))
            PagerWeekCalendar(onDateSelected = {
                selectedDate = it.toString()
                coroutineScope.launch {
                    currentStep = 4
                    scrollState.animateScrollTo(scrollState.maxValue)
                }
            })
        }

        StepContainer(visible = currentStep >= 4) {
            Text("Выберите время", style = MaterialTheme.typography.titleMedium.copy(fontFamily = roboto))
            val times = listOf("09:00", "10:00", "11:30", "13:00", "15:00", "17:00")

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                times.forEach { time ->
                    TimeChip(time, selectedTime) {
                        selectedTime = time
                        coroutineScope.launch {
                            currentStep = 5
                            scrollState.animateScrollTo(scrollState.maxValue)
                        }
                    }
                }
            }
        }

        StepContainer(visible = currentStep >= 5) {
            Text("Дополнительные опции", style = MaterialTheme.typography.titleMedium.copy(fontFamily = roboto))
            val options = if (selectedSport == "Теннис")
                listOf("Аренда ракеток", "Мячи", "Полотенце")
            else
                listOf("Аренда падел-ракеток", "Мячи для падела", "Вода")

            options.forEach { option ->
                OptionCheckbox(option, selectedOptions.contains(option)) {
                    if (it) {
                        selectedOptions = (selectedOptions + option)
                    } else {
                        selectedOptions = selectedOptions - option
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (selectedSport != null && selectedDate != null && selectedTime != null) {
                        val route = "summary_screen/" +
                                "${Uri.encode(selectedSport)}/" +
                                "${Uri.encode(selectedCoach ?: "Без тренера")}/" +
                                "${Uri.encode(selectedDate)}/" +
                                "${Uri.encode(selectedTime)}/" +
                                "${Uri.encode(selectedOptions.joinToString(";"))}"

                        navController.navigate(route)
                    } else {
                        Toast.makeText(
                            context,
                            "Заполните все обязательные поля: вид спорта, дата и время",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Подтвердить бронирование", color = Color.White, fontFamily = roboto)
            }
        }
    }
}

@Composable
fun StepContainer(
    visible: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(400)
        ) + fadeIn(animationSpec = tween(400)),
        exit = ExitTransition.None
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                content = content
            )
        }
    }
}

@Composable
fun SportSelector(
    options: List<String> = listOf("Теннис", "Падел"),
    selected: String?,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        options.forEach { option ->
            SportTab(
                text = option,
                selected = selected == option,
                onClick = { onSelect(option) }
            )
        }
    }
}

@Composable
fun SportTab(text: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = roboto,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) Color.Black else Color.Gray
            )
        )

        val underlineWidth by animateDpAsState(
            targetValue = if (selected) 40.dp else 0.dp,
            animationSpec = tween(durationMillis = 250),
            label = "underline"
        )

        Box(
            modifier = Modifier
                .height(2.dp)
                .width(underlineWidth)
                .background(Color(0xFF4CAF50), shape = CircleShape)
        )
    }
}

data class Coach(
    val name: String,
    val description: String,
    val imageRes: Int
)

@Composable
fun CoachCard(coach: Coach, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF4CAF50) else Color(0xFFE0E0E0),
        animationSpec = tween(250),
        label = "borderAnim"
    )

    Card(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = coach.imageRes),
                contentDescription = coach.name,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEAEAEA))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = coach.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = roboto,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
                Text(
                    text = coach.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = roboto,
                        color = Color.Gray
                    )
                )
            }
        }
    }
}

@Composable
fun TimeChip(time: String, selected: String?, onClick: () -> Unit) {
    Surface(
        color = if (selected == time) Color(0xFF4CAF50) else Color.White,
        shape = RoundedCornerShape(50),
        border = if (selected != time) BorderStroke(1.dp, Color.Gray) else null,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = time,
            color = if (selected == time) Color.White else Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontFamily = roboto
        )
    }
}

@Composable
fun OptionCheckbox(option: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50))
        )
        Text(option, fontFamily = roboto)
    }
}