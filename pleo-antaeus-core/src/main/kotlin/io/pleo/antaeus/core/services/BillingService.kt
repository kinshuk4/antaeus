package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
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

        try {
            val invoice = invoiceService.fetch(id)
            val isSuccess = paymentProvider.charge(invoice)
            if (isSuccess) {
                return invoiceService.updateStatusById(id, InvoiceStatus.PAID)
            } else {
                throw InvoicePaymentFailed(id)
            }
        } catch (ex: Exception) {
            // if exception is InvoiceNotFoundException, we cannot save in DB
            if (ex !is InvoiceNotFoundException) {
                invoiceService.updateStatusById(id, InvoiceStatus.FAILED, ex.toString())
            }

            throw ex
        }
    }

}
