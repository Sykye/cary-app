package com.szam.cary.data.model

import java.util.UUID

data class User(
    val id: String = UUID.randomUUID().toString(),
    val nama: String = "",
    val email: String = "",
    val fotoProfil: String? = null,
    val password: String = ""
)

enum class ItemStatus(val label: String) {
    HILANG("Hilang"),
    DITEMUKAN("Ditemukan"),
    DALAM_KLAIM("Dalam Klaim"),
    SUDAH_DIKEMBALIKAN("Sudah Dikembalikan")
}

enum class ItemKategori(val label: String) {
    HP("HP"),
    LAPTOP("Laptop"),
    DOMPET("Dompet"),
    TAS("Tas"),
    KUNCI("Kunci"),
    KARTU_IDENTITAS("Kartu Identitas"),
    DOKUMEN("Dokumen"),
    AKSESORIS("Aksesoris"),
    LAINNYA("Lainnya")
}

enum class ClaimStatus(val label: String) {
    MENUNGGU("Menunggu"),
    DISETUJUI("Disetujui"),
    DITOLAK("Ditolak")
}

data class LostItem(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val namaBarang: String = "",
    val kategori: ItemKategori = ItemKategori.LAINNYA,
    val lokasi: String = "",
    val tanggal: String = "",
    val deskripsi: String = "",
    val fotoUri: String? = null,
    val status: ItemStatus = ItemStatus.HILANG
)

data class FoundItem(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val namaBarang: String = "",
    val kategori: ItemKategori = ItemKategori.LAINNYA,
    val lokasi: String = "",
    val tanggal: String = "",
    val deskripsi: String = "",
    val fotoUri: String? = null,
    val status: ItemStatus = ItemStatus.DITEMUKAN
)

data class Claim(
    val id: String = UUID.randomUUID().toString(),
    val itemId: String = "",
    val itemNama: String = "",
    val itemType: String = "found", // "lost" or "found"
    val userId: String = "",
    val alasan: String = "",
    val status: ClaimStatus = ClaimStatus.MENUNGGU,
    val tanggal: String = ""
)

data class MatchResult(
    val lostItem: LostItem,
    val foundItem: FoundItem,
    val matchScore: Int // 0-100
)
