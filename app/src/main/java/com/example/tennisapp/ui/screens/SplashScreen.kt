package com.example.tennisapp.ui.screens

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.tennisapp.R
import com.example.tennisapp.data.UserDataStore
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }
    val context = LocalContext.current
    val clientIdFlow = UserDataStore.getClientId(context).collectAsState(initial = null)

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                }))
        delay(3000L)
        if (clientIdFlow.value != null) {
            navController.navigate("main_screen") {
                popUpTo("splash_screen") { inclusive = true }
            }
        } else {
            navController.navigate("authorization_screen") {
                popUpTo("splash_screen") { inclusive = true }
            }
        }
    }

    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = R.drawable.ic_tennis_men),
            contentDescription = "Логотип",
            modifier = Modifier.scale(scale.value))
    }
}