package com.szam.cary.data.repository

import com.szam.cary.data.model.*

interface CaryRepository {
    // Auth
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(nama: String, email: String, password: String): Result<User>
    suspend fun getCurrentUser(): User?
    suspend fun logout()
    suspend fun updateProfile(user: User): Result<User>

    // Lost Items
    suspend fun getLostItems(): List<LostItem>
    suspend fun getLostItemById(id: String): LostItem?
    suspend fun getLostItemsByUser(userId: String): List<LostItem>
    suspend fun addLostItem(item: LostItem): Result<LostItem>
    suspend fun updateLostItem(item: LostItem): Result<LostItem>
    suspend fun deleteLostItem(id: String): Result<Unit>

    // Found Items
    suspend fun getFoundItems(): List<FoundItem>
    suspend fun getFoundItemById(id: String): FoundItem?
    suspend fun getFoundItemsByUser(userId: String): List<FoundItem>
    suspend fun addFoundItem(item: FoundItem): Result<FoundItem>
    suspend fun updateFoundItem(item: FoundItem): Result<FoundItem>
    suspend fun deleteFoundItem(id: String): Result<Unit>

    // Claims
    suspend fun getClaims(): List<Claim>
    suspend fun getClaimsByUser(userId: String): List<Claim>
    suspend fun addClaim(claim: Claim): Result<Claim>
    suspend fun updateClaimStatus(claimId: String, status: ClaimStatus): Result<Claim>

    // Matching
    suspend fun getMatchSuggestions(lostItem: LostItem): List<FoundItem>
    suspend fun searchItems(query: String, kategori: ItemKategori?, status: ItemStatus?): Pair<List<LostItem>, List<FoundItem>>
}
