@file:OptIn(ExperimentalAnimationApi::class)

package com.example.tennisapp.ui.screens

import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.util.Log
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
import coil.compose.AsyncImage
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
import com.example.tennisapp.data.Trainer
import com.example.tennisapp.data.UserDataStore
import com.example.tennisapp.database.createBooking
import com.example.tennisapp.database.getBookings
import com.example.tennisapp.database.getTrainers
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun BookingContent(navController: NavController) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var selectedSport by remember { mutableStateOf<String?>(null) }
    var selectedCoach by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }
    var bookedTimes by remember { mutableStateOf<Set<String>>(emptySet()) } // Занятые времена
    var isLoadingTimes by remember { mutableStateOf(false) } // Загрузка данных
    var selectedOptions by remember { mutableStateOf(setOf<String>()) }
    var currentStep by remember { mutableStateOf(1) }
    var trainers by remember { mutableStateOf<List<Trainer>>(emptyList()) }
    val clientId by UserDataStore.getClientId(context).collectAsState(initial = null)
    val formattedSelectedDate = remember(selectedDate) {
        selectedDate?.let {
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(it)
        } ?: "Не выбрана"
    }

    // При изменении даты загружаем занятые слоты
    LaunchedEffect(selectedDate) {
        selectedDate?.let { date ->
            val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
            isLoadingTimes = true

            getBookings(
                context = context,
                date = dateString,
                onSuccess = { bookedSlots ->
                    bookedTimes = bookedSlots.toSet()
                    isLoadingTimes = false
                    Log.d("BookingContent", "Занятые слоты: $bookedTimes")
                },
                onError = { error ->
                    Log.e("BookingContent", "Ошибка загрузки слотов: $error")
                    bookedTimes = emptySet()
                    isLoadingTimes = false
                    Toast.makeText(context, "Ошибка загрузки доступных времен", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        getTrainers(context,
            onSuccess = { trainersList ->
                // Добавляем вариант "Без тренера"
                trainers = trainersList + Trainer(
                    id = 0,
                    name = "Без тренера",
                    specialization = "Самостоятельная тренировка",
                    photoUrl = null
                )
            },
            onError = { error ->
                Toast.makeText(context, "Ошибка: $error", Toast.LENGTH_SHORT).show()
                // На случай ошибки — хотя бы "Без тренера"
                trainers = listOf(
                    Trainer(
                        id = 0,
                        name = "Без тренера",
                        specialization = "Самостоятельная тренировка",
                        photoUrl = null
                    )
                )
            }
        )
    }

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

            trainers.forEach { trainer ->
                TrainerCard(
                    trainer = trainer,
                    isSelected = selectedCoach == trainer.name,
                    priceText = if (trainer.name == "Без тренера") "+0₽" else "+800₽",
                    onClick = {
                        selectedCoach = trainer.name
                        currentStep = 3
                        coroutineScope.launch { scrollState.animateScrollTo(scrollState.maxValue) }
                    }
                )
            }
        }

        StepContainer(visible = currentStep >= 3) {
            Text("Выберите дату", style = MaterialTheme.typography.titleMedium.copy(fontFamily = roboto))
            PagerWeekCalendar(
                selectedDate = selectedDate ?: Calendar.getInstance().time, // Передаем текущую или выбранную дату
                onDateSelected = { date ->
                    selectedDate = date // Сохраняем Date объект
                    coroutineScope.launch {
                        currentStep = 4
                        scrollState.animateScrollTo(scrollState.maxValue)
                    }
                }
            )
        }

        StepContainer(visible = currentStep >= 4) {
            Text("Выберите время", style = MaterialTheme.typography.titleMedium.copy(fontFamily = roboto))

            if (isLoadingTimes) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                Text("Загрузка доступных времен...", fontFamily = roboto)
            } else {
                val times = listOf("09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00")

                // Показываем сообщение если все времена заняты
                val availableTimes = times.filter { it !in bookedTimes }
                if (availableTimes.isEmpty()) {
                    Text(
                        "На выбранную дату нет свободных временных слотов",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Red,
                            fontFamily = roboto
                        ),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    times.forEach { time ->
                        val isBooked = time in bookedTimes
                        TimeChip(
                            time = time,
                            selected = selectedTime,
                            selectedDate = selectedDate, // ✅ добавляем это
                            isBooked = isBooked,
                            onClick = {
                                if (!isBooked) {
                                    selectedTime = time
                                    coroutineScope.launch {
                                        currentStep = 5
                                        scrollState.animateScrollTo(scrollState.maxValue)
                                    }
                                }
                            }
                        )
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
                        // Рассчитываем итоговую стоимость
                        val basePrice = when (selectedSport) {
                            "Теннис" -> 1500
                            "Падел" -> 2000
                            else -> 0
                        }
                        val coachPrice = if (selectedCoach != null && selectedCoach != "Без тренера") 800 else 0
                        val optionsPrice = calculateOptionsPrice(selectedOptions)
                        val totalPrice = basePrice + coachPrice + optionsPrice

                        // Правильный формат маршрута
                        val route = buildString {
                            append("summary_screen/")
                            append("${Uri.encode(selectedSport ?: "")}/")
                            append("${Uri.encode(selectedCoach ?: "Без тренера")}/")
                            append("${Uri.encode(formattedSelectedDate ?: "")}/")
                            append("${Uri.encode(selectedTime ?: "")}/")
                            append("$totalPrice") // Цена как отдельный параметр
                            if (selectedOptions.isNotEmpty()) {
                                append("?options=${Uri.encode(selectedOptions.joinToString(";"))}")
                            }
                        }

                        Log.d("Navigation", "Navigating to: $route")
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

@Composable
fun TrainerCard(
    trainer: Trainer,
    isSelected: Boolean,
    priceText: String, // Добавляем параметр для цены
    onClick: () -> Unit
) {
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
            .clickable { onClick() }
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = trainer.photoUrl ?: R.drawable.ic_person_placeholder,
                contentDescription = trainer.name,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEAEAEA))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = trainer.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = roboto,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
                Text(
                    text = trainer.specialization ?: "",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = roboto,
                        color = Color.Gray
                    )
                )
                Text(
                    text = priceText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = roboto,
                        color = Color.Green
                    )
                )
            }
        }
    }
}

