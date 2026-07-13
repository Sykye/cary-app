package com.szam.cary.data.repository

import com.szam.cary.data.datasource.DummyDataSource
import com.szam.cary.data.model.*
import kotlinx.coroutines.delay
import java.util.UUID

class DummyRepository : CaryRepository {

    private var currentUser: User? = null

    override suspend fun login(email: String, password: String): Result<User> {
        delay(800)
        val user = DummyDataSource.users.find {
            it.email.equals(email, ignoreCase = true) && it.password == password
        }
        return if (user != null) {
            currentUser = user
            Result.success(user)
        } else {
            Result.failure(Exception("Email atau password salah"))
        }
    }



    override suspend fun register(nama: String, email: String, password: String): Result<User> {
        delay(800)
        if (DummyDataSource.users.any { it.email.equals(email, ignoreCase = true) }) {
            return Result.failure(Exception("Email sudah terdaftar"))
        }
        val newUser = User(
            id = UUID.randomUUID().toString(),
            nama = nama,
            email = email,
            password = password
        )
        DummyDataSource.users.add(newUser)
        currentUser = newUser
        return Result.success(newUser)
    }

    override suspend fun getCurrentUser(): User? = currentUser

    override suspend fun logout() {
        currentUser = null
    }

    override suspend fun updateProfile(user: User): Result<User> {
        delay(500)
        val index = DummyDataSource.users.indexOfFirst { it.id == user.id }
        return if (index != -1) {
            DummyDataSource.users[index] = user
            currentUser = user
            Result.success(user)
        } else {
            Result.failure(Exception("User tidak ditemukan"))
        }
    }

    override suspend fun getLostItems(): List<LostItem> {
        delay(300)
        return DummyDataSource.lostItems.toList()
    }

    override suspend fun getLostItemById(id: String): LostItem? {
        delay(200)
        return DummyDataSource.lostItems.find { it.id == id }
    }

    override suspend fun getLostItemsByUser(userId: String): List<LostItem> {
        delay(300)
        return DummyDataSource.lostItems.filter { it.userId == userId }
    }

    override suspend fun addLostItem(item: LostItem): Result<LostItem> {
        delay(500)
        val newItem = item.copy(id = UUID.randomUUID().toString())
        DummyDataSource.lostItems.add(newItem)
        return Result.success(newItem)
    }

    override suspend fun updateLostItem(item: LostItem): Result<LostItem> {
        delay(500)
        val index = DummyDataSource.lostItems.indexOfFirst { it.id == item.id }
        return if (index != -1) {
            DummyDataSource.lostItems[index] = item
            Result.success(item)
        } else {
            Result.failure(Exception("Barang tidak ditemukan"))
        }
    }

    override suspend fun deleteLostItem(id: String): Result<Unit> {
        delay(400)
        val removed = DummyDataSource.lostItems.removeIf { it.id == id }
        return if (removed) Result.success(Unit)
        else Result.failure(Exception("Barang tidak ditemukan"))
    }

    override suspend fun getFoundItems(): List<FoundItem> {
        delay(300)
        return DummyDataSource.foundItems.toList()
    }

    override suspend fun getFoundItemById(id: String): FoundItem? {
        delay(200)
        return DummyDataSource.foundItems.find { it.id == id }
    }

    override suspend fun getFoundItemsByUser(userId: String): List<FoundItem> {
        delay(300)
        return DummyDataSource.foundItems.filter { it.userId == userId }
    }

    override suspend fun addFoundItem(item: FoundItem): Result<FoundItem> {
        delay(500)
        val newItem = item.copy(id = UUID.randomUUID().toString())
        DummyDataSource.foundItems.add(newItem)
        return Result.success(newItem)
    }

    override suspend fun updateFoundItem(item: FoundItem): Result<FoundItem> {
        delay(500)
        val index = DummyDataSource.foundItems.indexOfFirst { it.id == item.id }
        return if (index != -1) {
            DummyDataSource.foundItems[index] = item
            Result.success(item)
        } else {
            Result.failure(Exception("Barang tidak ditemukan"))
        }
    }

    override suspend fun deleteFoundItem(id: String): Result<Unit> {
        delay(400)
        val removed = DummyDataSource.foundItems.removeIf { it.id == id }
        return if (removed) Result.success(Unit)
        else Result.failure(Exception("Barang tidak ditemukan"))
    }

    override suspend fun getClaims(): List<Claim> {
        delay(300)
        return DummyDataSource.claims.toList()
    }

    override suspend fun getClaimsByUser(userId: String): List<Claim> {
        delay(300)
        return DummyDataSource.claims.filter { it.userId == userId }
    }

    override suspend fun addClaim(claim: Claim): Result<Claim> {
        delay(500)
        val newClaim = claim.copy(id = UUID.randomUUID().toString())
        DummyDataSource.claims.add(newClaim)
        return Result.success(newClaim)
    }

    override suspend fun updateClaimStatus(claimId: String, status: ClaimStatus): Result<Claim> {
        delay(400)
        val index = DummyDataSource.claims.indexOfFirst { it.id == claimId }
        return if (index != -1) {
            val updated = DummyDataSource.claims[index].copy(status = status)
            DummyDataSource.claims[index] = updated
            Result.success(updated)
        } else {
            Result.failure(Exception("Klaim tidak ditemukan"))
        }
    }

    override suspend fun getMatchSuggestions(lostItem: LostItem): List<FoundItem> {
        delay(400)
        return DummyDataSource.foundItems.filter { found ->
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
        delay(300)
        val lostFiltered = DummyDataSource.lostItems.filter { item ->
            val matchQuery = query.isEmpty() ||
                    item.namaBarang.contains(query, ignoreCase = true) ||
                    item.lokasi.contains(query, ignoreCase = true) ||
                    item.deskripsi.contains(query, ignoreCase = true)
            val matchKategori = kategori == null || item.kategori == kategori
            val matchStatus = status == null || item.status == status
            matchQuery && matchKategori && matchStatus
        }
        val foundFiltered = DummyDataSource.foundItems.filter { item ->
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
