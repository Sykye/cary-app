package com.szam.cary.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.szam.cary.data.model.ItemStatus
import com.szam.cary.ui.components.*
import com.szam.cary.ui.theme.*
import com.szam.cary.viewmodel.AuthViewModel
import com.szam.cary.viewmodel.FoundItemViewModel
import com.szam.cary.viewmodel.LostItemViewModel

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    lostItemViewModel: LostItemViewModel,
    foundItemViewModel: FoundItemViewModel,
    onNavigateToLostItems: () -> Unit,
    onNavigateToFoundItems: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToDetailLost: (String) -> Unit,
    onNavigateToDetailFound: (String) -> Unit,
    onLogout: () -> Unit
) {
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val lostState by lostItemViewModel.uiState.collectAsStateWithLifecycle()
    val foundState by foundItemViewModel.uiState.collectAsStateWithLifecycle()

    val user = authState.currentUser
    val recentLost = lostState.items.take(3)
    val recentFound = foundState.items.take(3)

    val totalHilang = lostState.items.count { it.status == ItemStatus.HILANG }
    val totalDitemukan = foundState.items.count { it.status == ItemStatus.DITEMUKAN }
    val totalDikembalikan = lostState.items.count { it.status == ItemStatus.SUDAH_DIKEMBALIKAN } + foundState.items.count { it.status == ItemStatus.SUDAH_DIKEMBALIKAN }

    Scaffold(
        containerColor = Background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Header
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().background(Primary).padding(20.dp)
                ) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("Halo, ${user?.nama?.split(" ")?.firstOrNull() ?: "Pengguna"}! 👋", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                                Text("Semoga barang Anda segera ditemukan", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                            }
                            Box(
                                modifier = Modifier.size(44.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)).clickable(onClick = onNavigateToProfile),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = "Profil", tint = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        // Search bar
                        Row(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = 0.2f)).clickable(onClick = onNavigateToSearch).padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.8f))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Cari barang hilang atau ditemukan...", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                }
            }

            // Stats
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Padding16 {
                    Text("Statistik Kampus", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(title = "Hilang", count = totalHilang, icon = Icons.Default.SearchOff, color = StatusHilang, modifier = Modifier.weight(1f))
                        StatCard(title = "Ditemukan", count = totalDitemukan, icon = Icons.Default.FindInPage, color = StatusDitemukan, modifier = Modifier.weight(1f))
                        StatCard(title = "Dikembalikan", count = totalDikembalikan, icon = Icons.Default.CheckCircle, color = StatusDikembalikan, modifier = Modifier.weight(1f))
                    }
                }
            }

            // Quick Actions
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Padding16 {
                    Text("Aksi Cepat", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        QuickActionCard(
                            icon = Icons.Default.SearchOff,
                            title = "Barang Hilang",
                            subtitle = "Lihat semua laporan",
                            color = StatusHilang,
                            onClick = onNavigateToLostItems,
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionCard(
                            icon = Icons.Default.FindInPage,
                            title = "Barang Temuan",
                            subtitle = "Lihat barang ditemukan",
                            color = StatusDitemukan,
                            onClick = onNavigateToFoundItems,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        QuickActionCard(
                            icon = Icons.Default.History,
                            title = "Riwayat",
                            subtitle = "Laporan & klaim saya",
                            color = StatusDalamKlaim,
                            onClick = onNavigateToHistory,
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionCard(
                            icon = Icons.Default.ManageSearch,
                            title = "Pencarian",
                            subtitle = "Cari dengan filter",
                            color = Primary,
                            onClick = onNavigateToSearch,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Recent Lost
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Padding16 {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Laporan Hilang Terbaru", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        TextButton(onClick = onNavigateToLostItems) { Text("Lihat Semua", color = Primary) }
                    }
                }
            }
            if (recentLost.isEmpty()) {
                item {
                    Padding16 {
                        Text("Belum ada laporan barang hilang.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                    }
                }
            } else {
                items(recentLost) { item ->
                    Padding16 {
                        com.szam.cary.ui.components.LostItemCard(item = item, onClick = { onNavigateToDetailLost(item.id) })
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Recent Found
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Padding16 {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Barang Ditemukan Terbaru", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        TextButton(onClick = onNavigateToFoundItems) { Text("Lihat Semua", color = Primary) }
                    }
                }
            }
            if (recentFound.isEmpty()) {
                item {
                    Padding16 {
                        Text("Belum ada laporan barang ditemukan.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                    }
                }
            } else {
                items(recentFound) { item ->
                    Padding16 {
                        com.szam.cary.ui.components.FoundItemCard(item = item, onClick = { onNavigateToDetailFound(item.id) })
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
private fun Padding16(content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp), content = content)
}
