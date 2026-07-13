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
import com.szam.cary.utils.validatePassword

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()

    var nama by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    var namaError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) onRegisterSuccess()
    }

    fun validate(): Boolean {
        var valid = true

        namaError = ""
        emailError = ""
        passwordError = ""
        confirmPasswordError = ""

        if (nama.isBlank()) {
            namaError = "Nama tidak boleh kosong"
            valid = false
        }

        if (email.isBlank()) {
            emailError = "Email tidak boleh kosong"
            valid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Format email tidak valid"
            valid = false
        }

        if (password.isBlank()) {
            passwordError = "Password tidak boleh kosong"
            valid = false
        } else {
            val pwdError = validatePassword(password)
            if (pwdError != null) {
                passwordError = pwdError
                valid = false
            }
        }

        if (confirmPassword.isBlank()) {
            confirmPasswordError = "Konfirmasi password tidak boleh kosong"
            valid = false
        } else if (confirmPassword != password) {
            confirmPasswordError = "Password tidak cocok"
            valid = false
        }

        return valid
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp).background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(72.dp).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Buat Akun Baru", style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)).background(Color.White).padding(24.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Daftar Sekarang", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Isi data diri Anda untuk mulai menggunakan Cary", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                Spacer(modifier = Modifier.height(24.dp))

                CaryTextField(value = nama, onValueChange = { nama = it; namaError = "" }, label = "Nama Lengkap", leadingIcon = Icons.Default.Person, isError = namaError.isNotEmpty(), errorMessage = namaError)
                Spacer(modifier = Modifier.height(16.dp))
                CaryTextField(value = email, onValueChange = { email = it; emailError = "" }, label = "Email", leadingIcon = Icons.Default.Email, isError = emailError.isNotEmpty(), errorMessage = emailError)
                Spacer(modifier = Modifier.height(16.dp))
                CaryTextField(
                    value = password, onValueChange = { password = it; passwordError = "" }, label = "Password",
                    leadingIcon = Icons.Default.Lock, isError = passwordError.isNotEmpty(), errorMessage = passwordError,
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                CaryTextField(
                    value = confirmPassword, onValueChange = { confirmPassword = it; confirmPasswordError = "" }, label = "Konfirmasi Password",
                    leadingIcon = Icons.Default.Lock, isError = confirmPasswordError.isNotEmpty(), errorMessage = confirmPasswordError,
                    trailingIcon = {
                        IconButton(onClick = { confirmVisible = !confirmVisible }) {
                            Icon(if (confirmVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                        }
                    }
                )

                if (uiState.error != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), shape = RoundedCornerShape(12.dp)) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(uiState.error ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { authViewModel.clearError(); if (validate()) authViewModel.register(nama, email, password) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Daftar", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text("Sudah punya akun?", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                    TextButton(onClick = { authViewModel.clearError(); onNavigateToLogin() }) {
                        Text("Masuk", fontWeight = FontWeight.SemiBold, color = Primary)
                    }
                }
            }
        }
    }
}
