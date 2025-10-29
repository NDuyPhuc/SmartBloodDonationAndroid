// feature_auth/src/main/java/com/smartblood/auth/ui/register/RegisterScreen.kt

package com.smartblood.auth.ui.register

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartblood.core.ui.components.PrimaryButton

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    navigateToDashboard: () -> Unit,
    navigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    // Điều hướng khi đăng ký thành công
    LaunchedEffect(state.registrationSuccess) {
        if (state.registrationSuccess) {
            navigateToDashboard()
        }
    }

    // Hiển thị thông báo lỗi
    if (state.error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(RegisterEvent.OnErrorDismissed) },
            title = { Text("Registration Failed") },
            text = { Text(state.error!!) },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(RegisterEvent.OnErrorDismissed) }) {
                    Text("OK")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Create an Account", style = MaterialTheme.typography.headlineMedium)

                OutlinedTextField(
                    value = state.fullName,
                    onValueChange = { viewModel.onEvent(RegisterEvent.OnFullNameChanged(it)) },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.email,
                    onValueChange = { viewModel.onEvent(RegisterEvent.OnEmailChanged(it)) },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.password,
                    onValueChange = { viewModel.onEvent(RegisterEvent.OnPasswordChanged(it)) },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                PrimaryButton(
                    text = "Sign Up",
                    onClick = { viewModel.onEvent(RegisterEvent.OnRegisterClicked) }
                )

                TextButton(onClick = navigateBack) {
                    Text("Already have an account? Log In")
                }
            }
        }
    }
}