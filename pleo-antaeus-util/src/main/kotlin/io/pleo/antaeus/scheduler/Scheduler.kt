package io.pleo.antaeus.scheduler

import dev.inmo.krontab.doInfinityTz
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import mu.KotlinLogging
import java.util.concurrent.Executors

private val logger = KotlinLogging.logger {}

class Scheduler {
    companion object {
        fun createScheduledTask(cronExpr: String, func: () -> Unit) {
            val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
            val scope = CoroutineScope(dispatcher)

            scope.launch {
                doInfinityTz(cronExpr) {
                    logger.info("Starting scheduler with cron expression: $cronExpr ....")
                    func()
                }
            }
        }
    }
}