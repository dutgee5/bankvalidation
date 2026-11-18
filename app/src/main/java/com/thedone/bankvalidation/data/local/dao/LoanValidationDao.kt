package com.thedone.bankvalidation.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thedone.bankvalidation.data.local.entity.LoanApplicationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanValidationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: LoanApplicationEntity)

    @Query(
        """
            SELECT * FROM (
            SELECT *, 
                   ROW_NUMBER() OVER(PARTITION BY customerId ORDER BY applicationDate DESC) as sira_no
            FROM loan_applications
        ) WHERE sira_no = 1
        """
    )
    fun getLatestApplicationsPerCustomer(): Flow<List<LoanApplicationEntity>>

    @Query(
        """
        SELECT 
            id,
            customerId,
            calculatedPd AS currentPd,
            LAG(calculatedPd, 1, calculatedPd) OVER(PARTITION BY customerId ORDER BY applicationDate ASC) AS previousPd,
            calculatedPd - LAG(calculatedPd, 1, calculatedPd) OVER(PARTITION BY customerId ORDER BY applicationDate ASC) AS riskChange,
            applicationDate
        FROM loan_applications
    """
    )
    fun getCustomerRiskTrend(): Flow<List<RiskTrendDto>>
}

// Trend analizi için veri taşıma sınıfı (DTO)
data class RiskTrendDto(
    val id: Long,
    val customerId: String,
    val currentPd: Double,
    val previousPd: Double,
    val riskChange: Double, // Pozitifse risk artmış demektir (Alarm!)
    val applicationDate: Long,
)