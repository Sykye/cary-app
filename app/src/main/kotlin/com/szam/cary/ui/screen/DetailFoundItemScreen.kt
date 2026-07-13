package com.szam.cary.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.szam.cary.data.model.ItemStatus
import com.szam.cary.ui.components.*
import com.szam.cary.ui.theme.Background
import com.szam.cary.ui.theme.Primary
import com.szam.cary.ui.theme.StatusDitemukan
import com.szam.cary.viewmodel.AuthViewModel
import com.szam.cary.viewmodel.FoundItemViewModel

@Composable
fun DetailFoundItemScreen(
    itemId: String,
    foundItemViewModel: FoundItemViewModel,
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToClaim: (String, String) -> Unit
) {
    val uiState by foundItemViewModel.uiState.collectAsStateWithLifecycle()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val item = uiState.selectedItem
    val isOwner = item?.userId == authState.currentUser?.id

    LaunchedEffect(itemId) { foundItemViewModel.loadItemById(itemId) }

    Scaffold(
        containerColor = Background,
        topBar = {
            CaryTopBar(
                title = "Detail Barang Ditemukan",
                onBack = onBack,
                actions = {
                    if (isOwner) {
                        IconButton(onClick = { onNavigateToEdit(itemId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading && item == null) {
            LoadingOverlay()
        } else if (item == null) {
            EmptyState(icon = Icons.Default.Error, title = "Tidak Ditemukan", subtitle = "Barang tidak ditemukan", modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(16.dp)).background(StatusDitemukan.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (item.fotoUri != null) {
                            AsyncImage(model = item.fotoUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.FindInPage, contentDescription = null, tint = StatusDitemukan.copy(alpha = 0.5f), modifier = Modifier.size(64.dp))
                                Text("Tidak ada foto", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }

                item {
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                                Text(item.namaBarang, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                Spacer(modifier = Modifier.width(8.dp))
                                StatusBadge(status = item.status)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            DetailRow(icon = Icons.Default.Category, label = "Kategori", value = item.kategori.label)
                            DetailRow(icon = Icons.Default.LocationOn, label = "Lokasi Ditemukan", value = item.lokasi)
                            DetailRow(icon = Icons.Default.CalendarToday, label = "Tanggal", value = item.tanggal)
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Deskripsi", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(item.deskripsi, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }

                if (!isOwner && item.status != ItemStatus.SUDAH_DIKEMBALIKAN) {
                    item {
                        Button(
                            onClick = { onNavigateToClaim(itemId, "found") },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Icon(Icons.Default.Assignment, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Klaim Ini Milik Saya", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (isOwner && item.status == ItemStatus.DITEMUKAN) {
                    item {
                        OutlinedButton(
                            onClick = { /* Mark as returned */ },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tandai Sudah Dikembalikan", color = Primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
