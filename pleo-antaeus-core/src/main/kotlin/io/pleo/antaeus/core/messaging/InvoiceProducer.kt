package io.pleo.antaeus.core.messaging

import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.common.serialization.IntegerSerializer
import org.apache.kafka.common.serialization.StringSerializer

class InvoiceProducer(messageProducer: MessageProducer) {
    private var producer: Producer<Int, String>
    init {
        producer = messageProducer.createProducer<Int, String> (Topics.pendingInvoices, IntegerSerializer::class.java, StringSerializer::class.java)
    }

}