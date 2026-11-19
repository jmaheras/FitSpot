package com.example.fitspot.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fitspot.viewmodel.ResetStatus

@Composable
fun ForgotPasswordDialog(
    initialEmail: String,
    status: ResetStatus?,
    onSend: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf(initialEmail) }
    var localError by rememberSaveable { mutableStateOf<String?>(null) }
    val sent = status != null && !status.isError

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reset password") },
        text = {
            Column {
                Text("Enter your account email and we'll send you a link to reset your password.")
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; localError = null },
                    label = { Text("Email") },
                    singleLine = true,
                    enabled = !sent,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                val message = localError ?: status?.message
                if (message != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = message,
                        color = if (localError != null || status?.isError == true)
                            MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        confirmButton = {
            if (sent) {
                TextButton(onClick = onDismiss) { Text("Done") }
            } else {
                TextButton(onClick = {
                    if (email.isBlank()) localError = "Enter your email"
                    else onSend(email.trim())
                }) { Text("Send link") }
            }
        },
        dismissButton = {
            if (!sent) TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
