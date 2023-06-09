package io.pleo.antaeus.messaging.consumer.invoice

import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.messaging.Topics
import io.pleo.antaeus.core.services.BillingService
import io.pleo.antaeus.util.factory.ConsumerFactory
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.errors.WakeupException
import org.apache.kafka.common.serialization.IntegerDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.concurrent.CountDownLatch

private val logger = KotlinLogging.logger {}

class PendingInvoicesConsumer(
    private val bootstrapServer: String,
    private val countDownLatch: CountDownLatch,
    private val billingService: BillingService,
    private val maxRetries: Int
) :
    Runnable {
    private var consumer: Consumer<Int, String>

    init {
        consumer = ConsumerFactory.createConsumer<Int, String>(
            bootstrapServer,
            IntegerDeserializer::class.java,
            StringDeserializer::class.java,
            "pending-invoice-1"
        )
    }

    fun subscribe() {
        consumer.subscribe(mutableListOf(Topics.pendingInvoices))
    }

    override fun run() {
        try {
            while (true) {
                val records = consumer.poll(Duration.of(1000L, ChronoUnit.MILLIS))

                for (i in records) {
                    logger.info("Processing invoice with Key ${i.key()}, Partition ${i.partition()}, Value ${i.value()}, Offset ${i.offset()}")
                    handleInvoice(i.value().toInt())
                }
            }
        } catch (e: WakeupException) {
            logger.info("consumer has stopped, received shutdown signal.")
        } finally {
            consumer.close()
            countDownLatch.countDown()
        }
    }

    fun handleInvoice(i: Int) {
        var numRetries = 0
        var isError = true
        while (isError && numRetries < maxRetries) {

            numRetries++
            try {
                billingService.billInvoice(i)
                isError = false
            } catch (ex: Exception) {
                when (ex) {
                    is NetworkException -> {
                        logger.info("Unable to process invoice '$i' due to error: '$ex'. Attempt: '$numRetries'")
                        continue
                    }
                    is CurrencyMismatchException, is CustomerNotFoundException -> {
                        logger.error("Unable to process invoice '$i' due to error: '$ex'")
                        break
                    }
                    else -> {
                        logger.error("Unable to process invoice '$i' due to generic error: '$ex'")
                        break
                    }
                }

            }

        }
        if (isError) {
            logger.error("Failed to process invoice '$i'. Moving it to dead letter queue")
            // write to dead letter queue
        }


    }


    fun shutdown() {
        consumer.wakeup()
    }


}
