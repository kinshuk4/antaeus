package io.pleo.antaeus.scheduler

import io.pleo.antaeus.core.services.BillingService
import mu.KotlinLogging

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class FirstOfMonthBillScheduler(val billingService: BillingService) {
    //    @Scheduled(cron = "0 0 1 * *")
    @Scheduled(fixedRate = 5000)
    fun requestPayment() {
        logger.info("Requesting the bills on 1st of the month")
        billingService.billInvoices();
    }
}