package com.szam.cary.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.szam.cary.ui.components.CaryTextField
import com.szam.cary.ui.theme.Background
import com.szam.cary.ui.theme.Primary
import com.szam.cary.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    fun validate(): Boolean {
        var valid = true
        emailError = ""
        passwordError = ""
        if (email.isBlank()) { emailError = "Email tidak boleh kosong"; valid = false }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { emailError = "Format email tidak valid"; valid = false }
        if (password.isBlank()) { passwordError = "Password tidak boleh kosong"; valid = false }
        else if (password.length < 6) { passwordError = "Password minimal 6 karakter"; valid = false }
        return valid
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(22.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.FindInPage, contentDescription = null, tint = Color.White, modifier = Modifier.size(44.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Cary", style = MaterialTheme.typography.headlineLarge, color = Color.White, fontWeight = FontWeight.ExtraBold)
                    Text("Masuk ke akun Anda", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Selamat Datang!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Masukkan email dan password Anda", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                Spacer(modifier = Modifier.height(24.dp))

                CaryTextField(
                    value = email,
                    onValueChange = { email = it; emailError = "" },
                    label = "Email",
                    leadingIcon = Icons.Default.Email,
                    isError = emailError.isNotEmpty(),
                    errorMessage = emailError
                )
                Spacer(modifier = Modifier.height(16.dp))
                CaryTextField(
                    value = password,
                    onValueChange = { password = it; passwordError = "" },
                    label = "Password",
                    leadingIcon = Icons.Default.Lock,
                    isError = passwordError.isNotEmpty(),
                    errorMessage = passwordError,
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "Sembunyikan" else "Tampilkan"
                            )
                        }
                    }
                )

                if (uiState.error != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(uiState.error ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        authViewModel.clearError()
                        if (validate()) authViewModel.login(email, password)
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Masuk", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text("Belum punya akun?", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                    TextButton(onClick = { authViewModel.clearError(); onNavigateToRegister() }) {
                        Text("Daftar Sekarang", fontWeight = FontWeight.SemiBold, color = Primary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Akun Demo:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Primary)
                        Text("Email: ahmad@kampus.ac.id", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        Text("Password: password123", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
}
