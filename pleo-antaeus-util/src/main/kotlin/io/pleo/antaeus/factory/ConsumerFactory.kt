package io.pleo.antaeus.factory

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.Serializer
import java.util.*

private fun <K, V> createConsumer(
    bootStrapServer: String,
    keySerializer: Serializer<K>,
    valueSerializer: Serializer<V>,
    consumerGroup: String
): Consumer<K, V> {
    val props = Properties()
    props["bootstrap.servers"] = bootStrapServer
    props["group.id"] = consumerGroup
    props["key.deserializer"] = keySerializer
    props["value.deserializer"] = valueSerializer
    return KafkaConsumer<K, V>(props)
}