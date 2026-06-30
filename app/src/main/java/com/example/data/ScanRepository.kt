package com.example.data

import kotlinx.coroutines.flow.Flow

class ScanRepository(private val scanItemDao: ScanItemDao) {
    val allScans: Flow<List<ScanItem>> = scanItemDao.getAllScans()

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
