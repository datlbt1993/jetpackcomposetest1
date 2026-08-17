package com.example.jecpackcomposeno1.ui.theme.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.jecpackcomposeno1.R
import com.example.jecpackcomposeno1.ui.theme.component.AppTextStyles
import com.example.jecpackcomposeno1.ui.theme.component.CommonSpacerHeight
import com.example.jecpackcomposeno1.ui.theme.component.CommonSpacerWidth

/**
 * Phần UI chỉ có ở flavor aicleaner.
 * Mỗi flavor phải có hàm cùng signature [HomeBrandContent] (cùng package).
 */
@Composable
fun HomeBrandContent() {
    SmartTools()
}

@Composable
private fun SmartTools() {
    Column(
        modifier = Modifier.padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.text_smart_tools),
            style = MaterialTheme.typography.headlineSmall,
            color = colorResource(R.color.color_on_surface),
        )
        CommonSpacerHeight(int = 8)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ButtonSmartTools(
                onClick = {},
                image = R.drawable.ic_smart_large_old_file,
                text = R.string.text_large_and_old_files
            )
            CommonSpacerHeight(int = 8)
            ButtonSmartTools(
                onClick = {},
                image = R.drawable.ic_smart_calendar,
                text = R.string.text_calendar_events
            )
            CommonSpacerHeight(int = 8)
            ButtonSmartTools(
                onClick = {},
                image = R.drawable.ic_smart_contact,
                text = R.string.text_all_contacts
            )
        }
    }
}

@Composable
private fun ButtonSmartTools(
    onClick: () -> Unit,
    image: Int,
    text: Int
) {
    val interactionSource = remember { MutableInteractionSource() } // MutableInteractionSource là bộ đếm tương tác của composable: press, hover, drag, focus…
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(percent = 12))
            .background(colorResource(R.color.color_F4F6F8))
            .clickable(
                interactionSource = interactionSource, // ripple custom, thì interactionSource bắt buộc có khi tự set màu cho ripple để nó biết khi nào click
                indication = ripple(color = colorResource(R.color.color_on_surface_variant_2)),
                onClick = onClick
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(32.dp),
            painter = painterResource(image),
            contentDescription = null
        )
        CommonSpacerWidth(10)
        Text(
            text = stringResource(text),
            style = AppTextStyles.Size16SemiBold,
            color = colorResource(R.color.black),
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = colorResource(R.color.color_on_surface_variant_3),
            modifier = Modifier.size(24.dp)
        )
    }
}
