package com.thedone.bankvalidation.domain.usecase

import com.thedone.bankvalidation.domain.model.LoanApplication
import com.thedone.bankvalidation.domain.model.RiskAssessmentResult
import kotlin.math.exp

class CalculateProbabilityOfDefaultUseCase {

    operator fun invoke(application: LoanApplication): RiskAssessmentResult {

        //Debt-to-Income Ratio
        val annualIncome = application.monthlyIncome * 12
        val debtRatio = if (annualIncome > 0) application.totalDebt / annualIncome else 1.0

        val intercept = -1.5 // Sabit terim
        val creditScoreCoefficient = -0.002 // Kredi notu arttıkça risk düşer (-)
        val debtRatioCoefficient = 1.2       // Borç oranı arttıkça risk artar (+)
        val pastDueCoefficient = 0.05        // Gecikme günü arttıkça risk artar (+)

        val zScore = intercept +
                (application.creditScore * creditScoreCoefficient) +
                (debtRatio * debtRatioCoefficient) +
                (application.pastDueDays * pastDueCoefficient)

        val pd = 1.0 / (1.0 + exp(-zScore))
        val clampedPd = pd.coerceIn(0.0, 1.0)

        val (segment, approved) = when {
            clampedPd < 0.05 -> "Segment A (Very Low Risk)" to true
            clampedPd < 0.15 -> "Segment B (Low Risk)" to true
            clampedPd < 0.35 -> "Segment C (Medium Risk - Conditional Approval)" to false
            else -> "Segment D (High Risk - Rejection)" to false
        }

        return RiskAssessmentResult(
            customerId = application.customerId,
            probabilityOfDefault = clampedPd,
            riskSegment = segment,
            isApproved = approved
        )
    }
}