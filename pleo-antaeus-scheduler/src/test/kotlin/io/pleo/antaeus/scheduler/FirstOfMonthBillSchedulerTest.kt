package io.pleo.antaeus.scheduler

//import io.mockk.every
import io.mockk.mockk
//import io.pleo.antaeus.core.messaging.MessageProducer
//import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.data.AntaeusDal
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.Test
//import java.util.Collections.emptyList
//import com.github.stefanbirkner.systemlambda.SystemLambda.*
//import io.pleo.antaeus.data.InvoiceTable.status
//import io.pleo.antaeus.data.util.MockDataGenerator.Companion.createInvoice
//import io.pleo.antaeus.models.InvoiceStatus

class FirstOfMonthBillSchedulerTest {
    private var dal = mockk<AntaeusDal> ()
//    private var invoiceService = InvoiceService(dal = dal)
//    private var messageProducer = mockk<MessageProducer>() {
//        every { sendMessage<Int, String> (any(), any(), any()) }
//    }
//    var scheduler = FirstOfMonthBillScheduler(invoiceService, messageProducer = messageProducer)
//
//    @Test
//    fun `will process zero pending invoices when there are none`() {
//        // Given
//        dal = mockk {
//            every { fetchPendingInvoices() } returns emptyList()
//        }
//        invoiceService = InvoiceService(dal = dal)
//        scheduler = FirstOfMonthBillScheduler(invoiceService, messageProducer = messageProducer)
//
//
//        // When
//        val output = tapSystemErr {
//            scheduler.requestToPayInvoice()
//        }
//
//        // Then
//        assertTrue(output.trim().contains("Requesting the bills on 1st of the month"))
//        assertFalse(output.trim().contains("Requesting invoice "))
//    }
//
//    @Test
//    fun `will process pending invoices when there are some`() {
////        // Given
////        val singleInvoice = createInvoice()
////        val list = listOf(singleInvoice)
////        dal = mockk {
////            every { fetchPendingInvoices() } returns list
////            every { updateInvoiceStatusById(any(), any()) } returns createInvoice(status = InvoiceStatus.REQUESTED)
////        }
////        invoiceService = InvoiceService(dal = dal)
////        scheduler = FirstOfMonthBillScheduler(invoiceService, messageProducer = messageProducer)
////
////
////        // When
////        val output = tapSystemErr {
////            scheduler.requestToPayInvoice()
////        }
////
////        // Then
////        assertTrue(output.trim().contains("Requesting the bills on 1st of the month"))
////        assertFalse(output.trim().contains("Requesting invoice '$singleInvoice.id"))
//    }
}