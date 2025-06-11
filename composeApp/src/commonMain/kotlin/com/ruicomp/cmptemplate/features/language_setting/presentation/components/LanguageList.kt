package com.ruicomp.cmptemplate.features.language_setting.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ruicomp.cmptemplate.features.language_setting.domain.models.Language
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LanguageList(
    languages: List<Language>,
    onSelect: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp) // Optional: Adds spacing between items
    ) {
        items(languages) { language ->
            LanguageItem(
                language = language,
                onClick = { onSelect(language.code) }
            )
        }
    }
}


@Preview
@Composable
fun LanguageListPreview() {
    LanguageList(
        languages = listOf(
            Language("en", "English", null, isSelected = true),
            Language("es", "Spanish", null),
            Language("fr", "French", null)
        ),
        onSelect = {}
    )
}