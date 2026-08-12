package com.example.jecpackcomposeno1

import android.annotation.SuppressLint
import android.graphics.Color as AndroidColor
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.jecpackcomposeno1.ui.theme.JetpackComposeNo1Theme
import com.example.jecpackcomposeno1.ui.theme.screen.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // UI app nền sáng -> ép icon status bar (giờ/pin/wifi) màu tối, không để
        // dark theme / dynamic color chọn icon trắng khiến nhìn mờ.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = AndroidColor.TRANSPARENT,
                darkScrim = AndroidColor.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = AndroidColor.TRANSPARENT,
                darkScrim = AndroidColor.TRANSPARENT,
            ),
        )
        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
            // Ẩn thanh điều hướng khi mở app
            hide(WindowInsetsCompat.Type.navigationBars())
            // Vuốt từ cạnh lên -> hiện tạm rồi tự ẩn lại
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        setContent {
            JetpackComposeNo1Theme {
                MainScreen()
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun LoginScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var email by rememberSaveable {
            mutableStateOf("")
        }
        Email(email) {
            email = it
        }
        Button(onClick = {
            email = ""
        }) {
            Text(text = "Click me")
        }
    }
}

@Composable
fun Email(email: String, onEmailChange: (String) -> Unit) {

    Log.e("DATLBT", "LoginScreen: START")
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Username") },
        modifier = Modifier.fillMaxWidth()
    )
    Log.e("DATLBT", "LoginScreen: End")
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    JetpackComposeNo1Theme() {
        LoginScreen()
    }
}


@Composable
fun DemoOutLineTextView() {
    var text by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextFieldDefaults.colors(
        errorLeadingIconColor = Color.Red,
        focusedBorderColor = Color(0xFF8B55EE),
        unfocusedBorderColor = Color.Gray,
        errorBorderColor = Color.Red,
        focusedLabelColor = Color(0xFF8B55EE),
        unfocusedLabelColor = Color.Gray,
        errorLabelColor = Color.Red,
        cursorColor = Color(0xFF8B55EE),
    )
    OutlinedTextField(
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_iap),
                contentDescription = "Leading Icon",
                modifier = Modifier.size(24.dp)
            )
        },
        value = text,
        onValueChange = { text = it },
        label = { Text("Label") },
        placeholder = { Text("Placeholder") },
        modifier = Modifier.padding(24.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = androidx.compose.ui.text.input.ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ),
    )
}


@Composable
fun DemoTextField() {
    var text by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    TextField(
        value = text,
        onValueChange = { text = it },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorTextColor = Color.Red,
            focusedLabelColor = Color.Transparent,
            unfocusedLabelColor = Color.Transparent,
            cursorColor = Color(0xFF8B55EE),
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = androidx.compose.ui.text.input.ImeAction.Next,
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ),
        shape = RoundedCornerShape(8.dp),
        textStyle = TextStyle(
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        ),
        placeholder = { Text("Placeholder") },
        modifier = Modifier.padding(24.dp),
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_iap),
                contentDescription = "Leading Icon",
                modifier = Modifier.size(24.dp)
            )
        },
        trailingIcon = {
            IconButton(onClick = { text = "" }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_iap),
                    contentDescription = "Trailing Icon",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    )
}

@Composable
fun CheckboxParentExample() {
    // Initialize states for the child checkboxes
    val childCheckedStates = remember { mutableStateListOf(false, false, false) }

    // Compute the parent state based on children's states
    val parentState = when {
        childCheckedStates.all { it } -> ToggleableState.On
        childCheckedStates.none { it } -> ToggleableState.Off
        else -> ToggleableState.Indeterminate
    }

    Column {
        // Parent TriStateCheckbox
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.selectable(
                selected = parentState == ToggleableState.On,
                onClick = {
                    // Determine new state based on current state
                    val newState = parentState != ToggleableState.On
                    childCheckedStates.forEachIndexed { index, _ ->
                        childCheckedStates[index] = newState
                    }
                },
                role = Role.Checkbox
            )
        ) {
            Text("Select all")
            TriStateCheckbox(
                state = parentState,
                onClick = null
            )
        }

        // Child Checkboxes
        childCheckedStates.forEachIndexed { index, checked ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.selectable(
                    selected = checked,
                    onClick = {
                        // Toggle the individual child state
                        childCheckedStates[index] = !checked
                    },
                    role = Role.Checkbox
                )
            ) {
                Text("Option ${index + 1}")
                Checkbox(
                    checked = checked,
                    onCheckedChange = { isChecked ->
                        // Update the individual child state
                        childCheckedStates[index] = isChecked
                    }
                )
            }
        }
    }

    if (childCheckedStates.all { it }) {
        Text("All options selected")
    }
}

@Composable
fun CheckboxMinimalExample() {
    var checked by remember { mutableStateOf(true) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.selectable(
            selected = checked,
            onClick = { checked = !checked },
            role = Role.Checkbox
        )
    ) {
        Text(
            "Minimal checkbox"
        )
        Checkbox(
            checked = checked,
            onCheckedChange = null
        )
    }

    Text(
        if (checked) "Checkbox is checked" else "Checkbox is unchecked"
    )
}

@Composable
fun DemoRadioButtonWithText(title: String) {
    var isSelected by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.selectable(
            selected = isSelected,
            onClick = { isSelected = !isSelected }
        )) {
        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color.Red,
                unselectedColor = Color.Gray
            )
        )
        Text(text = title)
    }
}


@Composable
fun DemoCustomRadioButton() {
    var isSelectedState by remember { mutableStateOf(false) }
    CustomRadioButton(
        title = "Custom Radio Button",
        isSelected = isSelectedState
    ) {
        isSelectedState = !isSelectedState
    }
}

@Composable
fun CustomRadioButton(title: String, isSelected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier.selectable(
            selected = isSelected,
            onClick = onSelect,
            role = Role.RadioButton
        )
    ) {
        val iconRadio = if (isSelected) R.drawable.ic_selected else R.drawable.ic_unselected
//        Icon(painter = painterResource(id = iconRadio), contentDescription = null,
//            tint = Color.Unspecified,  modifier = Modifier.size(24.dp)  )

        Image(
            painter = painterResource(id = iconRadio),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Text(text = title)
    }
}

@Composable
fun OptionRow(
    selected: Boolean,
    onSelect: () -> Unit,
    label: String,
    icon: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelect, role = Role.RadioButton)
            .background(
                if (selected) Color(0x228B55EE) else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = if (selected) Color(0xFF8B55EE) else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(label, color = if (selected) Color(0xFF8B55EE) else Color.Black)
    }
}

@Composable
fun CommonSpaceWidth() {
    Spacer(modifier = Modifier.width(16.dp))
}

@Composable
fun CommonSpaceHeight() {
    Spacer(modifier = Modifier.height(16.dp))
}


@Composable
fun RadioButtonSingleSelection(modifier: Modifier = Modifier) {
    val radioOptions = listOf("Calls", "Missed", "Friends")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    Column(modifier.selectableGroup()) {
        radioOptions.forEach { text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = null // null recommended for accessibility with screen readers
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

