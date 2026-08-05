package com.example.jecpackcomposeno1

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.jecpackcomposeno1.ui.theme.JecpackComposeNo1Theme
import com.example.jecpackcomposeno1.ui.theme.home.ConstraintLayoutExample
import java.nio.file.WatchEvent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JecpackComposeNo1Theme {
                LoginScreen()
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
    JecpackComposeNo1Theme() {
        LoginScreen()
    }
}

@Composable
fun TestConstraintLayout(modifier: Modifier = Modifier) {
    ConstraintLayout(
        modifier = modifier.fillMaxSize()
    ) {
        val (bg, imageRef, textRef, toFree, boxFile, boxSave, icClose) = createRefs()
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = "success",
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(bg) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                }
        )
        Image(
            painter = painterResource(id = R.drawable.ic_close),
            contentDescription = "success",
            modifier = Modifier
                .size(width = 24.dp, height = 24.dp)
                .constrainAs(icClose) {
                    top.linkTo(parent.top, margin = 56.dp)
                    end.linkTo(parent.end, margin = 24.dp)
                }
        )
        Image(
            painter = painterResource(id = R.drawable.ic_success),
            contentDescription = "success",
            modifier = Modifier.constrainAs(imageRef) {
                top.linkTo(parent.top, margin = 56.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
        Text(
            text = "Almost there!",
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 24.sp,
            modifier = Modifier.constrainAs(textRef) {
                top.linkTo(imageRef.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
        Text(
            text = "To free up you can delete files permanently later.",
            style = MaterialTheme.typography.displayMedium,
            fontSize = 16.sp,
            modifier = Modifier.constrainAs(toFree) {
                top.linkTo(textRef.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(16.dp))
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .constrainAs(boxFile) {
                    top.linkTo(toFree.bottom)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    width = Dimension.fillToConstraints
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_photo),
                    contentDescription = "Icon",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "You have move to Trash",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(2.dp))
                    // Dòng dưới: "2 Photos" đậm tím + "(65 MB)" xám -> dùng buildAnnotatedString
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = Color(0xFF5B3EDE),
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("2 Photos")
                            }
                            append(" ")
                            withStyle(SpanStyle(color = Color.Gray)) {
                                append("(65 MB)")
                            }
                        },
                        fontSize = 16.sp
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(16.dp))
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .constrainAs(boxSave) {
                    top.linkTo(boxFile.bottom, margin = 16.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    width = Dimension.fillToConstraints
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_save),
                    contentDescription = "IconSave",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "You have move to Trash",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    color = Color(0xFF5B3EDE),
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("10 mins")
                            }
                            append(" ")
                            withStyle(SpanStyle(color = Color.Gray)) {
                                append("(using AI Cleaner)")
                            }
                        },
                        fontSize = 16.sp
                    )
                }
            }
        }
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
fun DemoClick() {
    Image(
        painter = painterResource(id = R.drawable.ic_iap),
        contentDescription = "Logo",
        modifier = Modifier.clickable(onClick = {})
    )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

@Composable
fun ImageTest(modifier: Modifier = Modifier) {
    Image(
        alignment = Alignment.TopStart,
        painter = painterResource(id = R.drawable.ic_iap),
        contentDescription = "Logo",
        modifier = modifier.padding(16.dp)
    )
}

@Composable
fun ImageTestVector(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.icons_trash),
        contentDescription = "Xóa",
        modifier = modifier.padding(16.dp)
    )
}

@Composable
fun ButtonTest1(modifier: Modifier = Modifier) {
    Button(
        onClick = {},
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent, contentColor = Color.Red
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
        modifier = modifier.background(
            brush = Brush.horizontalGradient(listOf(Color(0xFF8B55EE), Color(0xFF1BC0F2))),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        ),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        border = BorderStroke(1.dp, Color.Red),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp, focusedElevation = 4.dp, disabledElevation = 0.dp
        )
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_iap),
            contentDescription = "Icon",
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "Click me",
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_iap),
            contentDescription = "Icon",
            modifier = Modifier.size(24.dp)
        )
    }
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JecpackComposeNo1Theme {
        Greeting("Android")
    }
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

