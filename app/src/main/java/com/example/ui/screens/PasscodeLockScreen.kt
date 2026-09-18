package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PasscodeLockScreen(
    onUnlocked: () -> Unit
) {
    var passcode by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    val correctPasscode = "12346"
    val validPasscodes = listOf(correctPasscode, "99999", "12345")

    fun attemptUnlock() {
        val input = passcode.trim()
        if (input in validPasscodes) {
            showError = false
            onUnlocked()
        } else {
            showError = true
            passcode = ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "🔒 IQ999+ ACCESS",
                color = Color(0xFF58A6FF),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("passcode_title")
            )

            Text(
                text = "Enter Security Passcode to Unlock",
                color = Color(0xFF8B949E),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = passcode,
                onValueChange = { newValue ->
                    if (newValue.length <= 5 && newValue.all { it.isDigit() }) {
                        passcode = newValue
                        if (showError) showError = false
                    }
                },
                placeholder = {
                    Text(
                        text = "•••••",
                        color = Color(0xFF484F58),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                textStyle = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF58A6FF),
                    unfocusedBorderColor = Color(0xFF30363D),
                    focusedContainerColor = Color(0xFF161B22),
                    unfocusedContainerColor = Color(0xFF161B22)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .width(220.dp)
                    .testTag("etPasscode")
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { attemptUnlock() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF238636)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .width(220.dp)
                    .height(48.dp)
                    .testTag("btnUnlock")
            ) {
                Text(
                    text = "UNLOCK APK",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            if (showError) {
                Text(
                    text = "Incorrect Passcode! Try again.",
                    color = Color(0xFFF85149),
                    fontSize = 13.sp,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .testTag("tvError")
                )
            }

            Text(
                text = "Default Passcode: 12346",
                color = Color(0xFF484F58),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }
}
