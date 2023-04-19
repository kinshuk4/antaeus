package io.pleo.antaeus.core.messaging

import io.pleo.antaeus.core.exceptions.InvalidTopicException
import io.pleo.antaeus.consumer.ProducerFactory
import mu.KotlinLogging
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.*

private val logger = KotlinLogging.logger {}

class MessageProducer(private val bootStrapServer: String) {

    private val hashMap: HashMap<String, Producer<Any, Any>> = HashMap<String, Producer<Any, Any>>()

    fun <K : Any, V : Any> createProducer(
        topicName: String,
        keySerializer: Any,
        valueSerializer: Any
    ): Producer<K, V> {
        val producer =
            ProducerFactory.createProducer<K, V>(bootStrapServer, keySerializer as Object, valueSerializer as Object)
        val castedProducer = producer as Producer<Any, Any>
        hashMap.put(topicName, castedProducer);
        return producer
    }

    fun <K, V> sendMessage(topicName: String, key: K, message: V) {
        val producer = hashMap.get(topicName) ?: throw InvalidTopicException(topicName)
        val castedProducer = producer as Producer<K, V>
        castedProducer.send(ProducerRecord<K, V>(topicName, key, message))
    }

}


