package com.example.tennisapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import com.example.tennisapp.roboto

@Composable
fun PhoneInput(
    phone: String,
    onPhoneChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = phone,
        onValueChange = { input ->
            val digits = input.filter { it.isDigit() }.take(10)
            onPhoneChange(digits)
        },
        label = { Text("Номер телефона") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        textStyle = TextStyle(
            fontFamily = roboto,
            fontSize = 18.sp
        ),
        visualTransformation = phoneVisualTransformation(),
        modifier = modifier.fillMaxWidth()
    )
}

fun phoneVisualTransformation(): VisualTransformation {
    return VisualTransformation { text ->
        val digits = text.text.filter { it.isDigit() }

        val out = StringBuilder("+7 ")
        var digitIndex = 0

        if (digits.isNotEmpty()) {
            out.append("(")
            val end = minOf(3, digits.length)
            out.append(digits.substring(0, end))
            digitIndex = end
        }
        if (digits.length > 3) {
            out.append(") ")
            val end = minOf(6, digits.length)
            out.append(digits.substring(3, end))
            digitIndex = end
        }
        if (digits.length > 6) {
            out.append("-")
            val end = minOf(8, digits.length)
            out.append(digits.substring(6, end))
            digitIndex = end
        }
        if (digits.length > 8) {
            out.append("-")
            val end = minOf(10, digits.length)
            out.append(digits.substring(8, end))
            digitIndex = end
        }

        val transformed = out.toString()

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var transformedOffset = offset

                if (offset >= 0) transformedOffset += 3
                if (offset >= 1) transformedOffset += 1
                if (offset >= 4) transformedOffset += 2
                if (offset >= 7) transformedOffset += 1
                if (offset >= 9) transformedOffset += 1

                return transformedOffset.coerceAtMost(transformed.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                var originalOffset = offset

                if (originalOffset > 3) originalOffset -= 3
                if (originalOffset > 4) originalOffset -= 1
                if (originalOffset > 7) originalOffset -= 2
                if (originalOffset > 11) originalOffset -= 1
                if (originalOffset > 14) originalOffset -= 1

                return originalOffset.coerceIn(0, digits.length)
            }
        }

        TransformedText(AnnotatedString(transformed), offsetMapping)
    }
}