package com.example.jecpackcomposeno1.ui.theme.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.jecpackcomposeno1.ui.theme.JecpackComposeNo1Theme

@Composable
fun ConstraintLayoutExample(modifier: Modifier = Modifier) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val (title, subtitle, button) = createRefs()

        Text(
            text = "ConstraintLayout",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        Text(
            text = "Title ở trên, button full width ở dưới",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.constrainAs(subtitle) {
                top.linkTo(title.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        Button(
            onClick = { },
            modifier = Modifier.constrainAs(button) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        ) {
            Text("Button")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConstraintLayoutExamplePreview() {
    JecpackComposeNo1Theme {
        ConstraintLayoutExample()
    }
}
