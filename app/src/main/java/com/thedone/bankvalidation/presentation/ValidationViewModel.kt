package com.thedone.bankvalidation.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thedone.bankvalidation.data.local.dao.LoanValidationDao
import com.thedone.bankvalidation.data.local.entity.LoanApplicationEntity
import com.thedone.bankvalidation.domain.model.LoanApplication
import com.thedone.bankvalidation.domain.model.RiskAssessmentResult
import com.thedone.bankvalidation.domain.usecase.CalculateProbabilityOfDefaultUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class ValidationViewModel(
    private val dao: LoanValidationDao,
    private val calculatePdUseCase: CalculateProbabilityOfDefaultUseCase,
) : ViewModel() {

    // UI Slider State value
    var creditScore by mutableStateOf(1400f)
        private set
    var monthlyIncome by mutableStateOf(45000f)
        private set
    var totalDebt by mutableStateOf(120000f)
        private set
    var pastDueDays by mutableStateOf(0f)
        private set


    var currentResult by mutableStateOf<RiskAssessmentResult?>(null)
        private set

    // latestApplication (Flow -> State)
    val latestApplications: StateFlow<List<LoanApplicationEntity>> =
        dao.getLatestApplicationsPerCustomer()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun updateCreditScore(value: Float) {
        creditScore = value
        recalculateModel()
    }

    fun updateMonthlyIncome(value: Float) {
        monthlyIncome = value
        recalculateModel()
    }

    fun updateTotalDebt(value: Float) {
        totalDebt = value
        recalculateModel()
    }

    fun updatePastDueDays(value: Float) {
        pastDueDays = value
        recalculateModel()
    }

    private fun recalculateModel() {
        val application = LoanApplication(
            customerId = "CUST-${UUID.randomUUID().toString().take(4)}",
            monthlyIncome = monthlyIncome.toDouble(),
            totalDebt = totalDebt.toDouble(),
            creditScore = creditScore.toInt(),
            pastDueDays = pastDueDays.toInt()
        )
        currentResult = calculatePdUseCase(application)
    }

    fun saveApplicationToDb() {
        val result = currentResult ?: return
        viewModelScope.launch {
            val entity = LoanApplicationEntity(
                customerId = result.customerId,
                monthlyIncome = monthlyIncome.toDouble(),
                totalDebt = totalDebt.toDouble(),
                creditScore = creditScore.toInt(),
                pastDueDays = pastDueDays.toInt(),
                calculatedPd = result.probabilityOfDefault,
                riskSegment = result.riskSegment,
                applicationDate = System.currentTimeMillis()
            )
            dao.insertApplication(entity)
        }
    }

}