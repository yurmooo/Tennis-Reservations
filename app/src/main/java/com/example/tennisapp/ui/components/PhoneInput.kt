package com.example.tennisapp.ui.components

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.NumberParseException
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.tennisapp.roboto

@Composable
fun PhoneInput(
    phone: String,
    onPhoneChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        val formattedPhone = formatPhone(phone) ?: phone

        AndroidView(
            factory = { context ->
                com.hbb20.CountryCodePicker(context).apply {
                    setCountryForNameCode("RU")
//                    setCcpClickable(false) //Запрет смены страны
                }
            },
            modifier = Modifier
                .padding(top = 15.dp)
                .weight(0.35f)
        )

        OutlinedTextField(
            value = formattedPhone,
            onValueChange = { input ->
                val digits = input.filter { it.isDigit() }
                val trimmed = digits.take(11)
                onPhoneChange(trimmed)
            },
            label = { Text("Номер телефона") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            textStyle = TextStyle(
                fontFamily = roboto,
                fontSize = 18.sp
            ),
            modifier = modifier
        )
    }
}

fun formatPhone(phone: String): String? {
    val phoneUtil = PhoneNumberUtil.getInstance()
    return try {
        val numberProto = phoneUtil.parse(phone, "RU")
        if (phoneUtil.isValidNumber(numberProto)) {
            phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
        } else null
    } catch (e: NumberParseException) {
        null
    }
}