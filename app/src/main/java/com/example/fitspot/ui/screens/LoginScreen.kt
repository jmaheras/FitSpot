package com.example.fitspot.ui.screens

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.fitspot.ui.theme.FitSpotTheme
import androidx.compose.material3.CircularProgressIndicator


@Composable
fun LoginScreen(
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onSignIn: (String, String) -> Unit,
    onSignUp: (String, String) -> Unit
) {
    DisposableEffect(Unit) {
        Log.d("FitSpotLifeCycle", "LoginScreen: Entered Screen (Composition)")
        onDispose {
            Log.d("FitSpotLifeCycle", "LoginScreen: Exited Screen (Disposed)")
        }
    }

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "FitSpot",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Find the inspo. Shop the look.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                error = null
            },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                error = null
            },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                TextButton(onClick = { showPassword = !showPassword }) {
                    Text(if (showPassword) "Hide" else "Show")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        (error ?: errorMessage)?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    error = "Enter your email and password"
                } else {
                    onSignIn(email.trim(), password)
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(text = "Login")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { Log.d("FitSpotLifeCycle", "LoginScreen: Forgot password clicked") }) {
            Text("Forgot password?")
        }
        TextButton(
            onClick = {
                Log.d("FitSpotLifeCycle", "LoginScreen: Create account clicked")
                if (email.isBlank() || password.isBlank()) {
                    error = "Enter your email and password"
                } else {
                    onSignUp(email.trim(), password)
                }
            },
            enabled = !isLoading
        ) {
            Text("Create account")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    FitSpotTheme {
        LoginScreen(onSignIn = { _, _ -> }, onSignUp = { _, _ -> })
    }
}
