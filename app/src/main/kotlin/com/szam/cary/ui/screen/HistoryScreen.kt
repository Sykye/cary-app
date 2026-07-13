package com.szam.cary.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.szam.cary.ui.components.*
import com.szam.cary.ui.theme.Background
import com.szam.cary.ui.theme.Primary
import com.szam.cary.viewmodel.AuthViewModel
import com.szam.cary.viewmodel.ClaimViewModel
import com.szam.cary.viewmodel.FoundItemViewModel
import com.szam.cary.viewmodel.LostItemViewModel

@Composable
fun HistoryScreen(
    claimViewModel: ClaimViewModel,
    lostItemViewModel: LostItemViewModel,
    foundItemViewModel: FoundItemViewModel,
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onNavigateToDetailLost: (String) -> Unit,
    onNavigateToDetailFound: (String) -> Unit
) {
    val claimState by claimViewModel.uiState.collectAsStateWithLifecycle()
    val lostState by lostItemViewModel.uiState.collectAsStateWithLifecycle()
    val foundState by foundItemViewModel.uiState.collectAsStateWithLifecycle()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    val userId = authState.currentUser?.id ?: ""

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Laporan Saya", "Klaim Saya")

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            claimViewModel.loadClaimsByUser(userId)
        }
    }

    val myLostItems = lostState.items.filter { it.userId == userId }
    val myFoundItems = foundState.items.filter { it.userId == userId }
    val myClaims = claimState.claims

    Scaffold(
        containerColor = Background,
        topBar = { CaryTopBar(title = "Riwayat", onBack = onBack) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = androidx.compose.ui.graphics.Color.White,
                contentColor = Primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    // Laporan Saya
                    if (myLostItems.isEmpty() && myFoundItems.isEmpty()) {
                        EmptyState(
                            icon = Icons.Default.History,
                            title = "Belum Ada Laporan",
                            subtitle = "Anda belum membuat laporan barang hilang atau ditemukan."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (myLostItems.isNotEmpty()) {
                                item {
                                    SectionHeader(
                                        icon = Icons.Default.SearchOff,
                                        title = "Barang Hilang (${myLostItems.size})"
                                    )
                                }
                                items(myLostItems) { item ->
                                    LostItemCard(
                                        item = item,
                                        onClick = { onNavigateToDetailLost(item.id) }
                                    )
                                }
                            }
                            if (myFoundItems.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    SectionHeader(
                                        icon = Icons.Default.FindInPage,
                                        title = "Barang Ditemukan (${myFoundItems.size})"
                                    )
                                }
                                items(myFoundItems) { item ->
                                    FoundItemCard(
                                        item = item,
                                        onClick = { onNavigateToDetailFound(item.id) }
                                    )
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Klaim Saya
                    if (claimState.isLoading) {
                        LoadingOverlay()
                    } else if (myClaims.isEmpty()) {
                        EmptyState(
                            icon = Icons.Default.Assignment,
                            title = "Belum Ada Klaim",
                            subtitle = "Anda belum mengajukan klaim kepemilikan barang."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                Text(
                                    "${myClaims.size} klaim diajukan",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            items(myClaims) { claim ->
                                ClaimCard(claim = claim)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Primary)
    }
}
