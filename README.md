# Cary

Aplikasi Android untuk melaporkan dan mencari **barang hilang** serta **barang temuan**. Cary menghubungkan pemilik barang dengan penemu melalui fitur pencarian, pencocokan barang, dan pengajuan klaim.

![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.09.02-4285F4?logo=jetpackcompose&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-Auth%20%7C%20RTDB%20%7C%20Storage-FFCA28?logo=firebase&logoColor=black)
![License](https://img.shields.io/badge/License-MIT-green)

---

## Tentang Aplikasi

Cary adalah aplikasi *lost & found* berbasis Android yang dibangun dengan Kotlin dan Jetpack Compose. Pengguna dapat membuat laporan barang hilang, mengunggah barang temuan, mencari barang berdasarkan kategori/status, serta mengajukan klaim kepemilikan. Seluruh data disimpan di Firebase (Authentication, Realtime Database, dan Cloud Storage).

- **Package:** `com.szam.cary`
- **Min SDK:** 26 (Android 8.0)
- **Target / Compile SDK:** 35 (Android 15)
- **Bahasa UI:** Indonesia

## Fitur

- **Autentikasi**
  - Registrasi dan login dengan email & password (Firebase Authentication)
  - Logout dan edit nama profil
- **Barang Hilang**
  - Tambah, lihat detail, edit, dan hapus laporan
  - Unggah foto barang
- **Barang Temuan**
  - Tambah, lihat detail, edit, dan hapus laporan
  - Unggah foto barang
- **Klaim Barang**
  - Ajukan klaim dengan alasan kepemilikan
  - Riwayat klaim pengguna
- **Pencarian & Filter**
  - Cari berdasarkan nama, lokasi, atau deskripsi
  - Filter berdasarkan kategori dan status barang
- **Pencocokan Barang**
  - Rekomendasi barang temuan yang mirip dengan barang hilang (berdasarkan kategori, nama, dan lokasi)
- **Beranda**
  - Statistik ringkas: hilang, ditemukan, dan sudah dikembalikan
  - Daftar laporan terbaru
- **Profil**
  - Informasi akun dan edit nama

## Tech Stack

| Kategori | Teknologi |
|---|---|
| Bahasa | Kotlin 2.0.21 |
| UI | Jetpack Compose, Material 3, Material Icons Extended |
| Navigasi | Navigation Compose 2.8.2 |
| State Management | ViewModel, StateFlow, Coroutines |
| Backend | Firebase Authentication, Realtime Database, Cloud Storage |
| Image Loading | Coil 2.7.0 |
| Build | Gradle 8.9, AGP 8.5.2, Kotlin DSL, Version Catalog |
| Min / Target SDK | 26 / 35 |

## Struktur Proyek

```text
Cary/
├── app/
│   ├── google-services.json.example     # contoh konfigurasi Firebase
│   └── src/
│       ├── main/
│       │   ├── kotlin/com/szam/cary/
│       │   │   ├── data/
│       │   │   │   ├── datasource/      # data contoh (DummyDataSource)
│       │   │   │   ├── model/           # User, LostItem, FoundItem, Claim
│       │   │   │   └── repository/      # CaryRepository, Firebase, Dummy
│       │   │   ├── ui/
│       │   │   │   ├── components/      # komponen Compose reusable
│       │   │   │   ├── navigation/      # NavGraph & NavRoutes
│       │   │   │   ├── screen/          # 16 layar aplikasi
│       │   │   │   └── theme/           # warna & tipografi
│       │   │   ├── utils/
│       │   │   ├── viewmodel/           # Auth, LostItem, FoundItem, Claim, Search
│       │   │   └── MainActivity.kt
│       │   └── res/
│       ├── test/                        # unit test
│       └── androidTest/                 # instrumented test
├── gradle/
│   ├── libs.versions.toml               # version catalog
│   └── wrapper/
├── database.rules.json                  # aturan Realtime Database
├── storage.rules                        # aturan Cloud Storage
├── firebase.json                        # konfigurasi Firebase CLI
├── .firebaserc
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Skema Data Firebase

### Realtime Database

```text
users/{uid}
  ├── id, nama, email, fotoProfil
lost_items/{itemId}
  ├── id, userId, namaBarang, kategori, lokasi,
  │   tanggal, deskripsi, fotoUri, status
found_items/{itemId}
  ├── id, userId, namaBarang, kategori, lokasi,
  │   tanggal, deskripsi, fotoUri, status
claims/{claimId}
  ├── id, itemId, itemNama, itemType, userId,
  │   alasan, status, tanggal
```

**Enum yang digunakan:**

- `ItemKategori`: `HP`, `LAPTOP`, `DOMPET`, `TAS`, `KUNCI`, `KARTU_IDENTITAS`, `DOKUMEN`, `AKSESORIS`, `LAINNYA`
- `ItemStatus`: `HILANG`, `DITEMUKAN`, `DALAM_KLAIM`, `SUDAH_DIKEMBALIKAN`
- `ClaimStatus`: `MENUNGGU`, `DISETUJUI`, `DITOLAK`

### Cloud Storage

```text
profile_photos/{uuid}.jpg
lost_items/{uuid}.jpg
found_items/{uuid}.jpg
```

## Prasyarat

- Android Studio (disarankan versi terbaru)
- JDK 17 atau lebih baru (proyek ini diuji dengan JDK 21)
- Android SDK Platform 35
- Akun Firebase dan project Firebase aktif
- (Opsional) [Firebase CLI](https://firebase.google.com/docs/cli) untuk deploy rules

## Cara Menjalankan

### 1. Clone repository

```bash
git clone https://github.com/Sykye/cary-app.git
cd cary-app
```

### 2. Siapkan project Firebase

1. Buat project baru di [Firebase Console](https://console.firebase.google.com/).
2. Tambahkan aplikasi **Android** dengan package name `com.szam.cary`.
3. Aktifkan **Authentication → Sign-in method → Email/Password**.
4. Buat **Realtime Database** (contoh region: `asia-southeast1`).
5. Aktifkan **Cloud Storage**.
6. Unduh file `google-services.json`.

### 3. Pasang konfigurasi Firebase

Salin file yang sudah diunduh ke folder `app/`:

```text
app/google-services.json
```

> File ini **tidak di-commit** ke repository (lihat `.gitignore`). Gunakan `app/google-services.json.example` sebagai referensi struktur file.

Jika URL Realtime Database milikmu berbeda, sesuaikan di:

```text
app/src/main/kotlin/com/szam/cary/data/repository/FirebaseRepository.kt
```

```kotlin
private val db = FirebaseDatabase.getInstance(
    "https://<project-id>-default-rtdb.<region>.firebasedatabase.app"
).reference
```

### 4. Deploy Firebase Rules (opsional, disarankan)

Rules tersedia di `database.rules.json` dan `storage.rules`. Bisa juga menyalin isinya secara manual ke tab **Rules** di Firebase Console.

```bash
npm install -g firebase-tools
firebase login
firebase use --add          # pilih project Firebase milikmu
firebase deploy --only database,storage
```

### 5. Build dan jalankan

Buka folder proyek di Android Studio, tunggu Gradle sync, lalu klik **Run**.

Atau lewat terminal:

```bash
# Linux / macOS
./gradlew assembleDebug

# Windows
gradlew.bat assembleDebug
```

APK debug dihasilkan di:

```text
app/build/outputs/apk/debug/app-debug.apk
```

> Jika build lewat terminal dan Android SDK belum terdeteksi, buat file `local.properties` di root proyek (file ini sudah di-ignore):
>
> ```properties
> # Windows
> sdk.dir=C\:\\Users\\<nama-user>\\AppData\\Local\\Android\\Sdk
>
> # Linux / macOS
> sdk.dir=/Users/<nama-user>/Library/Android/sdk
> ```
>
> Alternatif: set environment variable `ANDROID_HOME` ke lokasi Android SDK.

## Testing

```bash
# Unit test
./gradlew test

# Instrumented test (butuh emulator/perangkat)
./gradlew connectedAndroidTest
```

## Keamanan & Catatan

- `app/google-services.json` di-ignore dan **tidak boleh** di-commit. File ini berisi API key dan ID project Firebase.
- API key Firebase sebaiknya dibatasi di Google Cloud Console (restrict berdasarkan package name dan SHA-1 aplikasi).
- Rules di repository ini bersifat dasar:
  - Semua pengguna yang sudah login dapat **membaca** data barang dan klaim.
  - **Tulis/ubah/hapus** hanya boleh dilakukan oleh pemilik data (`userId`).
  - Semua pengguna login dapat mengunggah foto ke folder Storage. Untuk produksi, pertimbangkan menyertakan `uid` pada path upload agar rules bisa lebih ketat.
- Field `password` pada model `User` tidak menyimpan password asli; password dikelola sepenuhnya oleh Firebase Authentication.
- `DummyRepository` dan `DummyDataSource` tersedia untuk data contoh, tetapi ViewModel saat ini menggunakan `FirebaseRepository`.

## Lisensi

Proyek ini dilisensikan di bawah [MIT License](LICENSE).

## Author

**Sykye**

- GitHub: [@Sykye](https://github.com/Sykye)
