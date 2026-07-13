package com.szam.cary.ui.screen

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.szam.cary.data.model.Claim
import com.szam.cary.ui.components.*
import com.szam.cary.ui.theme.Background
import com.szam.cary.ui.theme.Primary
import com.szam.cary.viewmodel.AuthViewModel
import com.szam.cary.viewmodel.ClaimViewModel
import com.szam.cary.viewmodel.FoundItemViewModel
import com.szam.cary.viewmodel.LostItemViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ClaimScreen(
    itemId: String,
    itemType: String,
    claimViewModel: ClaimViewModel,
    foundItemViewModel: FoundItemViewModel,
    lostItemViewModel: LostItemViewModel,
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val claimState by claimViewModel.uiState.collectAsStateWithLifecycle()
    val foundState by foundItemViewModel.uiState.collectAsStateWithLifecycle()
    val lostState by lostItemViewModel.uiState.collectAsStateWithLifecycle()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    var alasan by remember { mutableStateOf("") }
    var alasanError by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    // Load the item
    LaunchedEffect(itemId, itemType) {
        if (itemType == "found") foundItemViewModel.loadItemById(itemId)
        else lostItemViewModel.loadItemById(itemId)
    }

    val itemNama = if (itemType == "found") {
        foundState.selectedItem?.namaBarang ?: "Memuat..."
    } else {
        lostState.selectedItem?.namaBarang ?: "Memuat..."
    }

    LaunchedEffect(claimState.successMessage) {
        claimState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            claimViewModel.clearMessages()
            onSuccess()
        }
    }

    LaunchedEffect(claimState.error) {
        claimState.error?.let {
            snackbarHostState.showSnackbar(it)
            claimViewModel.clearMessages()
        }
    }

    fun validate(): Boolean {
        alasanError = ""
        if (alasan.isBlank()) { alasanError = "Alasan klaim tidak boleh kosong"; return false }
        if (alasan.length < 20) { alasanError = "Alasan terlalu singkat, minimal 20 karakter"; return false }
        return true
    }

    Scaffold(
        containerColor = Background,
        topBar = { CaryTopBar(title = "Ajukan Klaim", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Info banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.08f)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            "Pengajuan Klaim Kepemilikan",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Berikan alasan yang jelas dan detail mengapa barang ini milik Anda. Sertakan ciri khas atau bukti kepemilikan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            // Item info
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Informasi Barang", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (itemType == "found") Icons.Default.FindInPage else Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(itemNama, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                if (itemType == "found") "Barang Ditemukan" else "Barang Hilang",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }

            // Alasan form
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Alasan Klaim *", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Jelaskan mengapa barang ini milik Anda (min. 20 karakter)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = alasan,
                        onValueChange = { alasan = it; alasanError = "" },
                        placeholder = { Text("Contoh: Barang itu milik saya. Di dalamnya terdapat KTM atas nama saya, foto keluarga, dan uang tunai sejumlah...") },
                        isError = alasanError.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth().heightIn(min = 140.dp),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 8,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            focusedLabelColor = Primary,
                            cursorColor = Primary
                        )
                    )
                    if (alasanError.isNotEmpty()) {
                        Text(alasanError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
                    }
                    Text(
                        "${alasan.length} karakter",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (alasan.length < 20) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                    )
                }
            }

            // Tips card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("💡 Tips Klaim yang Kuat", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf(
                        "Sebutkan isi atau ciri khas barang secara detail",
                        "Sebutkan nomor seri atau identitas unik barang",
                        "Ceritakan kronologi kehilangan barang",
                        "Sebutkan kapan terakhir menggunakan barang"
                    ).forEach { tip ->
                        Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                            Text("•", style = MaterialTheme.typography.bodySmall, color = Primary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(tip, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }

            Button(
                onClick = {
                    if (validate()) {
                        val claim = Claim(
                            itemId = itemId,
                            itemNama = itemNama,
                            itemType = itemType,
                            userId = authState.currentUser?.id ?: "",
                            alasan = alasan,
                            tanggal = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        )
                        claimViewModel.submitClaim(claim)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                enabled = !claimState.isLoading
            ) {
                if (claimState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kirim Klaim", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
