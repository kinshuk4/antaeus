package io.pleo.antaeus.consumer

import mu.KotlinLogging
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import java.util.*

private val logger = KotlinLogging.logger {}


class ProducerFactory {
    companion object {
        fun <K, V> createProducer(
            bootStrapServer: String,
            keySerializer: Object,
            valueSerializer: Object
        ): Producer<K, V> {
            val props = Properties()
            props["bootstrap.servers"] = bootStrapServer
            props["key.serializer"] = keySerializer
            props["value.serializer"] = valueSerializer
            return KafkaProducer<K, V>(props)
        }
    }
}