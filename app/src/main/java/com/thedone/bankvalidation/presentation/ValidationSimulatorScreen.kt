package com.thedone.bankvalidation.presentation

import androidx.compose.foundation.layout. *
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3. *
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ValidationSimulatorScreen(viewModel: ValidationViewModel) {
    val applications by viewModel.latestApplications.collectAsState()
    val result = viewModel.currentResult



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Bankacılık Risk & Validasyon Simülatörü",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 1. Slider Paneli (Parametre Ayarları)
        Text(text = "Kredi Notu: ${viewModel.creditScore.toInt()}")
        Slider(
            value = viewModel.creditScore,
            onValueChange = { viewModel.updateCreditScore(it) },
            valueRange = 0f..1900f
        )

        Text(text = "Aylık Gelir: ₺${viewModel.monthlyIncome.toInt()}")
        Slider(
            value = viewModel.monthlyIncome,
            onValueChange = { viewModel.updateMonthlyIncome(it) },
            valueRange = 5000f..200000f
        )

        Text(text = "Geçmiş Gecikme Günü: ${viewModel.pastDueDays.toInt()} gün")
        Slider(
            value = viewModel.pastDueDays,
            onValueChange = { viewModel.updatePastDueDays(it) },
            valueRange = 0f..90f
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Anlık Model Çıktısı (PD ve Segment)
        result?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Temerrüt Olasılığı (PD): ${
                            String.format(
                                "%.2f",
                                it.probabilityOfDefault * 100
                            )
                        }%"
                    )
                    Text(text = "Risk Segmenti: ${it.riskSegment}")
                    Text(text = "Karar: ${if (it.isApproved) "ONAYLANDI" else "REDDEDİLDİ"}")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.saveApplicationToDb() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Simülasyonu Veritabanına Kaydet")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Kayıtlı Başvurular (Window Functions Listesi)",
            style = MaterialTheme.typography.titleMedium
        )

        // 3. Veritabanından Gelen Başvurular Listesi
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(applications) { app ->
                ListItem(
                    headlineContent = { Text(text = "Müşteri: ${app.customerId} - Skor: ${app.creditScore}") },
                    supportingContent = {
                        Text(
                            text = "PD: ${
                                String.format(
                                    "%.2f",
                                    app.calculatedPd * 100
                                )
                            }% | ${app.riskSegment}"
                        )
                    }
                )
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            }
        }
    }
}