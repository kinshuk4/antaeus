package io.pleo.antaeus.core.exceptions

class InvoicePaymentFailed(id: Int) : Exception("Invoice '$id' couldnt not be paid")
