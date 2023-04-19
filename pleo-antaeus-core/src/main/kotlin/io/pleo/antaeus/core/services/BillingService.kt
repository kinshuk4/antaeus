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
        logger.info("fetching invoice" + id)
        val invoice = invoiceService.fetch(id)
        logger.error("fetched invoice" + id)
        logger.error(paymentProvider.charge(invoice).toString())
        if (paymentProvider.charge(invoice)) {
            logger.info("doing update xxxx")
            return invoiceService.updateStatusById(id, InvoiceStatus.PAID)
        }
        throw InvoicePaymentFailed(id)
    }

}
