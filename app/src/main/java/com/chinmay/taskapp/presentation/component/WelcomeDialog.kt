package com.chinmay.taskapp.presentation.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chinmay.taskapp.presentation.theme.ColorPalette

@Composable
fun WelcomeDialog(userName: String, onNameEntered: (String) -> Unit) {
    var textState by remember { mutableStateOf(TextFieldValue(userName)) }

    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            Button(
                onClick = { onNameEntered(textState.text) },
                colors = ButtonDefaults.buttonColors(containerColor = ColorPalette.PastelBlue) // Soft Blue Button
            ) {
                Text("Continue", color = Color.White)
            }
        },
        title = {
            Text(
                text = "Welcome to Task Manager!",
                fontSize = 22.sp,
                color = ColorPalette.PastelCoral // Soft Purple Title
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Please enter your name", fontSize = 18.sp, color = ColorPalette.PastelPink) // Soft Pink Text
                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 6.dp,
                    color = ColorPalette.PastelLavender, // Light Lavender Input Field
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    BasicTextField(
                        value = textState,
                        onValueChange = { textState = it },
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 18.sp,
                            color = ColorPalette.PastelGray // Soft Gray Text
                        )
                    )
                }
            }
        },
        containerColor = ColorPalette.PastelCoral // Warm Peach Dialog Background
    )
}
