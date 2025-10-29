// feature_auth/src/main/java/com/smartblood/auth/ui/login/LoginScreen.kt

package com.smartblood.auth.ui.login

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
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToDashboard: () -> Unit,
    navigateToRegister: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    // Điều hướng khi đăng nhập thành công
    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) {
            navigateToDashboard()
        }
    }

    // Hiển thị thông báo lỗi
    if (state.error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(LoginEvent.OnErrorDismissed) },
            title = { Text("Login Failed") },
            text = { Text(state.error!!) },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(LoginEvent.OnErrorDismissed) }) {
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
                Text("Welcome Back!", style = MaterialTheme.typography.headlineMedium)

                OutlinedTextField(
                    value = state.email,
                    onValueChange = { viewModel.onEvent(LoginEvent.OnEmailChanged(it)) },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.password,
                    onValueChange = { viewModel.onEvent(LoginEvent.OnPasswordChanged(it)) },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                PrimaryButton(
                    text = "Login",
                    onClick = { viewModel.onEvent(LoginEvent.OnLoginClicked) }
                )

                TextButton(onClick = navigateToRegister) {
                    Text("Don't have an account? Sign Up")
                }
            }
        }
    }
}