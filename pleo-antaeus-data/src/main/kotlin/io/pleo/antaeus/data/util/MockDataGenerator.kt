package io.pleo.antaeus.data.util

import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import java.math.BigDecimal

class MockDataGenerator {
    companion object {
        fun createInvoice(
            id: Int = 1,
            customerId: Int = 1,
            value: Long = 100000,
            currency: Currency = Currency.DKK,
            status: InvoiceStatus = InvoiceStatus.PENDING
        ) = Invoice(
            id,
            customerId,
            Money(BigDecimal.valueOf(value), currency),
            status,
        )
    }
}