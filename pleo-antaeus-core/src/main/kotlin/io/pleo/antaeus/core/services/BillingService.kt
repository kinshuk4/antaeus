package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.InvoicePaymentFailed
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class BillingService(
    private val paymentProvider: PaymentProvider,
    private val invoiceService: InvoiceService
) {
    // DONE - Add code e.g. here

    fun billInvoice(id: Int): Invoice {
        logger.info("Billing the invoice '$id'")
        var isSuccess = false
        var failureReason: java.lang.Exception? = null
        try {
            val invoice = invoiceService.fetch(id)
            isSuccess = paymentProvider.charge(invoice)
        } catch (ex: Exception) {
            failureReason = ex
        }

        if (isSuccess) {
            return invoiceService.updateStatusById(id, InvoiceStatus.PAID)
        }
        if (failureReason == null) {
            failureReason = InvoicePaymentFailed(id)
        }
        invoiceService.updateStatusById(id, InvoiceStatus.FAILED, failureReason.toString())

        throw failureReason
    }

}
