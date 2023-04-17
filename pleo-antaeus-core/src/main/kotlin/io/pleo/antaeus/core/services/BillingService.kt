package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.InvoiceStatus
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class BillingService(
    private val paymentProvider: PaymentProvider,
    private val invoiceService: InvoiceService
) {
    // DONE - Add code e.g. here
    fun billInvoices() {
        val pendingInvoices = invoiceService.fetchPending()
        logger.info("Paying bills")
        pendingInvoices.forEach {

            if (paymentProvider.charge(it)) {
                invoiceService.updateStatusById(it.id, InvoiceStatus.PAID)
            }
        }
    }
}
