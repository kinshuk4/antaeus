package io.pleo.antaeus.util.factory

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.common.serialization.Serializer
import java.util.*


class ConsumerFactory {
    companion object {
        fun <K, V> createConsumer(
            bootStrapServer: String,
            keySerializer: Any,
            valueSerializer: Any,
            consumerGroup: String,
            autoOffsetResetConfig: String = "earliest"
        ): Consumer<K, V> {
            val props = Properties()
            props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootStrapServer
            props[ConsumerConfig.GROUP_ID_CONFIG] = consumerGroup
            props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = keySerializer
            props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = valueSerializer
            props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = autoOffsetResetConfig
            return KafkaConsumer<K, V>(props)
        }

    }
}