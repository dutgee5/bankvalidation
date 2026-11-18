package com.thedone.bankvalidation.domain.model


// feature set
data class LoanApplication(
    val customerId: String,
    val monthlyIncome: Double,
    val totalDebt: Double,
    val creditScore: Int,      // Findeks/Kredi notu
    val pastDueDays: Int,       //toplam gecikme gün sayısı
)
