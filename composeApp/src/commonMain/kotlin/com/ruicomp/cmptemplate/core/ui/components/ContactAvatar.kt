package com.ruicomp.cmptemplate.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ContactAvatar(
    name: String,
    size: Int = 40,
    fontSize: TextUnit = TextUnit.Unspecified,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.firstOrNull()?.toString()?.uppercase() ?: "",
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize
        )
    }
}

@Preview
@Composable
fun ContactAvatarPreview() {
    ContactAvatar(name = "John Doe")
}

@Preview
@Composable
fun ContactAvatarLargePreview() {
    ContactAvatar(
        name = "Alice Smith",
        size = 80,
        fontSize = 32.sp,
        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
        textColor = MaterialTheme.colorScheme.onSecondaryContainer
    )
} 