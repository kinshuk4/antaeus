package io.pleo.antaeus.factory

import io.pleo.antaeus.core.services.BillingService
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
//private const val cronExpr = "0 0 1 * *"
private const val cronExpr = "*/5 * * * *"

class FirstOfMonthBillScheduler(private val billingService: BillingService) {
    fun start() {
        logger.info("Requesting the bills on 1st of the month")
        SchedulerFactory.createScheduledTask(cronExpr, func = {
            billingService.billInvoices();
        })

    }
}
