package com.thedone.bankvalidation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Room
import com.thedone.bankvalidation.data.local.BankDatabase
import com.thedone.bankvalidation.domain.usecase.CalculateProbabilityOfDefaultUseCase
import com.thedone.bankvalidation.presentation.ValidationSimulatorScreen
import com.thedone.bankvalidation.presentation.ValidationViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = Room.databaseBuilder(
            applicationContext,
            BankDatabase::class.java,
            "bank_validation.db"
        ).build()


        // use di
        val dao = database.loanValidationDao()
        val calculatePdUseCase = CalculateProbabilityOfDefaultUseCase()

        val viewModel = ValidationViewModel(dao, calculatePdUseCase)


        setContent {
            ValidationSimulatorScreen(viewModel = viewModel)
        }
    }
}

