package io.pleo.antaeus.factory

import mu.KotlinLogging
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.common.serialization.Serializer
import java.util.*

private val logger = KotlinLogging.logger {}


class ProducerFactory {
    companion object {
        fun <K, V> createProducer(
            bootStrapServer: String,
            keySerializer: Serializer<K>,
            valueSerializer: Serializer<V>
        ): Producer<K, V> {
            val props = Properties()
            props["bootstrap.servers"] = bootStrapServer
            props["key.serializer"] = keySerializer
            props["value.serializer"] = valueSerializer
            return KafkaProducer<K, V>(props)
        }
    }
}