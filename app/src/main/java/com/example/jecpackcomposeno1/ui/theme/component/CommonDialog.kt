package com.example.jecpackcomposeno1.ui.theme.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.jecpackcomposeno1.R

@Composable
fun CommonDialog(
    image: Int,
    title: Int,
    description: Int,
    buttonText: Int,
    onButtonClick: () -> Unit,
    onDismiss: () -> Unit,
    titleArgs: List<Any> = emptyList(),
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Box {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = "Close",
                    tint = colorResource(R.color.color_on_surface_variant_2),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(24.dp)
                        .clickable { onDismiss() }
                )

                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(image),
                        contentDescription = null,
                        modifier = Modifier.size(72.dp)
                    )
                    CommonSpacerHeight(16)
                    Text(
                        text = if (titleArgs.isEmpty()) stringResource(title)
                        else stringResource(title, *titleArgs.toTypedArray()),
                        style = AppTextStyles.Size18SemiBold,
                        color = colorResource(R.color.black),
                        textAlign = TextAlign.Center
                    )
                    CommonSpacerHeight(8)
                    Text(
                        text = stringResource(description),
                        style = AppTextStyles.Size14Medium,
                        color = colorResource(R.color.color_on_surface_variant_2),
                        textAlign = TextAlign.Center
                    )
                    CommonSpacerHeight(20)
                    Text(
                        text = stringResource(buttonText),
                        style = AppTextStyles.Size16SemiBold,
                        color = colorResource(R.color.primary_primary),
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .clickable { onButtonClick() }
                            .padding(horizontal = 32.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}