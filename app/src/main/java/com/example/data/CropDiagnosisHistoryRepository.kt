package com.example.data

import kotlinx.coroutines.flow.Flow

class CropDiagnosisHistoryRepository(private val dao: CropDiagnosisHistoryDao) {
    val allHistory: Flow<List<CropDiagnosisHistoryEntity>> = dao.getAllDiagnosisHistory()

    fun getByCrop(cropName: String): Flow<List<CropDiagnosisHistoryEntity>> {
        return dao.getDiagnosisByCrop(cropName)
    }

    fun getByPlot(plotName: String): Flow<List<CropDiagnosisHistoryEntity>> {
        return dao.getDiagnosisByPlot(plotName)
    }

    suspend fun getById(id: Int): CropDiagnosisHistoryEntity? {
        return dao.getDiagnosisById(id)
    }

    suspend fun insert(item: CropDiagnosisHistoryEntity): Long {
        return dao.insertDiagnosis(item)
    }

    suspend fun update(item: CropDiagnosisHistoryEntity) {
        dao.updateDiagnosis(item)
    }

    suspend fun deleteById(id: Int) {
        dao.deleteDiagnosisById(id)
    }

    suspend fun clearAll() {
        dao.clearAllHistory()
    }
}
