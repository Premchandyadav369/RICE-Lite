package com.example.data

import kotlinx.coroutines.flow.Flow

class ScanRepository(private val scanItemDao: ScanItemDao) {
    val allScans: Flow<List<ScanItem>> = scanItemDao.getAllScans()
    val cropDiseaseScans: Flow<List<ScanItem>> = scanItemDao.getCropDiseaseScans()

    fun searchScans(query: String): Flow<List<ScanItem>> {
        return scanItemDao.searchScans(query)
    }

    suspend fun getScanById(id: Int): ScanItem? {
        return scanItemDao.getScanById(id)
    }

    suspend fun insertScan(scanItem: ScanItem): Long {
        return scanItemDao.insertScan(scanItem)
    }

    suspend fun deleteScanById(id: Int) {
        scanItemDao.deleteScanById(id)
    }

    suspend fun clearHistory() {
        scanItemDao.clearHistory()
    }
}
