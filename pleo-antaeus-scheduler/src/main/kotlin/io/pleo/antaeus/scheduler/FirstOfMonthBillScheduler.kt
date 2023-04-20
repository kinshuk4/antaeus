package io.pleo.antaeus.scheduler

import io.pleo.antaeus.core.messaging.MessageProducer
import io.pleo.antaeus.core.messaging.Topics
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.util.factory.SchedulerFactory
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
//private const val cronExpr = "0 0 1 * *"
private const val cronExpr = "*/5 * * * *"

class FirstOfMonthBillScheduler(private val invoiceService: InvoiceService, private val messageProducer: MessageProducer) {
    fun start() {
        SchedulerFactory.createScheduledTask(cronExpr, func = {
            requestToPayInvoice()
        })
    }
    fun requestToPayInvoice() {
        logger.info("Requesting the bills on 1st of the month")
        invoiceService.fetchPending().map { invoice ->
            logger.info("Requesting invoice '$invoice.id")
            messageProducer.sendMessage(Topics.pendingInvoices, invoice.id, invoice.id.toString())
            invoiceService.updateStatusById(invoice.id, InvoiceStatus.REQUESTED)
        }

    }
}
