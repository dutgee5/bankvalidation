package com.thedone.bankvalidation.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loan_applications")
data class LoanApplicationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val customerId: String,
    val monthlyIncome: Double,
    val totalDebt: Double,
    val creditScore: Int,
    val pastDueDays: Int,
    val calculatedPd: Double,
    val riskSegment: String,
    val applicationDate: Long,
)
