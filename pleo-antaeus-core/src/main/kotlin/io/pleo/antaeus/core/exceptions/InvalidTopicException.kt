package io.pleo.antaeus.core.exceptions


class InvalidTopicException(topicName: String) :
    Exception("Producer for topic name '$topicName' does not exist")
