package com.szam.cary.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.szam.cary.ui.components.*
import com.szam.cary.ui.theme.Background
import com.szam.cary.ui.theme.Primary
import com.szam.cary.viewmodel.AuthViewModel
import com.szam.cary.viewmodel.FoundItemViewModel

@Composable
fun FoundItemScreen(
    foundItemViewModel: FoundItemViewModel,
    authViewModel: AuthViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onBack: () -> Unit
) {
    val uiState by foundItemViewModel.uiState.collectAsStateWithLifecycle()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val currentUserId = authState.currentUser?.id ?: ""

    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            foundItemViewModel.clearMessages()
        }
    }

    Scaffold(
        containerColor = Background,
        topBar = { CaryTopBar(title = "Barang Ditemukan", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = Primary,
                contentColor = Color.White,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Laporan")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (uiState.isLoading && uiState.items.isEmpty()) {
            LoadingOverlay()
        } else if (uiState.items.isEmpty()) {
            EmptyState(
                icon = Icons.Default.FindInPage,
                title = "Belum Ada Laporan",
                subtitle = "Belum ada laporan barang ditemukan.\nTap tombol + untuk membuat laporan.",
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        "${uiState.items.size} barang ditemukan",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                items(uiState.items) { item ->
                    val isOwner = item.userId == currentUserId
                    FoundItemCard(
                        item = item,
                        onClick = { onNavigateToDetail(item.id) },
                        onEdit = if (isOwner) ({ onNavigateToEdit(item.id) }) else null,
                        onDelete = if (isOwner) ({ showDeleteDialog = item.id }) else null
                    )
                }
            }
        }
    }

    showDeleteDialog?.let { itemId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Hapus Laporan", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus laporan ini?") },
            confirmButton = {
                Button(
                    onClick = { foundItemViewModel.deleteItem(itemId); showDeleteDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Hapus") }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = null }) { Text("Batal") } }
        )
    }
}
