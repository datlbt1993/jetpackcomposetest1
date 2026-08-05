package com.example.jecpackcomposeno1.ui.theme.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.jecpackcomposeno1.R
import com.example.jecpackcomposeno1.ui.theme.component.AISmartCleanButton
import com.example.jecpackcomposeno1.ui.theme.component.AppTextStyles
import com.example.jecpackcomposeno1.ui.theme.component.CommonSpacerHeight
import com.example.jecpackcomposeno1.ui.theme.component.CommonSpacerWidth

@Preview
@Composable
fun HomeScreen() {
    Column(modifier = Modifier.background(color = colorResource(R.color.surface_normal))) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(R.drawable.bg_home_top),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.matchParentSize()
            )
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(bottom = 16.dp)
            ) {
                ToolbarHome()
                CommonSpacerHeight(12)
                ViewSmartClean()
            }
        }
        CommonSpacerHeight(12)
        ManualClean()
        CommonSpacerHeight(12)
        SmartTools()
    }
}

@Composable
fun ToolbarHome() {
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { },
        ) {
            Icon(
                modifier = Modifier.size(28.dp),
                painter = painterResource(id = R.drawable.ic_setting),
                contentDescription = null
            )
        }
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = {}) {
            Image(
                painter = painterResource(id = R.drawable.ic_trash),
                contentDescription = null,
            )
        }
        CommonSpacerWidth(int = 12)
    }
}

@Composable
fun ViewSmartClean() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        val (content, broom) = createRefs()
        Column(
            modifier = Modifier.constrainAs(content) {
                top.linkTo(parent.top, margin = 16.dp)
                start.linkTo(parent.start, margin = 12.dp)
                end.linkTo(parent.end, margin = 12.dp)
                width = Dimension.fillToConstraints
            }
        ) {
            Text(
                text = stringResource(R.string.text_storage_used),
                style = AppTextStyles.Size14Medium,
                color = colorResource(R.color.color_on_surface_variant_3)
            )
            CommonSpacerHeight(8)
            Text(
                text = "117.9 BG of 128 GB",
                style = AppTextStyles.Size18SemiBold,
                color = colorResource(R.color.color_on_surface)
            )
            CommonSpacerHeight(8)
            Text(
                text = "0 Files • 0 KB to clean",
                style = AppTextStyles.Size12SemiBold,
                color = colorResource(R.color.color_on_surface_variant_2),
                modifier = Modifier
                    .background(
                        color = colorResource(R.color.color_surface_container_low),
                        shape = RoundedCornerShape(percent = 40)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
            CommonSpacerHeight(12)
            HorizontalDivider(
                thickness = 1.dp,
                color = colorResource(R.color.color_on_surface_variant_2)
            )
            CommonSpacerHeight(8)
            AISmartCleanButton(onClick = {})
            CommonSpacerHeight(12)
        }
        Image(
            painter = painterResource(R.drawable.ic_brom_smart),
            contentDescription = null,
            modifier = Modifier
                .constrainAs(broom) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
        )
    }
}

@Composable
fun ManualClean() {
    Column(
        modifier = Modifier
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(R.string.text_manual_clean),
            style = MaterialTheme.typography.headlineSmall
        )
        CommonSpacerHeight(int = 8)
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ItemPhotoOrVideHome(
                modifier = Modifier.weight(1f),
                bg = R.drawable.ic_bg_photo_home,
                icon = R.drawable.ic_photo_home,
                title = R.string.tv_photos,
                onClick = {}
            )
            ItemPhotoOrVideHome(
                modifier = Modifier.weight(1f),
                bg = R.drawable.ic_bg_video_home,
                icon = R.drawable.ic_video_home,
                title = R.string.tv_videos,
                onClick = {}
            )
        }
    }
}

@Composable
private fun ItemPhotoOrVideHome(
    bg: Int,
    icon: Int,
    title: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .paint(
                painter = painterResource(id = bg),
                contentScale = ContentScale.Crop
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = colorResource(R.color.color_on_surface_variant_2)),
                onClick = onClick
            )
            .padding(12.dp)
    ) {
        Image(
            modifier = Modifier.size(32.dp),
            painter = painterResource(id = icon),
            contentDescription = null
        )
        CommonSpacerHeight(int = 8)
        Text(
            text = stringResource(title),
            style = AppTextStyles.Size16SemiBold,
            color = colorResource(R.color.black)
        )
        CommonSpacerHeight(int = 8)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.text_allow_access),
                style = AppTextStyles.Size16SemiBold,
                color = colorResource(R.color.primary_primary)
            )
            CommonSpacerWidth(int = 4)
            Icon(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = null,
                tint = colorResource(R.color.primary_primary)
            )
        }
    }
}

@Composable
fun SmartTools() {
    Column(
        modifier = Modifier.padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.text_smart_tools),
            style = MaterialTheme.typography.headlineSmall
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
fun ButtonSmartTools(
    onClick: () -> Unit,
    image: Int,
    text: Int
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(percent = 12))
            .background(colorResource(R.color.color_F4F6F8))
            .clickable(
                interactionSource = interactionSource,
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




