package io.pleo.antaeus.messaging.consumer.invoice

import com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr
import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.services.BillingService
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.data.util.MockDataGenerator
import io.pleo.antaeus.models.InvoiceStatus
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch

private val logger = KotlinLogging.logger {}

class PendingInvoicesConsumerTest {
    private var dal = mockk<AntaeusDal> ()
    private var invoiceService = InvoiceService(dal = dal)
    private var paymentProvider = mockk<PaymentProvider> {
        every { charge(any()) } returns true
    }
    private var billingService = BillingService(paymentProvider, invoiceService)
    private val countDownLatch = CountDownLatch(1)

    @Test
    fun `given an invoice, when will try settle a single invoice provided everything goes well`() {
        // Given
        val singleInvoice = MockDataGenerator.createInvoice()
        dal = mockk {
            every { fetchInvoice(singleInvoice.id) } returns singleInvoice
            every { updateInvoiceStatusById(singleInvoice.id, InvoiceStatus.PAID) } returns MockDataGenerator.createInvoice(
                status = InvoiceStatus.PAID
            )
        }
        invoiceService = InvoiceService(dal)
        val billingService = BillingService(paymentProvider, invoiceService)
        val pendingInvoicesConsumer = PendingInvoicesConsumer("localhost:9092", countDownLatch, billingService, 3)
        // when
        val output = tapSystemErr {
            pendingInvoicesConsumer.handleInvoice(singleInvoice.id)
        }
        // then
        assertTrue(output.trim().contains("Billing the invoice '1'"))
    }

    @Test
    fun `given an non existing invoice, it should fail with proper reason`() {
        // Given
        val singleInvoice = MockDataGenerator.createInvoice()
        dal = mockk {
            every { fetchInvoice(singleInvoice.id) } returns null
            every { updateInvoiceStatusById(singleInvoice.id, InvoiceStatus.PAID) } returns MockDataGenerator.createInvoice(
                status = InvoiceStatus.PAID
            )
        }
        invoiceService = InvoiceService(dal)
        val billingService = BillingService(paymentProvider, invoiceService)
        val pendingInvoicesConsumer = PendingInvoicesConsumer("localhost:9092", countDownLatch, billingService, 3)
        // when
        val output = tapSystemErr {
            pendingInvoicesConsumer.handleInvoice(singleInvoice.id)
        }
        // then
        assertTrue(output.trim().contains("Billing the invoice '1'"))
    }
}