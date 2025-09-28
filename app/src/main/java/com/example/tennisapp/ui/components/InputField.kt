package com.example.tennisapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.tennisapp.roboto

@Composable
fun InputField(
    icon: ImageVector,
    value: String,
    label: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                fontFamily = roboto
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF4CAF50)
            )
        },
        textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = roboto),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    )
}