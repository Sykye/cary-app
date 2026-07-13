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
import com.szam.cary.data.model.ItemKategori
import com.szam.cary.ui.components.*
import com.szam.cary.ui.theme.Background
import com.szam.cary.ui.theme.Primary
import com.szam.cary.viewmodel.AuthViewModel
import com.szam.cary.viewmodel.FoundItemViewModel

@Composable
fun EditFoundItemScreen(
    itemId: String,
    foundItemViewModel: FoundItemViewModel,
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val uiState by foundItemViewModel.uiState.collectAsStateWithLifecycle()

    var namaBarang by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf(ItemKategori.LAINNYA) }
    var lokasi by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var fotoUri by remember { mutableStateOf<String?>(null) }
    var initialized by remember { mutableStateOf(false) }

    var namaError by remember { mutableStateOf("") }
    var lokasiError by remember { mutableStateOf("") }
    var tanggalError by remember { mutableStateOf("") }
    var deskripsiError by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(itemId) { foundItemViewModel.loadItemById(itemId) }

    LaunchedEffect(uiState.selectedItem) {
        if (!initialized && uiState.selectedItem != null) {
            val item = uiState.selectedItem!!
            namaBarang = item.namaBarang
            kategori = item.kategori
            lokasi = item.lokasi
            tanggal = item.tanggal
            deskripsi = item.deskripsi
            fotoUri = item.fotoUri
            initialized = true
        }
    }

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
        if (lokasi.isBlank()) { lokasiError = "Lokasi tidak boleh kosong"; valid = false }
        if (tanggal.isBlank()) { tanggalError = "Tanggal tidak boleh kosong"; valid = false }
        if (deskripsi.isBlank()) { deskripsiError = "Deskripsi tidak boleh kosong"; valid = false }
        return valid
    }

    Scaffold(
        containerColor = Background,
        topBar = { CaryTopBar(title = "Edit Barang Ditemukan", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (uiState.isLoading && !initialized) {
            LoadingOverlay()
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Edit Informasi Barang", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        CaryTextField(value = namaBarang, onValueChange = { namaBarang = it; namaError = "" }, label = "Nama Barang", leadingIcon = Icons.Default.Inventory, isError = namaError.isNotEmpty(), errorMessage = namaError)
                        KategoriDropdown(selected = kategori, onSelect = { kategori = it })
                        CaryTextField(value = lokasi, onValueChange = { lokasi = it; lokasiError = "" }, label = "Lokasi Ditemukan", leadingIcon = Icons.Default.LocationOn, isError = lokasiError.isNotEmpty(), errorMessage = lokasiError)
                        CaryTextField(value = tanggal, onValueChange = { tanggal = it; tanggalError = "" }, label = "Tanggal (yyyy-MM-dd)", leadingIcon = Icons.Default.CalendarToday, isError = tanggalError.isNotEmpty(), errorMessage = tanggalError)
                        CaryTextField(value = deskripsi, onValueChange = { deskripsi = it; deskripsiError = "" }, label = "Deskripsi", leadingIcon = Icons.Default.Description, isError = deskripsiError.isNotEmpty(), errorMessage = deskripsiError, singleLine = false, maxLines = 5)
                        ImagePickerSection(selectedUri = fotoUri, onImageSelected = { fotoUri = it })
                    }
                }

                Button(
                    onClick = {
                        if (validate()) {
                            val existing = uiState.selectedItem!!
                            foundItemViewModel.updateItem(
                                existing.copy(
                                    namaBarang = namaBarang, kategori = kategori, lokasi = lokasi,
                                    tanggal = tanggal, deskripsi = deskripsi, fotoUri = fotoUri
                                )
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    enabled = !uiState.isLoading && initialized
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simpan Perubahan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
