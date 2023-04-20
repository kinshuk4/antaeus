package io.pleo.antaeus.core.services

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.data.util.MockDataGenerator.Companion.createInvoice
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.InvoiceStatus
import mu.KotlinLogging
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

private val logger = KotlinLogging.logger {}

class BillingServiceTest {
    private var dal = mockk<AntaeusDal>()
    private var invoiceService = InvoiceService(dal)
    private var paymentProvider = mockk<PaymentProvider> {
        every { charge(any()) } returns true
    }


    @AfterEach
    fun init() {
        clearAllMocks()
    }


    @Test
    fun `given an invoice, when will try settle a single invoice and return if the result is paid`() {
        // Given
        val singleInvoice = createInvoice()
        dal = mockk {
            every { fetchInvoice(singleInvoice.id) } returns singleInvoice
            every { updateInvoiceStatusById(singleInvoice.id, InvoiceStatus.PAID) } returns createInvoice(status = InvoiceStatus.PAID)
        }
        invoiceService = InvoiceService(dal)
        val billingService = BillingService(paymentProvider, invoiceService)
        // when
        val updatedInvoice = billingService.billInvoice(singleInvoice.id)

        // then
        Assertions.assertEquals(
            InvoiceStatus.PAID,
            updatedInvoice.status
        )
    }


}