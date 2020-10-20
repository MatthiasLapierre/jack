package com.matthiaslapierre.jack.utils

interface Factory<T> {
    fun create(): T
}