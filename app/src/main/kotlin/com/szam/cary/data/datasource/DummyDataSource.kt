package com.szam.cary.data.datasource

import com.szam.cary.data.model.*

object DummyDataSource {

    val users = mutableListOf(
        User(id = "user1", nama = "Szam Khae", email = "szam@kampus.ac.id", password = "password123"),
        User(id = "user2", nama = "Siti Rahayu", email = "siti@kampus.ac.id", password = "password123"),
        User(id = "user3", nama = "Budi Santoso", email = "budi@kampus.ac.id", password = "password123")
    )

    val lostItems = mutableListOf(
        LostItem(
            id = "lost1",
            userId = "user1",
            namaBarang = "iPhone 14 Pro",
            kategori = ItemKategori.HP,
            lokasi = "Perpustakaan Pusat",
            tanggal = "2024-01-15",
            deskripsi = "HP warna hitam dengan case transparan, hilang saat di perpustakaan lantai 2",
            status = ItemStatus.HILANG
        ),
        LostItem(
            id = "lost2",
            userId = "user2",
            namaBarang = "Dompet Coklat",
            kategori = ItemKategori.DOMPET,
            lokasi = "Kantin Mahasiswa",
            tanggal = "2024-01-16",
            deskripsi = "Dompet kulit warna coklat berisi KTM dan uang tunai",
            status = ItemStatus.DALAM_KLAIM
        ),
        LostItem(
            id = "lost3",
            userId = "user1",
            namaBarang = "Laptop ASUS",
            kategori = ItemKategori.LAPTOP,
            lokasi = "Ruang Kelas B201",
            tanggal = "2024-01-17",
            deskripsi = "Laptop ASUS warna silver, ada stiker di bagian belakang",
            status = ItemStatus.SUDAH_DIKEMBALIKAN
        ),
        LostItem(
            id = "lost4",
            userId = "user3",
            namaBarang = "Kunci Motor",
            kategori = ItemKategori.KUNCI,
            lokasi = "Parkiran Motor Gedung A",
            tanggal = "2024-01-18",
            deskripsi = "Kunci motor Honda dengan gantungan kunci warna merah",
            status = ItemStatus.HILANG
        ),
        LostItem(
            id = "lost5",
            userId = "user2",
            namaBarang = "KTM / Kartu Tanda Mahasiswa",
            kategori = ItemKategori.KARTU_IDENTITAS,
            lokasi = "Gedung Rektorat",
            tanggal = "2024-01-19",
            deskripsi = "KTM atas nama Siti Rahayu NIM 2021001234",
            status = ItemStatus.HILANG
        )
    )

    val foundItems = mutableListOf(
        FoundItem(
            id = "found1",
            userId = "user2",
            namaBarang = "iPhone 14 Pro",
            kategori = ItemKategori.HP,
            lokasi = "Perpustakaan Pusat Lantai 2",
            tanggal = "2024-01-15",
            deskripsi = "Menemukan HP iPhone berwarna hitam di kursi dekat rak buku",
            status = ItemStatus.DALAM_KLAIM
        ),
        FoundItem(
            id = "found2",
            userId = "user3",
            namaBarang = "Tas Ransel Biru",
            kategori = ItemKategori.TAS,
            lokasi = "Aula Serbaguna",
            tanggal = "2024-01-16",
            deskripsi = "Tas ransel warna biru tua berisi buku dan alat tulis",
            status = ItemStatus.DITEMUKAN
        ),
        FoundItem(
            id = "found3",
            userId = "user1",
            namaBarang = "Dompet Hitam",
            kategori = ItemKategori.DOMPET,
            lokasi = "Kantin Mahasiswa",
            tanggal = "2024-01-16",
            deskripsi = "Dompet warna hitam berisi beberapa kartu dan uang",
            status = ItemStatus.SUDAH_DIKEMBALIKAN
        ),
        FoundItem(
            id = "found4",
            userId = "user2",
            namaBarang = "Kunci dengan Gantungan Merah",
            kategori = ItemKategori.KUNCI,
            lokasi = "Parkiran Motor",
            tanggal = "2024-01-18",
            deskripsi = "Kunci motor ditemukan di area parkiran, ada gantungan kunci warna merah",
            status = ItemStatus.DITEMUKAN
        ),
        FoundItem(
            id = "found5",
            userId = "user3",
            namaBarang = "Kartu Mahasiswa",
            kategori = ItemKategori.KARTU_IDENTITAS,
            lokasi = "Lobby Gedung Rektorat",
            tanggal = "2024-01-19",
            deskripsi = "KTM mahasiswi ditemukan di lantai lobby gedung rektorat",
            status = ItemStatus.DITEMUKAN
        )
    )

    val claims = mutableListOf(
        Claim(
            id = "claim1",
            itemId = "found1",
            itemNama = "iPhone 14 Pro",
            itemType = "found",
            userId = "user1",
            alasan = "Itu HP saya, saya kehilangan di perpustakaan pada tanggal 15 Januari. HP saya berwarna hitam dengan case transparan dan ada wallpaper foto keluarga.",
            status = ClaimStatus.MENUNGGU,
            tanggal = "2024-01-15"
        ),
        Claim(
            id = "claim2",
            itemId = "found3",
            itemNama = "Dompet Hitam",
            itemType = "found",
            userId = "user2",
            alasan = "Dompet itu milik saya. Di dalamnya ada KTM saya dan foto keluarga sebagai tanda kepemilikan.",
            status = ClaimStatus.DISETUJUI,
            tanggal = "2024-01-16"
        )
    )
}
