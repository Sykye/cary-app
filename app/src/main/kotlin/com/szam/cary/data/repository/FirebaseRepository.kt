package com.szam.cary.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.szam.cary.data.model.*
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseRepository : CaryRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance(
        "https://android-security-carryapp-default-rtdb.asia-southeast1.firebasedatabase.app"
    ).reference
    private val storage = FirebaseStorage.getInstance()

    private val usersRef = db.child("users")
    private val lostItemsRef = db.child("lost_items")
    private val foundItemsRef = db.child("found_items")
    private val claimsRef = db.child("claims")

    // ───── HELPER ─────

    private suspend fun uploadImageIfNeeded(uriString: String?, folder: String): String? {
        if (uriString.isNullOrBlank()) return null
        if (uriString.startsWith("http")) return uriString
        return try {
            val uri = Uri.parse(uriString)
            val ref = storage.reference.child("$folder/${UUID.randomUUID()}.jpg")
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }

    // ───── AUTH ─────

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return Result.failure(Exception("Login gagal"))
            val snapshot = usersRef.child(uid).get().await()
            val user = snapshot.getValue(User::class.java)
                ?: return Result.failure(Exception("Data user tidak ditemukan"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Email atau password salah"))
        }
    }

    override suspend fun register(nama: String, email: String, password: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return Result.failure(Exception("Registrasi gagal"))
            val newUser = User(id = uid, nama = nama, email = email, password = "")
            usersRef.child(uid).setValue(newUser).await()
            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Registrasi gagal"))
        }
    }

    override suspend fun getCurrentUser(): User? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            usersRef.child(uid).get().await().getValue(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun updateProfile(user: User): Result<User> {
        return try {
            val fotoUrl = uploadImageIfNeeded(user.fotoProfil, "profile_photos")
            val updatedUser = user.copy(fotoProfil = fotoUrl, password = "")
            usersRef.child(updatedUser.id).setValue(updatedUser).await()
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Gagal update profil"))
        }
    }

    // ───── LOST ITEMS ─────

    override suspend fun getLostItems(): List<LostItem> {
        return try {
            val snapshot = lostItemsRef.get().await()
            snapshot.children.mapNotNull { it.getValue(LostItem::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getLostItemById(id: String): LostItem? {
        return try {
            lostItemsRef.child(id).get().await().getValue(LostItem::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getLostItemsByUser(userId: String): List<LostItem> {
        return try {
            val snapshot = lostItemsRef.orderByChild("userId").equalTo(userId).get().await()
            snapshot.children.mapNotNull { it.getValue(LostItem::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addLostItem(item: LostItem): Result<LostItem> {
        return try {
            val id = lostItemsRef.push().key ?: UUID.randomUUID().toString()
            val fotoUrl = uploadImageIfNeeded(item.fotoUri, "lost_items")
            val newItem = item.copy(id = id, fotoUri = fotoUrl)
            lostItemsRef.child(id).setValue(newItem).await()
            Result.success(newItem)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Gagal menambah barang hilang"))
        }
    }

    override suspend fun updateLostItem(item: LostItem): Result<LostItem> {
        return try {
            val fotoUrl = uploadImageIfNeeded(item.fotoUri, "lost_items")
            val updatedItem = item.copy(fotoUri = fotoUrl)
            lostItemsRef.child(item.id).setValue(updatedItem).await()
            Result.success(updatedItem)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Gagal update barang hilang"))
        }
    }

    override suspend fun deleteLostItem(id: String): Result<Unit> {
        return try {
            lostItemsRef.child(id).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Gagal menghapus barang hilang"))
        }
    }

    // ───── FOUND ITEMS ─────

    override suspend fun getFoundItems(): List<FoundItem> {
        return try {
            val snapshot = foundItemsRef.get().await()
            snapshot.children.mapNotNull { it.getValue(FoundItem::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getFoundItemById(id: String): FoundItem? {
        return try {
            foundItemsRef.child(id).get().await().getValue(FoundItem::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getFoundItemsByUser(userId: String): List<FoundItem> {
        return try {
            val snapshot = foundItemsRef.orderByChild("userId").equalTo(userId).get().await()
            snapshot.children.mapNotNull { it.getValue(FoundItem::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addFoundItem(item: FoundItem): Result<FoundItem> {
        return try {
            val id = foundItemsRef.push().key ?: UUID.randomUUID().toString()
            val fotoUrl = uploadImageIfNeeded(item.fotoUri, "found_items")
            val newItem = item.copy(id = id, fotoUri = fotoUrl)
            foundItemsRef.child(id).setValue(newItem).await()
            Result.success(newItem)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Gagal menambah barang temuan"))
        }
    }

    override suspend fun updateFoundItem(item: FoundItem): Result<FoundItem> {
        return try {
            val fotoUrl = uploadImageIfNeeded(item.fotoUri, "found_items")
            val updatedItem = item.copy(fotoUri = fotoUrl)
            foundItemsRef.child(item.id).setValue(updatedItem).await()
            Result.success(updatedItem)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Gagal update barang temuan"))
        }
    }

    override suspend fun deleteFoundItem(id: String): Result<Unit> {
        return try {
            foundItemsRef.child(id).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Gagal menghapus barang temuan"))
        }
    }

    // ───── CLAIMS ─────

    override suspend fun getClaims(): List<Claim> {
        return try {
            val snapshot = claimsRef.get().await()
            snapshot.children.mapNotNull { it.getValue(Claim::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getClaimsByUser(userId: String): List<Claim> {
        return try {
            val snapshot = claimsRef.orderByChild("userId").equalTo(userId).get().await()
            snapshot.children.mapNotNull { it.getValue(Claim::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addClaim(claim: Claim): Result<Claim> {
        return try {
            val id = claimsRef.push().key ?: UUID.randomUUID().toString()
            val newClaim = claim.copy(id = id)
            claimsRef.child(id).setValue(newClaim).await()
            Result.success(newClaim)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Gagal mengajukan klaim"))
        }
    }

    override suspend fun updateClaimStatus(claimId: String, status: ClaimStatus): Result<Claim> {
        return try {
            claimsRef.child(claimId).child("status").setValue(status.name).await()
            val updated = claimsRef.child(claimId).get().await()
                .getValue(Claim::class.java)
                ?: return Result.failure(Exception("Klaim tidak ditemukan"))
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Gagal update status klaim"))
        }
    }

    // ───── SEARCH & MATCH ─────

    override suspend fun getMatchSuggestions(lostItem: LostItem): List<FoundItem> {
        return getFoundItems().filter { found ->
            val sameKategori = found.kategori == lostItem.kategori
            val similarName = found.namaBarang.contains(lostItem.namaBarang, ignoreCase = true) ||
                    lostItem.namaBarang.contains(found.namaBarang, ignoreCase = true)
            val sameLocation = found.lokasi.contains(lostItem.lokasi, ignoreCase = true) ||
                    lostItem.lokasi.contains(found.lokasi, ignoreCase = true)
            sameKategori || similarName || sameLocation
        }
    }

    override suspend fun searchItems(
        query: String,
        kategori: ItemKategori?,
        status: ItemStatus?
    ): Pair<List<LostItem>, List<FoundItem>> {
        val allLost = getLostItems()
        val allFound = getFoundItems()

        val lostFiltered = allLost.filter { item ->
            val matchQuery = query.isEmpty() ||
                    item.namaBarang.contains(query, ignoreCase = true) ||
                    item.lokasi.contains(query, ignoreCase = true) ||
                    item.deskripsi.contains(query, ignoreCase = true)
            val matchKategori = kategori == null || item.kategori == kategori
            val matchStatus = status == null || item.status == status
            matchQuery && matchKategori && matchStatus
        }

        val foundFiltered = allFound.filter { item ->
            val matchQuery = query.isEmpty() ||
                    item.namaBarang.contains(query, ignoreCase = true) ||
                    item.lokasi.contains(query, ignoreCase = true) ||
                    item.deskripsi.contains(query, ignoreCase = true)
            val matchKategori = kategori == null || item.kategori == kategori
            val matchStatus = status == null || item.status == status
            matchQuery && matchKategori && matchStatus
        }

        return Pair(lostFiltered, foundFiltered)
    }
}