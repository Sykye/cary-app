package com.szam.cary.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.szam.cary.data.model.ItemKategori
import com.szam.cary.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KategoriDropdown(
    selected: ItemKategori,
    onSelect: (ItemKategori) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = selected.label,
                onValueChange = {},
                readOnly = true,
                label = { Text("Kategori") },
                leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                isError = isError,
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, focusedLabelColor = Primary)
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                ItemKategori.entries.forEach { kategori ->
                    DropdownMenuItem(
                        text = { Text(kategori.label) },
                        onClick = { onSelect(kategori); expanded = false }
                    )
                }
            }
        }
        if (isError && errorMessage != null) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
        }
    }
}

@Composable
fun ImagePickerSection(
    selectedUri: String?,
    onImageSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.toString()?.let { onImageSelected(it) }
    }

    Column(modifier = modifier) {
        Text("Foto Barang (Opsional)", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.outline)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, if (selectedUri != null) Primary else MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .background(if (selectedUri != null) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant)
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (selectedUri != null) {
                AsyncImage(
                    model = selectedUri,
                    contentDescription = "Foto Barang",
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        Text("Ganti Foto", color = Color.White, style = MaterialTheme.typography.labelMedium)
                    }
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tap untuk memilih foto", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                    Text("dari galeri", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}
