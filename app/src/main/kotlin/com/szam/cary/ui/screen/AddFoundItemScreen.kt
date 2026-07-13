package com.szam.cary.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.szam.cary.data.model.FoundItem
import com.szam.cary.data.model.ItemKategori
import com.szam.cary.ui.components.*
import com.szam.cary.ui.theme.Background
import com.szam.cary.ui.theme.Primary
import com.szam.cary.viewmodel.AuthViewModel
import com.szam.cary.viewmodel.FoundItemViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.szam.cary.utils.isSafeInput

@Composable
fun AddFoundItemScreen(
    foundItemViewModel: FoundItemViewModel,
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val uiState by foundItemViewModel.uiState.collectAsStateWithLifecycle()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    var namaBarang by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf(ItemKategori.LAINNYA) }
    var lokasi by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) }
    var deskripsi by remember { mutableStateOf("") }
    var fotoUri by remember { mutableStateOf<String?>(null) }

    var namaError by remember { mutableStateOf("") }
    var lokasiError by remember { mutableStateOf("") }
    var tanggalError by remember { mutableStateOf("") }
    var deskripsiError by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            foundItemViewModel.clearMessages()
            onSuccess()
        }
    }

    fun validate(): Boolean {
        var valid = true
        namaError = ""; lokasiError = ""; tanggalError = ""; deskripsiError = ""
        if (namaBarang.isBlank()) { namaError = "Nama barang tidak boleh kosong"; valid = false }
        else if (!isSafeInput(namaBarang)) { namaError = "Input mengandung karakter tidak valid"; valid = false }  // ← tambah
        if (lokasi.isBlank()) { lokasiError = "Lokasi tidak boleh kosong"; valid = false }
        else if (!isSafeInput(lokasi)) { lokasiError = "Input mengandung karakter tidak valid"; valid = false }    // ← tambah
        if (tanggal.isBlank()) { tanggalError = "Tanggal tidak boleh kosong"; valid = false }
        if (deskripsi.isBlank()) { deskripsiError = "Deskripsi tidak boleh kosong"; valid = false }
        else if (!isSafeInput(deskripsi)) { deskripsiError = "Input mengandung karakter tidak valid"; valid = false } // ← tambah
        return valid
    }

    Scaffold(
        containerColor = Background,
        topBar = { CaryTopBar(title = "Laporan Barang Ditemukan", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Informasi Barang Ditemukan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    CaryTextField(value = namaBarang, onValueChange = { namaBarang = it; namaError = "" }, label = "Nama Barang", leadingIcon = Icons.Default.Inventory, isError = namaError.isNotEmpty(), errorMessage = namaError)
                    KategoriDropdown(selected = kategori, onSelect = { kategori = it })
                    CaryTextField(value = lokasi, onValueChange = { lokasi = it; lokasiError = "" }, label = "Lokasi Ditemukan", leadingIcon = Icons.Default.LocationOn, isError = lokasiError.isNotEmpty(), errorMessage = lokasiError)
                    CaryTextField(value = tanggal, onValueChange = { tanggal = it; tanggalError = "" }, label = "Tanggal Ditemukan (yyyy-MM-dd)", leadingIcon = Icons.Default.CalendarToday, isError = tanggalError.isNotEmpty(), errorMessage = tanggalError)
                    CaryTextField(value = deskripsi, onValueChange = { deskripsi = it; deskripsiError = "" }, label = "Deskripsi Barang", leadingIcon = Icons.Default.Description, isError = deskripsiError.isNotEmpty(), errorMessage = deskripsiError, singleLine = false, maxLines = 5)
                    ImagePickerSection(selectedUri = fotoUri, onImageSelected = { fotoUri = it })
                }
            }

            Button(
                onClick = {
                    if (validate()) {
                        val item = FoundItem(
                            userId = authState.currentUser?.id ?: "",
                            namaBarang = namaBarang, kategori = kategori, lokasi = lokasi,
                            tanggal = tanggal, deskripsi = deskripsi, fotoUri = fotoUri
                        )
                        foundItemViewModel.addItem(item)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan Laporan", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
