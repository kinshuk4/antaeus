package io.pleo.antaeus.util.factory

import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import java.util.*

private val logger = KotlinLogging.logger {}


class ProducerFactory {
    companion object {
        fun <K, V> createProducer(
            bootStrapServer: String,
            keySerializer: Any,
            valueSerializer: Any
        ): Producer<K, V> {
            val props = Properties()
            props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootStrapServer
            props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = keySerializer
            props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = valueSerializer
            return KafkaProducer<K, V>(props)
        }
    }
}