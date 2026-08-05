package com.example.jecpackcomposeno1.ui.theme.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.jecpackcomposeno1.R

@Composable
fun AISmartCleanButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(percent = 100),
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF3B1DE0), Color(0xFF6B4BFF), Color(0xFF2E5BFF))
                ),
                shape = RoundedCornerShape(percent = 50)
            )
    ) {
        Image(
            modifier = Modifier.size(32.dp),
            painter = painterResource(R.drawable.ic_smart_start),
            contentDescription = null
        )
        CommonSpacerWidth(10)
        Text(
            text = stringResource(R.string.text_ai_smart_clean),
            style = AppTextStyles.Size16SemiBold,
            color = colorResource(R.color.white),
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = colorResource(R.color.white),
            modifier = Modifier.size(24.dp)
        )
    }
}
