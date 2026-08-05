package com.example.jecpackcomposeno1.ui.theme.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.jecpackcomposeno1.R

@Composable
fun TextExample() {
    Text(
        text = stringResource(id = R.string.name),
        color = colorResource(id = R.color.purple_200),
        textAlign = TextAlign.End,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}