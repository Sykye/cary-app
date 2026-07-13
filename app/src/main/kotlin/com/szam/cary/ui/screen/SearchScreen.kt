package com.szam.cary.ui.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.szam.cary.data.model.ItemKategori
import com.szam.cary.data.model.ItemStatus
import com.szam.cary.ui.components.*
import com.szam.cary.ui.theme.Background
import com.szam.cary.ui.theme.Primary
import com.szam.cary.viewmodel.SearchViewModel
import androidx.compose.foundation.background

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    onBack: () -> Unit,
    onNavigateToDetailLost: (String) -> Unit,
    onNavigateToDetailFound: (String) -> Unit
) {
    val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { searchViewModel.search() }

    Scaffold(
        containerColor = Background,
        topBar = { CaryTopBar(title = "Pencarian", onBack = onBack) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Search bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Primary)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = { searchViewModel.updateQuery(it) },
                    placeholder = { Text("Cari nama barang, lokasi...", color = Color.White.copy(alpha = 0.7f)) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                    },
                    trailingIcon = {
                        if (uiState.query.isNotEmpty()) {
                            IconButton(onClick = { searchViewModel.updateQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Hapus", tint = Color.White)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    ),
                    singleLine = true
                )
            }

            // Category filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.selectedKategori == null,
                    onClick = { searchViewModel.updateKategori(null) },
                    label = { Text("Semua") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Primary,
                        selectedLabelColor = Color.White
                    )
                )
                ItemKategori.entries.forEach { kategori ->
                    FilterChip(
                        selected = uiState.selectedKategori == kategori,
                        onClick = { searchViewModel.updateKategori(if (uiState.selectedKategori == kategori) null else kategori) },
                        label = { Text(kategori.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Status filter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Status:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                FilterChip(
                    selected = uiState.selectedStatus == null,
                    onClick = { searchViewModel.updateStatus(null) },
                    label = { Text("Semua") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Primary, selectedLabelColor = Color.White)
                )
                ItemStatus.entries.forEach { status ->
                    FilterChip(
                        selected = uiState.selectedStatus == status,
                        onClick = { searchViewModel.updateStatus(if (uiState.selectedStatus == status) null else status) },
                        label = { Text(status.label) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Primary, selectedLabelColor = Color.White)
                    )
                }
            }

            // Results
            val totalResults = uiState.lostItems.size + uiState.foundItems.size

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (totalResults == 0) {
                EmptyState(
                    icon = Icons.Default.SearchOff,
                    title = "Tidak Ada Hasil",
                    subtitle = "Coba gunakan kata kunci lain\natau ubah filter pencarian."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            "$totalResults hasil ditemukan",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    if (uiState.lostItems.isNotEmpty()) {
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SearchOff, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Barang Hilang (${uiState.lostItems.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                            }
                        }
                        items(uiState.lostItems) { item ->
                            LostItemCard(item = item, onClick = { onNavigateToDetailLost(item.id) })
                        }
                    }

                    if (uiState.foundItems.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FindInPage, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Barang Ditemukan (${uiState.foundItems.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                            }
                        }
                        items(uiState.foundItems) { item ->
                            FoundItemCard(item = item, onClick = { onNavigateToDetailFound(item.id) })
                        }
                    }
                }
            }
        }
    }
}
