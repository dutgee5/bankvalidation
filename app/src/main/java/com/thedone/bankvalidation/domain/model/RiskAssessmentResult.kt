package com.thedone.bankvalidation.domain.model

//risk output

data class RiskAssessmentResult(
    val customerId: String,
    val probabilityOfDefault: Double, // PD (0.0 ile 1.0 arası risk olasılığı)
    val riskSegment: String,          // A, B, C, D (Düşükten Yükseğe Risk)
    val isApproved: Boolean           // Validasyon / Karar Kriteri
)
