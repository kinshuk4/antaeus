package io.pleo.antaeus.messaging.consumer.invoice

import com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr
import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.services.BillingService
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.data.util.MockDataGenerator
import io.pleo.antaeus.models.InvoiceStatus
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
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
    fun `given a non existing invoice, it should fail with InvoiceNotFoundException reason`() {
        // Given
        val singleInvoice = MockDataGenerator.createInvoice()
        dal = mockk {
            every { fetchInvoice(singleInvoice.id) } returns null
            every {
                updateInvoiceStatusById(
                    singleInvoice.id,
                    InvoiceStatus.PAID
                )
            } returns MockDataGenerator.createInvoice(
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
        assertTrue(output.trim().contains("InvoiceNotFoundException: Invoice '1' was not found"))
        assertTrue(output.trim().contains("Failed to process invoice '1'. Moving it to dead letter queue"))
    }


    @Test
    fun `given an existing invoice, it should fail with InvoiceNotFoundException reason`() {
        // Given
        val singleInvoice = MockDataGenerator.createInvoice()
        dal = mockk {
            every { fetchInvoice(singleInvoice.id) } returns null
            every {
                updateInvoiceStatusById(
                    singleInvoice.id,
                    InvoiceStatus.FAILED,
                    any()
                )
            } returns MockDataGenerator.createInvoice(
                status = InvoiceStatus.FAILED
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
        assertTrue(output.trim().contains("InvoiceNotFoundException: Invoice '1' was not found"))
        assertTrue(output.trim().contains("Failed to process invoice '1'. Moving it to dead letter queue"))
    }

    @Test
    fun `given a non existing invoice, it should fail with InvoicePaymentFailed reason`() {
        // Given
        val singleInvoice = MockDataGenerator.createInvoice()
        dal = mockk {
            every { fetchInvoice(singleInvoice.id) } returns singleInvoice
            every {
                updateInvoiceStatusById(
                    singleInvoice.id,
                    InvoiceStatus.FAILED,
                    any()
                )
            } returns MockDataGenerator.createInvoice(
                status = InvoiceStatus.FAILED
            )
        }
        invoiceService = InvoiceService(dal)
        paymentProvider = mockk<PaymentProvider> {
            every { charge(any()) } returns false
        }
        val billingService = BillingService(paymentProvider, invoiceService)
        val pendingInvoicesConsumer = PendingInvoicesConsumer("localhost:9092", countDownLatch, billingService, 3)
        // when
        val output = tapSystemErr {
            pendingInvoicesConsumer.handleInvoice(singleInvoice.id)
        }
        // then
        assertTrue(output.trim().contains("Billing the invoice '1'"))
        assertTrue(output.trim().contains("InvoicePaymentFailed: Invoice '1' couldnt not be paid"))
        assertTrue(output.trim().contains("Failed to process invoice '1'. Moving it to dead letter queue"))
    }


    @Test
    fun `given non-existing Customer, it should fail with CustomerNotFoundException reason`() {
        // Given
        val singleInvoice = MockDataGenerator.createInvoice()
        dal = mockk {
            every { fetchInvoice(singleInvoice.id) } returns singleInvoice
            every {
                updateInvoiceStatusById(
                    singleInvoice.id,
                    InvoiceStatus.FAILED,
                    any()
                )
            } returns MockDataGenerator.createInvoice(
                status = InvoiceStatus.FAILED
            )
        }
        invoiceService = InvoiceService(dal)
        paymentProvider = mockk<PaymentProvider> {
            every { charge(any()) }.throws(CustomerNotFoundException(singleInvoice.customerId))
        }
        val billingService = BillingService(paymentProvider, invoiceService)
        val pendingInvoicesConsumer = PendingInvoicesConsumer("localhost:9092", countDownLatch, billingService, 3)
        // when
        val output = tapSystemErr {
            pendingInvoicesConsumer.handleInvoice(singleInvoice.id)
        }
        // then
        assertTrue(output.trim().contains("Billing the invoice '1'"))
        assertTrue(output.trim().contains("CustomerNotFoundException: Customer '1' was not found"))
        assertTrue(output.trim().contains("Failed to process invoice '1'. Moving it to dead letter queue"))
    }

    @Test
    fun `given mismatch between currency of invoice and customer, it should fail with CurrencyMismatchException reason`() {
        // Given
        val singleInvoice = MockDataGenerator.createInvoice()
        dal = mockk {
            every { fetchInvoice(singleInvoice.id) } returns singleInvoice
            every {
                updateInvoiceStatusById(
                    singleInvoice.id,
                    InvoiceStatus.FAILED,
                    any()
                )
            } returns MockDataGenerator.createInvoice(
                status = InvoiceStatus.FAILED
            )
        }
        invoiceService = InvoiceService(dal)
        paymentProvider = mockk<PaymentProvider> {
            every { charge(any()) }.throws(CurrencyMismatchException(singleInvoice.id, singleInvoice.customerId))
        }
        val billingService = BillingService(paymentProvider, invoiceService)
        val pendingInvoicesConsumer = PendingInvoicesConsumer("localhost:9092", countDownLatch, billingService, 3)
        // when
        val output = tapSystemErr {
            pendingInvoicesConsumer.handleInvoice(singleInvoice.id)
        }
        // then
        assertTrue(output.trim().contains("Billing the invoice '1'"))
        assertTrue(
            output.trim()
                .contains("CurrencyMismatchException: Currency of invoice '1' does not match currency of customer '1'")
        )
        assertTrue(output.trim().contains("Failed to process invoice '1'. Moving it to dead letter queue"))
    }

    @Test
    fun `given network issues, it should fail with NetworkException reason, but should be retried and succeed`() {
        // Given
        val singleInvoice = MockDataGenerator.createInvoice()
        dal = mockk {
            every { fetchInvoice(singleInvoice.id) } returns singleInvoice
            every {
                updateInvoiceStatusById(
                    singleInvoice.id,
                    InvoiceStatus.FAILED,
                    any()
                )
            } returns MockDataGenerator.createInvoice(
                status = InvoiceStatus.FAILED
            )
            every {
                updateInvoiceStatusById(
                    singleInvoice.id,
                    InvoiceStatus.PAID
                )
            } returns MockDataGenerator.createInvoice(
                status = InvoiceStatus.PAID
            )
        }
        invoiceService = InvoiceService(dal)
        paymentProvider = mockk<PaymentProvider> {
            every { charge(any()) } throws NetworkException() andThenThrows NetworkException() andThen true
        }
        val billingService = BillingService(paymentProvider, invoiceService)
        val pendingInvoicesConsumer = PendingInvoicesConsumer("localhost:9092", countDownLatch, billingService, 3)
        // when
        val output = tapSystemErr {
            pendingInvoicesConsumer.handleInvoice(singleInvoice.id)
        }
        // then
        assertTrue(output.trim().contains("Billing the invoice '1'"))
        assertTrue(
            output.trim().contains("NetworkException: A network error happened please try again.'. Attempt: '1'")
        )
        assertTrue(
            output.trim().contains("NetworkException: A network error happened please try again.'. Attempt: '2'")
        )
        assertFalse(output.trim().contains("Failed to process invoice '1'. Moving it to dead letter queue"))
    }

    @Test
    fun `given network issues for more than maxRetries, it should fail with NetworkException reason, but should be retried and still fail`() {
        // Given
        val singleInvoice = MockDataGenerator.createInvoice()
        dal = mockk {
            every { fetchInvoice(singleInvoice.id) } returns singleInvoice
            every {
                updateInvoiceStatusById(
                    singleInvoice.id,
                    InvoiceStatus.FAILED,
                    any()
                )
            } returns MockDataGenerator.createInvoice(
                status = InvoiceStatus.FAILED
            )
        }
        invoiceService = InvoiceService(dal)
        paymentProvider = mockk<PaymentProvider> {
            every { charge(any()) } throws NetworkException() andThenThrows NetworkException() andThenThrows NetworkException()
        }
        val billingService = BillingService(paymentProvider, invoiceService)
        val pendingInvoicesConsumer = PendingInvoicesConsumer("localhost:9092", countDownLatch, billingService, 3)
        // when
        val output = tapSystemErr {
            pendingInvoicesConsumer.handleInvoice(singleInvoice.id)
        }
        // then

        assertTrue(output.trim().contains("Billing the invoice '1'"))
        assertTrue(
            output.trim().contains("NetworkException: A network error happened please try again.'. Attempt: '1'")
        )
        assertTrue(
            output.trim().contains("NetworkException: A network error happened please try again.'. Attempt: '2'")
        )
        assertTrue(output.trim().contains("Failed to process invoice '1'. Moving it to dead letter queue"))
    }
}