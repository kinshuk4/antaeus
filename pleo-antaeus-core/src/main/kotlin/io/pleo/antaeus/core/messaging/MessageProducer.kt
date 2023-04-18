package io.pleo.antaeus.core.messaging

import io.pleo.antaeus.core.exceptions.InvalidTopicException
import io.pleo.antaeus.factory.ProducerFactory
import mu.KotlinLogging
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.Serializer
import java.util.*

private val logger = KotlinLogging.logger {}

class MessageProducer() {

    var hashMap: HashMap<String, Producer<Any, Any>> = HashMap<String, Producer<Any, Any>>()

    fun <K : Any, V : Any> createProducer(
        topicName: String, bootStrapServer: String,
        keySerializer: Serializer<K>,
        valueSerializer: Serializer<V>
    ) {
        val producer =
            ProducerFactory.createProducer(bootStrapServer, keySerializer, valueSerializer) as Producer<Any, Any>
        hashMap.put(topicName, producer);
    }

    fun <K, V> sendMessage(topicName: String, key: K, message: V) {
        val producer = hashMap.get(topicName) ?: throw InvalidTopicException(topicName)
        val castedProducer = producer as Producer<K, V>
        castedProducer.send(ProducerRecord<K, V>(topicName, key, message))
    }

}


