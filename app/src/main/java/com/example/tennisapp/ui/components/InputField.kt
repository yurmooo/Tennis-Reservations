package com.example.tennisapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.tennisapp.roboto

@Composable
fun InputField(
    icon: ImageVector,
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    onSave: (String) -> Unit
) {
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    var isFocused = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                fontFamily = roboto
            )
        },
        leadingIcon = { Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF4CAF50)) },
        trailingIcon = {
            if (isFocused.value) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Сохранить",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.clickable {
                        onSave(value)
                        focusManager.clearFocus()
                    }
                )
            }
        },
        textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = roboto),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .onFocusChanged { focusState ->
                isFocused.value = focusState.isFocused
                if (!focusState.isFocused) {
                    onSave(value)
                }
            },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
                onSave(value)
                focusManager.clearFocus()
            }
        )
    )
}