@Composable
fun TimeChip(
    time: String,
    selected: String?,
    selectedDate: Date?, // Дата для сравнения
    isBooked: Boolean = false,
    onClick: () -> Unit
) {
    val now = remember { Calendar.getInstance() }

    // Разделяем строку "HH:mm"
    val timeParts = time.split(":")
    val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 0
    val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0

    // Создаем календарь для выбранного слота
    val slotCalendar = Calendar.getInstance().apply {
        selectedDate?.let { date ->
            timeInMillis = date.time // ✅ правильно присваиваем миллисекунды
        }
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
    }

    // Проверяем, прошло ли это время
    val isPast = slotCalendar.before(now)

    // Цвета для разных состояний
    val backgroundColor = when {
        isPast -> Color(0xFFEEEEEE)
        isBooked -> Color(0xFFCCCCCC)
        selected == time -> Color(0xFF4CAF50)
        else -> Color.White
    }

    val textColor = when {
        isPast -> Color(0xFF999999)
        isBooked -> Color(0xFF666666)
        selected == time -> Color.White
        else -> Color.Black
    }

    // Отображение кнопки
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(50),
        border = if (!isBooked && !isPast && selected != time) BorderStroke(1.dp, Color.Gray) else null,
        modifier = Modifier.clickable(
            enabled = !isBooked && !isPast,
            onClick = onClick
        )
    ) {
        Text(
            text = time,
            color = textColor,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontFamily = roboto
        )
    }
}

@Composable
fun OptionCheckbox(option: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    // Функция для получения цены опции
    fun getOptionPrice(optionName: String): String {
        return when (optionName) {
            "Аренда ракеток", "Аренда падел-ракеток" -> "300 ₽"
            "Вода" -> "0 ₽"
            "Полотенце", "Мячи", "Мячи для падела" -> "50 ₽"
            else -> "0 ₽"
        }
    }

    val priceText = getOptionPrice(option)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween // Распределяем пространство между элементами
    ) {
        // Левая часть - чекбокс и название услуги
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f) // Занимает доступное пространство
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50))
            )
            Text(
                text = option,
                fontFamily = roboto,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Правая часть - цена
        Text(
            text = if (priceText == "0 ₽") "Бесплатно" else priceText,
            fontFamily = roboto,
            color = if (priceText == "0 ₽") Color.Gray else Color(0xFF4CAF50),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

fun calculateOptionsPrice(options: Set<String>): Int {
    var price = 0
    options.forEach { option ->
        price += when (option) {
            "Аренда ракеток", "Аренда падел-ракеток" -> 300
            "Полотенце", "Мячи", "Мячи для падела" -> 50
            "Вода" -> 0
            else -> 0
        }
    }
    return price
}