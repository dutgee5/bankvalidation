package com.thedone.bankvalidation.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.thedone.bankvalidation.data.local.dao.LoanValidationDao
import com.thedone.bankvalidation.data.local.entity.LoanApplicationEntity

@Database(entities = [LoanApplicationEntity::class], version = 1, exportSchema = false)
abstract class BankDatabase : RoomDatabase() {
    abstract fun loanValidationDao(): LoanValidationDao
}