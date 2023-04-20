package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.data.util.MockDataGenerator.Companion.createInvoice
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Collections.emptyList

class InvoiceServiceTest {
    private var dal = mockk<AntaeusDal> {
        every { fetchInvoice(404) } returns null
    }

    private var invoiceService = InvoiceService(dal = dal)

    @Test
    fun `will throw if invoice is not found`() {
        assertThrows<InvoiceNotFoundException> {
            invoiceService.fetch(404)
        }
    }

    @Test
    fun `will get zero pending invoices when there are none`() {
        // Given
        dal = mockk {
            every { fetchPendingInvoices() } returns emptyList()
        }
        invoiceService = InvoiceService(dal = dal)

        // When
        val pendingInvoices = invoiceService.fetchPending()

        // Then the pending ids we get is empty list
        val emptyList : List<Invoice>  = emptyList()
        assert(pendingInvoices == emptyList)
    }

    @Test
    fun `will get pending invoices when there are some`() {
        // Given

        val singleInvoice = createInvoice()
        val pendingInvoiceList = listOf(singleInvoice)
        dal = mockk {
            every { fetchPendingInvoices() } returns pendingInvoiceList
        }
        invoiceService = InvoiceService(dal = dal)

        // When
        val pendingInvoices = invoiceService.fetchPending()

        // Then the pending ids we get is empty list
        assert(pendingInvoices == pendingInvoiceList)
    }

    @Test
    fun `will get update invoice the status change requested`() {
        // Given
        val singleInvoice = createInvoice()
        val expectedInvoice = createInvoice(status = InvoiceStatus.PAID)
        dal = mockk {
            every { updateInvoiceStatusById(singleInvoice.id, InvoiceStatus.PAID) } returns expectedInvoice
        }
        invoiceService = InvoiceService(dal = dal)

        // When
        val updatedInvoice = invoiceService.updateStatusById(singleInvoice.id, InvoiceStatus.PAID)

        // Then the pending ids we get is empty list
        assertEquals(expectedInvoice, updatedInvoice)
    }
}
