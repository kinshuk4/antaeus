package io.pleo.antaeus.consumer

import io.pleo.antaeus.core.messaging.MessageProducer
import io.pleo.antaeus.core.messaging.Topics
import io.pleo.antaeus.core.services.InvoiceService
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
//private const val cronExpr = "0 0 1 * *"
private const val cronExpr = "*/5 * * * *"

class FirstOfMonthBillScheduler(private val invoiceService: InvoiceService, private val messageProducer: MessageProducer) {
    fun start() {
        logger.info("Requesting the bills on 1st of the month")
        invoiceService.fetchPending().map {
                invoice -> messageProducer.sendMessage(Topics.pendingInvoices, invoice.id, invoice.id.toString())
        }

    }
}
